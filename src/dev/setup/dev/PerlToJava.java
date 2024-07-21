package dev.setup.dev;

import java.util.ArrayList;

import utilities.LocalToolBase;

/**
 * Designed to aid in translating Perl code to Java
 * This is NOT a substitute for manual translation, just a way to cut down on busywork
 * @author Benjy Strauss
 *
 */

public class PerlToJava extends LocalToolBase {
	
	public static void superFixTranslatedJavaFile(String fileName) {
		String lines[] = getFileLines(fileName);
		ArrayList<String> newLines = new ArrayList<String>();
		
		mainLoop:
		for(int i = 0; i < lines.length; ++i) {
			String str = lines[i];
			//get rid of @_
			StringBuilder paramsBuilder = new StringBuilder();
			int indexOfCloseParen = i;
			
			if(str.trim().startsWith("sub") && str.trim().endsWith("{") && !str.trim().contains("(")) {
				String tokens[] = str.trim().split("\\s+");
				if(lines[i+1].contains("//")) { continue; }
				for(int j = i+1; j < lines.length; ++j) {
					//qp("appending: " + lines[j]);
					paramsBuilder.append(lines[j]);
					if(lines[j].contains(")")) {
						if(!lines[j].contains("@_")) {
							continue mainLoop;
						}
						
						indexOfCloseParen = j;
						break;
					}
				}
				
				String newParams = paramsBuilder.toString().trim().replaceAll("\\s+", "");
				newParams = newParams.replaceAll("local", "");
				newParams = newParams.replaceAll("=", "");
				newParams = newParams.replaceAll(";", "");
				newParams = newParams.replaceAll("@_", "");
				String newLine = tokens[0] + tokens[1] + newParams + tokens[2];
				
				/*for(int k = i; k <= indexOfCloseParen; ++k) {
					qp("condensing " + lines[k]);
				}*/
				
				i = indexOfCloseParen;
				newLines.add(newLine);
				
				//qp(newLine);
			}
			
			str = str.replaceAll("elsif", "else if");
			
			if(str.contains("#")) {
				if(!str.contains("$#") && !str.contains("=~") && !str.contains("!~")) {
					str = str.replaceAll(" #", " //");
					str = str.replaceAll("\t#", "\t//");	
				}
			}
			
			if(str.contains("foreach")) {
				str = str.replaceAll("\\(", ":");
				str = str.replaceAll("foreach", "for\\(");
			}
			
			str = str.replaceAll(".=", "+=");
			
			/*if(str.contains(" eq \"") && str.contains("if")) {
				str.replaceAll(" eq \"", ".equals(\"");
				
			}*/
			
			newLines.add(str);
			
		}
		writeFileLines("test.java", newLines);
	}
	
	public static void superFixTranslatedJavaFile2(String fileName) {
		String lines[] = getFileLines(fileName);
		ArrayList<String> newLines = new ArrayList<String>();
		
		//mainLoop:
		for(int i = 0; i < lines.length; ++i) {
			String str = lines[i];
			//get rid of @_
			//StringBuilder paramsBuilder = new StringBuilder();
			//int indexOfCloseParen = i;
			
			if(str.contains("foreach")) {
				str = str.replaceAll("\\(", ":");
				str = str.replaceAll("foreach", "for\\(");
			}
			
			str = str.replaceAll(".=", "+=");
			
			newLines.add(str);
		}
		writeFileLines("test.java", newLines);
	}
}
