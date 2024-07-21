package tools.writer.csv;

import java.util.ArrayList;
import java.util.Objects;

import assist.base.FileToolBase;
import biology.amino.*;
import biology.descriptor.*;
import biology.protein.AminoChain;
import modules.descriptor.charge.Charge;
import regression.vectors.DataVector;
import tools.reader.EmptyDescriptorListException;

/**
 * Designed to write lists of protein chains to CSVs
 * Version 3.1 of CSVWriter, designed to be able to write customizable CSVs
 * 
 * @author Benjy Strauss
 * 
 */

//@SuppressWarnings("rawtypes")
public class DescriptorCSVWriter3 extends AbstractDescriptorCSVWriter {
	private static final long serialVersionUID = 1L;
	//how close are we to needing to run the garbage collector
	
	@SuppressWarnings("unused")
	private static int gc = 0;
	
	public DescriptorCSVWriter3() { }
	
	/**
	 * Adds a descriptor to the list:
	 * @param desc
	 */
	public void add(DescriptorType desc) {
		super.add(new Descriptor(desc));
	}
	
	/**
	 * Writes the data to a file
	 * @param saveFileName: name of the file to save to
	 * @param myChains: 
	 */
	public void writeData(String saveFileName, Iterable<AminoChain<?>> myChains) {
		Objects.requireNonNull(myChains, "No chains to write!");
		
		if(size() == 0) { throw new EmptyDescriptorListException(); }
		
		String outFileName;
		if(!saveFileName.startsWith(OUTPUT)) {
			outFileName = "output/"+saveFileName;
		} else {
			outFileName = saveFileName;
		}
		
		if(!saveFileName.endsWith(CSV)) { outFileName = outFileName+CSV; }
		
		ArrayList<String> fileLines = new ArrayList<String>();
		fileLines.add(makeHeader());
		//qp(fileLines);
		for(AminoChain<?> chain: myChains) {
			for(int index = 0; index < chain.size(); ++index) {
				if(chain.get(index) != null && chain.get(index).toChar() != '_') {
					fileLines.add(formLine(chain, index));
				}
			}
			if(repeatHeaderBetweenChains) { fileLines.add(makeHeader()); }
		}
		
		FileToolBase.writeFileLines(outFileName, fileLines);
	}
	
	/**
	 * 
	 * @param saveFileName
	 * @param data
	 */
	public void writeData(String saveFileName, DataVector<?>... data) {
		Objects.requireNonNull(data, "No data to write!");
		if(size() == 0) { throw new EmptyDescriptorListException(); }
		
		int maxDataLen = 0;
		for(DataVector<?> dv: data) {
			if(dv.size() > maxDataLen) { maxDataLen = dv.size(); }
		}
		
		String outFileName;
		if(!saveFileName.startsWith(OUTPUT)) {
			outFileName = "output/"+saveFileName;
		} else {
			outFileName = saveFileName;
		}
		
		if(!saveFileName.endsWith(CSV)) { outFileName = outFileName+CSV; }
		
		ArrayList<String> fileLines = new ArrayList<String>();
		for(int index = 0; index < data.length; ++index) {
			if(data[index].descriptor() == null) {
				qp("Warning!--No column descriptor for column'" + data[index] + "'!");
				qp("'" + data[index] + "' cannot be written to the file!");
				data[index] = null;
			} else {
				add(data[index].descriptor());
			}
		}
		
		fileLines.add(makeHeader());
		StringBuilder lineBuilder = new StringBuilder();
		for(int index = 0; index < maxDataLen; ++index) {
			lineBuilder.setLength(0);
			for(DataVector<?> vect: data) {
				if(vect != null) {
					lineBuilder.append(vect.get(index) + ",");
				}
			}
			lineBuilder.setLength(lineBuilder.length()-1);
			fileLines.add(lineBuilder.toString());
		}
		
		FileToolBase.writeFileLines(outFileName, fileLines);
	}
	
	/**
	 * Forms the CSV line
	 * @param chain: the chain
	 * @param index: the index into the chain
	 * @return: string that will be written to the CSV
	 */
	private String formLine(AminoChain<?> chain, int index) {
		StringBuilder lineBuilder = new StringBuilder();
		for(Metric col: this) {
			if(chain.get(index).residueType() == null) {
				qp("Warning: index " + index + " in chain " + chain.id() + " is null and will be skipped.");
				continue;
			}
			
			if(col instanceof Descriptor) {
				Descriptor desc = (Descriptor) col;
				lineBuilder.append(retrieveDescriptorData(chain, index, desc));
				
			} else if(col instanceof SecStructConfig) {
				SecStructConfig swi = (SecStructConfig) col;
				if(chain.get(index) instanceof Aminoid) {
					lineBuilder.append(AminoTools.aminoHasHomologue((Aminoid) chain.get(index), swi));
				} else {
					lineBuilder.append("N/A");
				}
			} else if(col instanceof Identifier) {
				Identifier mm = (Identifier) col;
				lineBuilder.append(retrieveIdentifierData(chain, index, mm));
				
			} else if(col instanceof VKPred) {
				VKPred pred = (VKPred) col;
				if(chain.get(index) instanceof Aminoid) {
					SecondarySimple ss = ((Aminoid) chain.get(index)).getVKPrediction(pred);
					lineBuilder.append(ss);
				} else {
					lineBuilder.append("N/A");
				}
			} else if(col instanceof TMBRecordedAtom) {
				TMBRecordedAtom atom = (TMBRecordedAtom) col;
				if(chain.get(index) instanceof Aminoid) {
					double charge = Charge.getVKPredictedCharge((Aminoid) chain.get(index), atom, false);
					lineBuilder.append(charge);
				}
			} else if (col instanceof PartialVK) {
				PartialVK pvk = (PartialVK) col;
				lineBuilder.append(pvk.getVKbat(chain, index));
			} else if (col instanceof SwitchClass) {
				SwitchClass swi = (SwitchClass) col;
				if(chain.get(index) instanceof Aminoid) {
					lineBuilder.append(swi.getRegressionValue((Aminoid) chain.get(index)));
				} else {
					lineBuilder.append("N/A");
				}
			}  else if (col instanceof ResAnnotation) {
				ResAnnotation resAnn = (ResAnnotation) col;
				lineBuilder.append(chain.get(index).hasAnnotation(resAnn) ? "1" : "0");
			}
			lineBuilder.append(",");
		}
		trimLastChar(lineBuilder);
		return lineBuilder.toString();
	}
	
	/**
	 * Makes and returns a header for a descriptor CSV File
	 * @return: header for the descriptor CSV File
	 */
	private String makeHeader() {
		StringBuilder headerBuilder = new StringBuilder();
		for(Metric type: this) {
			headerBuilder.append(type + ",");
		}
		trimLastChar(headerBuilder);
		return headerBuilder.toString();
	}
}
