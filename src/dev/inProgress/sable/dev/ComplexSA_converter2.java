package dev.inProgress.sable.dev;

import assist.util.LabeledList;
import dev.inProgress.sable.NetworkBase;
import install.DirectoryManager;
import utilities.LocalToolBase;

/**
 * Nearly perfect converter…
 * @author Benjamin Strauss
 *
 */

public class ComplexSA_converter2 extends LocalToolBase {
	public static final String[] FILES = { "Pfam_BP_all_0_4", "Pfam_BP_all_1_2",
			"Pfam_BP_all_2_6", "Pfam_BP_reg_all_0_12","Pfam_RP_all_1_8",
			"Pfam_BP_reg_all_1_10", "Pfam_RP_all_0_7","Pfam_RP_all_2_7",
			"Pfam_RP_reg_all_0_11"
	};
	
	public static final String DIRECTORY = "singleSA";
	
	public static final String SOURCES = "&Sources[";
	public static final String WEIGHTS = "&Weights[";
	
	public static void main(String[] args) {
		for(String filename: FILES) {
			convert(filename);
		}
		System.exit(0);
	}
	
	private static void convert(String filename) {
		String[] lines = getFileLines("dev/sable_v4_distr/"+DIRECTORY+"/"+filename+".c");
		LabeledList<String> output = new LabeledList<String>();
		LabeledList<String> code = new LabeledList<String>();
		int state = -1;
		int substate = 0;
		StringBuilder structLineBuilder = new StringBuilder();
		code.add("package dev.inProgress.sable."+DIRECTORY+";");
		code.add("");
		code.add("import dev.inProgress.sable.NetworkBase;\n"
				+ "import dev.inProgress.sable.Valid;\n"
				+ "import dev.inProgress.sable.pUnit;\n"
				+ "import setup.installation.DirectoryManager;");
		code.add("");
		
		for(String line: lines) {
			if(line.trim().length() == 0) {
				continue;
			} else if(line.trim().equals("static pUnit Sources[] =  {")) {
				state = 1;
				output.add(NetworkBase.UNITS);
				continue;
			} else if(line.trim().equals("static float Weights[] =  {")) {
				state = 2;
				output.add(NetworkBase.WEIGHTS);
				continue;
			} else if(line.contains("static UnitType Units") && !line.contains("];")) {
				state = 3;
				output.add(NetworkBase.STRUCTS);
				continue;
			} else if(line.startsWith("int net") ) {
				state = 4;
			} else if(line.contains("********************************/")) {
				state = 0;
				code.add(line);
				code.add("");
				addCode(filename, code);				
				continue;
			}
			
			switch(state) {
			case -1:
				code.add(line);
				break;
			case 1:
				line = line.replaceAll(", Units \\+ ", ",");
				line = line.replaceAll("Units \\+ ", "");
				if(line.endsWith(",")) {
					line = line.substring(0, line.length()-1);
				}
				if(!(line.contains("};") || line.contains("/*"))) {
					output.add(line);
				}	
				break;
			case 2:
				if(line.endsWith(",")) {
					line = line.substring(0, line.length()-1);
				}
				if(!(line.contains("};") || line.contains("/*"))) {
					output.add(line);
				}	
				break;
			case 3:
				if(line.contains("{ 0.0, 0.0, 0, NULL , NULL },")) {
					output.add("0.0,0.0,0,null,null");
				} else if(line.endsWith("{")) { 
				
				} else {
					switch(substate) {
					case 1:
						//qp("*\"" + line + "\"");
						structLineBuilder.append(line.replaceAll("\\s+", ""));
						break;
					case 2:
						//qp("\"" + line + "\"");
						line = line.trim().substring(SOURCES.length());
						line = line.replaceAll("\\[", "");
						line = line.replaceAll("\\]", "");
						structLineBuilder.append(line.replaceAll("\\s+", ""));
						break;
					case 3:
						//qp("\"" + line + "\"");
						line = line.trim().substring(SOURCES.length());
						line = line.replaceAll("\\[", "");
						line = line.replaceAll("\\]", "");
						structLineBuilder.append(line.replaceAll("\\s+", ""));
						trimLastChar(structLineBuilder);
						break;
					case 4:
						break;
					case 5:
						substate = 0;
						output.add(structLineBuilder.toString());
						structLineBuilder.setLength(0);
						break;
					}
					++substate;
				}
				break;
			case 4:
				if(line.trim().startsWith("static pUnit")) {
					code.add(process(line));
				} else if(line.trim().startsWith("enum") || line.trim().equals("{")) {
					
				} else if(line.trim().startsWith("static int Output")) {
					int equalsLoc = line.indexOf("=");
					code.add("\t\tint[] Output "+line.substring(equalsLoc));
				} else if(line.contains("sum = 0.0;")) {
					code.add("\t\t\tsum = (float) 0.0;");
				} else if(line.contains("int net(float *in, float *out, int init)")) {
					code.add("\tpublic int net(float[] in, float[] out, int init) {");
				} else if(line.trim().equals("return(OK);")) {
					code.add("\t\treturn Valid.OK.ordinal();");
				} else {
					line = line.replaceAll("->", ".");
					line = line.replaceAll("  ", "\t");
					line = "\t"+line;
					code.add(line);
				}
			}
		}
		code.add("}");
		
		writeFileLines(DirectoryManager.FILES_PREDICT_SABLE+"/"+filename+".txt", output);
		writeFileLines(filename+".java", code);
	}
	
	private static void addCode(String filename, LabeledList<String> code) {
		code.add("public class "+filename+" extends NetworkBase {");
		code.add("\tprivate static final String DATA_FILE = DirectoryManager.FILES_PREDICT_SABLE + \""+filename+".txt\";");
		code.add("\tprivate static boolean initialized = false;");
		code.add("");
		code.add("\n"
				+ "	static int[] Sources = null;\n"
				+ "	static float[] Weights = null;\n"
				+ "	static pUnit[] Units = null;");
		code.add("");
		code.add("	public "+filename+"() {\n"
				+ "		if(!initialized) {\n"
				+ "			Object[] temp = init(DATA_FILE);\n"
				+ "			Sources = (int[])   temp[0];\n"
				+ "			Weights = (float[]) temp[1];\n"
				+ "			Units   = (pUnit[]) temp[2];\n"
				+ "			initialized = true;\n"
				+ "		}\n"
				+ "	}");
		code.add("");
	}

	/**
	 * "static pUnit Output1[1] = {Units + 330};"
	 * @param line
	 * @return
	 */
	private static String process(String line) {
		//qp(line);
		line = line.trim();
		String[] tokens = line.split("\\s+");
		StringBuilder builder = new StringBuilder("\t\tpUnit[] ");
		//qp(tokens[3]);
		builder.append(tokens[2].substring(0,tokens[2].indexOf("[")));
		builder.append(" = pUnit.getUnits(Units, ");
		for(String token: tokens) {
			try {
				token = token.replaceAll("\\}", "");
				token = token.replaceAll(",", "");
				token = token.replaceAll(";", "");
				//qp(token);
				Integer.parseInt(token);
				builder.append(token + ",");
			} catch (NumberFormatException NFE) {
				
			}
		}
		trimLastChar(builder);
		builder.append(");");
		return builder.toString();
	}
	
}
