package model;

import java.io.File;

import assist.util.LabeledList;
import assist.util.LabeledSet;
import utilities.LocalToolBase;

class TestClass extends LocalToolBase {
	
	private static final String START_HERE = "#  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO";
	
	private static final LabeledSet<Character> CHAR_SET_1 = new LabeledSet<Character>();
	private static final LabeledSet<Character> CHAR_SET_2 = new LabeledSet<Character>();
	private static final LabeledSet<Character> CHAR_SET_3 = new LabeledSet<Character>();
	private static final LabeledSet<Character> CHAR_SET_4 = new LabeledSet<Character>();
	private static final LabeledSet<Character> CHAR_SET_5 = new LabeledSet<Character>();
	private static final LabeledSet<Character> CHAR_SET_6 = new LabeledSet<Character>();
	
	public static void main(String[] args) {
		final String directory = "files/fasta/dssp";
		final File dsspDir = new File(directory);
		
		final String dssps[] = dsspDir.list();
		
		for(String dssp: dssps) {
			qp("starting: " + dssp);
			String[] lines = getFileLines(directory+"/"+dssp);
			
			boolean process = false;
			for(String line: lines) {
				if(!process && line.trim().startsWith(START_HERE)) {
					process = true;
				} else if(process) {
					DSSP_Measure measure;
					try {
						measure = new DSSP_Measure(line);
					} catch (DSSPBorderException e) {
						continue;
					} catch (NumberFormatException NFE) {
						qerr(">>"+line);
						continue;
					}
					
					CHAR_SET_1.add(measure._3_10_helix);
					CHAR_SET_2.add(measure.alpha_helix);
					CHAR_SET_3.add(measure.pi_helix);
					CHAR_SET_4.add(measure.PPII);
					CHAR_SET_5.add(measure.chirality);
					CHAR_SET_6.add(measure.geometrical_bend);
				}
			}
			
			qp("processed "+ dssp);
		}
		
		final LabeledList<String> out = new LabeledList<String>();
		out.add(getStr(CHAR_SET_1));
		out.add(getStr(CHAR_SET_2));
		out.add(getStr(CHAR_SET_3));
		out.add(getStr(CHAR_SET_4));
		out.add(getStr(CHAR_SET_5));
		out.add(getStr(CHAR_SET_6));
		writeFileLines("values.txt", out);
	}

}
