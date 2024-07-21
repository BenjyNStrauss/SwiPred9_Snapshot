package modules.encode.esm;

import java.io.File;
import java.util.List;
import java.util.Objects;

import assist.base.Assist;
import assist.exceptions.UnmappedEnumValueException;
import assist.script.PythonScript;
import assist.script.Script;
import assist.util.LabeledList;
import biology.molecule.FastaCrafter;
import biology.protein.AminoChain;
import biology.protein.ChainFactory;
import biology.protein.ProteinChain;
import install.DirectoryManager;
import project.ProteinDataset;
import system.Instruction;
import tools.writer.fasta.FastaWriter;
import utilities.LocalToolBase;

/**
 * Designed to act as a bridge with Facebook's ESM encoding mechanism
 * @author Benjamin Strauss
 * 
 * conda env list
 * conda create -n py_3_8 python=3.8
 * conda activate py_3_8
 * 
 * 
 * "python src-py/esm/scripts/extract.py esm1_t34_670M_UR50S files/tmp/esm_fasta.txt files/tmp --repr_layers 33 --include mean per_tok --truncate"
 * "python src-py/esm/scripts/extract.py esm2_t48_15B_UR50D  files/tmp/esm_fasta.txt files/tmp --repr_layers 47 --include mean per_tok"
 * "python src-py/esm/scripts/extract.py esm2_t36_3B_UR50D  files/tmp/esm_fasta.txt files/tmp --repr_layers 35 --include mean per_tok"
 * 
 * 
 */

@SuppressWarnings("unused")
public final class FacebookESM extends LocalToolBase {
	private static final File EXTRACT_PY	= new File("src-py/esm/scripts/extract.py");
	private static final File ESM_TRANSLATE = new File("src-py/esm-translate.py");
	private static final File ESM_PLAINTEXT = new File("files/tmp/esm-plaintext.txt");
	
	private static final String[] ENCODE_SCRIPT = {
			"", DirectoryManager.FILES_TMP+"/esm_fasta.txt", DirectoryManager.FILES_TMP,
			"--repr_layers", "", "--include", "mean", "per_tok"//, "--truncate"
	};
	
	private FacebookESM() { }
	
	public static void assign(Instruction instr, ProteinDataset... projects) {
		LabeledList<ProteinDataset> dataList = new LabeledList<ProteinDataset>();
		dataList.addAll(projects);
		assign(instr, projects);
	}
	
	/**
	 * 
	 * @param instr
	 * @param relevantProjects
	 */
	public static void assign(Instruction instr, List<ProteinDataset> relevantProjects) {
		ESM_Model model = null;
		try {
			//qp(instr.getFirstArgumentNamed(true, "-model"));
			model = ESM_Model.parse(instr.getFirstArgumentNamed(true, "-model"));
		} catch (UnmappedEnumValueException UEVE) {
			error("Error: Invalid or Unknown ESM Model.");
			return;
		} catch (NullPointerException NPE) {
			error("Error: No model specified.");
			return;
		}
		
		int layer = model.default_layer;
		if(instr.hasArgumentNamed(true, "layer")) {
			try {
				layer = Integer.parseInt(instr.getFirstArgumentNamed(true, "layer").trim());
			} catch (NumberFormatException NFE) {
				error("Error: Layer Unrecognized.");
				return;
			}
		}

		for(ProteinDataset pp: relevantProjects) {
			for(AminoChain<?> chain: pp) {
				assignESM(chain, model, layer, instr.override);
			}
		}
	}
	
	/**
	 * 
	 * @param chain
	 * @param model
	 * @return
	 */
	public static synchronized double[][] getEsmEncoding(AminoChain<?> chain, ESM_Model model) {
		return getEsmEncoding(chain, model, model.default_layer, true);
	}
	
	/**
	 * 
	 * @param chain
	 * @param model
	 * @param reextract
	 * @return
	 */
	public static synchronized double[][] getEsmEncoding(AminoChain<?> chain, ESM_Model model, boolean reextract) {
		return getEsmEncoding(chain, model, model.default_layer, reextract);
	}
	
	/**
	 * 
	 * @param chain
	 * @param model
	 * @param layer
	 * @param reextract
	 * @return
	 */
	public static synchronized double[][] getEsmEncoding(AminoChain<?> chain, ESM_Model model, int layer,
			boolean reextract) {
		Objects.requireNonNull(chain, "No chain specified.");
		Objects.requireNonNull(model, "No model specified.");
		ESM_PLAINTEXT.delete();
		String[] script = ENCODE_SCRIPT;
		
		boolean temp = FastaCrafter.filter_gap;
		FastaCrafter.filter_gap = true;
		FastaWriter.write(script[1], chain);
		String seq = chain.toSequence();
		FastaCrafter.filter_gap = temp;
		
		script[0] = model.toString();
		script[4] = ""+layer;
		String[] fastalines = getFileLines(script[1]);
		String pt_file = DirectoryManager.FILES_TMP+"/"+fastalines[0].substring(1)+".pt";
		
		if((!fileExists(pt_file)) || reextract) {
			qp("Running ESM encoding on chain: " + chain + ".");
			try {
				PythonScript.runPythonScript(EXTRACT_PY, script);
			} catch (assist.script.PythonScriptException PSE) {
				qerr("Script Error: "+getStr(script));
				PSE.printStackTrace();
				return null;
			}
		} else {
			qp("Using pre-extracted encoding for chain: " + chain + ".");
		}
		
		//qp("pt_file: " + pt_file);
		
		String pt_file_corrected = DirectoryManager.FILES_TMP+"/"+fastalines[0].substring(1, fastalines[0].indexOf('|'))+".pt";
		//qp("pt_file_corrected: " + pt_file_corrected);
		
		Script.runScript("mv", pt_file, pt_file_corrected);
		
		PythonScript.runPythonScript(ESM_TRANSLATE, pt_file_corrected, ""+layer);
		
		String[] filelines = getFileLines(ESM_PLAINTEXT);
		
		if(ESM_PLAINTEXT.length() == 0) {
			qerr("Error: no encoding for "+chain.id());
			return null;
		}
		
		int residuesWritten = seq.length();
		
		double[][] values = new double[residuesWritten][model.encodingLength()];
		//qp(filelines.length);
		
		for(int index = 0; index < filelines.length; ++index) {
			
			String[] str_values = filelines[index].split(",");
			for(int index2 = 0; index2 < str_values.length; ++index2) {
				try {
					values[index][index2] = Double.parseDouble(str_values[index2]);
				} catch (NumberFormatException NFE) {
					error("Error in parsing ESM value: \"" + str_values[index2] + "\" on line #"+index);
					values[index][index2] = Double.NaN;
				} catch (ArrayIndexOutOfBoundsException AIOOBE) {
					qp("Error wrong array length, expected "+model.encodingLength() + ", found " + str_values.length);
					System.exit(1);
				}
			}
		}
		
		return values;
	}
	
	/**
	 * 
	 * @param sequence
	 * @param model
	 * @return
	 */
	public static synchronized double[][] getEsmEncoding(String sequence, ESM_Model model) {
		return getEsmEncoding(sequence, model, model.default_layer);
	}
	
	/**
	 * 
	 * @param sequence
	 * @param model
	 * @param layer
	 * @return
	 */
	public static synchronized double[][] getEsmEncoding(String sequence, ESM_Model model, int layer) {
		Objects.requireNonNull(sequence, "No sequence specified.");
		Objects.requireNonNull(model, "No model specified.");
		ESM_PLAINTEXT.delete();
		String[] script = ENCODE_SCRIPT;
		
		if(sequence.contains("_")) {
			error("Warning: FacebookESM does not process null residues.  Nulls will be deleted");
			sequence = sequence.replaceAll("_", "");
		}
		
		FastaWriter.write(script[1], sequence);
		
		script[0] = model.toString();
		script[4] = ""+layer;
		qp("Running ESM encoding on sequence " + sequence + ".");
		PythonScript.runPythonScript(EXTRACT_PY, script);
		
		String[] fastalines = getFileLines(script[1]);
		String pt_file = DirectoryManager.FILES_TMP+"/"+fastalines[0].substring(1)+".pt";
		String pt_file_corrected = DirectoryManager.FILES_TMP+"/"+fastalines[0].substring(1, fastalines[0].indexOf('|'))+".pt";
		Script.runScript("mv", pt_file, pt_file_corrected);
		
		PythonScript.runPythonScript(ESM_TRANSLATE, pt_file_corrected, ""+layer);
		
		String[] filelines = getFileLines(ESM_PLAINTEXT);
		int residuesWritten = sequence.length();
		
		double[][] values = new double[residuesWritten][model.encodingLength()];
		//qp(filelines.length);
		
		for(int index = 0; index < filelines.length; ++index) {
			String[] str_values = filelines[index].split(",");
			for(int index2 = 0; index2 < str_values.length; ++index2) {
				try {
					values[index][index2] = Double.parseDouble(str_values[index2]);
				} catch (NumberFormatException NFE) {
					error("Error in parsing ESM value: \"" + str_values[index2] + "\" on line #"+index);
					values[index][index2] = Double.NaN;
				}
			}
		}
		
		return values;
	}
	
	/**
	 * 
	 * @param chain
	 * @param model
	 */
	public static synchronized void assignESM(AminoChain<?> chain, ESM_Model model) {
		assignESM(chain, model, model.default_layer, true);
	}
	
	/**
	 * 
	 * @param chain
	 * @param model
	 * @param reextract
	 */
	public static synchronized void assignESM(AminoChain<?> chain, ESM_Model model, boolean reextract) {
		assignESM(chain, model, model.default_layer, reextract);
	}
	
	/**
	 * 
	 * @param chain
	 * @param model
	 * @param layer
	 * @param reextract
	 */
	public static synchronized void assignESM(AminoChain<?> chain, ESM_Model model, int layer, boolean reextract) {
		double[][] encoding = getEsmEncoding(chain, model, layer, reextract);
		if(encoding == null) {
			error("ESM Embedding Assignment Failed");
			return;
		}
		
		int offset = 0;
		for(int index = 0; index < encoding.length; ++index) {
			int assignIndex = chain.convertIndex(index)+offset;
			if(chain.get(assignIndex) == null || chain.get(assignIndex).toChar() == '_') {
				++offset;
				--index;
			} else if(chain.get(assignIndex) == null) {
				//skip
			} else {
				//qp(chain.get(assignIndex).toChar() + ":" + encoding[index][0]);
				chain.get(assignIndex).setEncoding(model, encoding[index]);
			}
		}
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PythonScript.setPythonPath("/Users/bns/opt/anaconda3/bin/python");
		ProteinChain test4JJX = ChainFactory.makeRCSB("4JJX", "A", "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFEEPYESFDELEELYNKHIHDNAERRFVVEDAQKNLIGLVELIEINYIHRSAEFQIIIAPEHQGKGFARTLINRALDYSFTILNLHKIYLHVAVENPKAVHLYEECGFVEEGHLVEEFFINGRYQDVKRMYILQSKYLNRSE");
		//assignESM(test4JJX, ESM_Model.esm1_t34_670M_UR50S, true);
		assignESM(test4JJX, ESM_Model.esm2_t36_3B_UR50D, true);
		System.exit(0);
	}
	
	private static void test1() {
		PythonScript.setPythonPath("/Users/bns/miniconda3/bin/python");
		ProteinChain test4JJX = ChainFactory.makeRCSB("4JJX", "A", "SNAMNSQLTLRALERGDLRFIHNLNNNRNIMSYWFE");
		
		LabeledList<String> buffer = new LabeledList<String>();
		
		for(ESM_Model model: ESM_Model.values()) {
			qp("Model = "+model);
			assignESM(test4JJX, model, true);
			if(test4JJX.get(0).getEncoding(model) != null) {
				buffer.add("success: " + model);
			} else {
				buffer.add("fail:    " + model);
			}
		}
		qp("––––––––––––––––––––––––––––––––––––––––––––––––––––––");
		for(String str: buffer) {
			qp(str);
		}
		
		System.exit(0);
	}
}
