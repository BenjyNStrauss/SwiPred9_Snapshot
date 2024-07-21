package dev.special.transform;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import assist.exceptions.UnmappedEnumValueException;
import assist.util.LabeledList;
import biology.amino.AminoAcid;
import biology.amino.SecondarySimple;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import modules.descriptor.charge.Charge;
import utilities.LocalToolBase;

/**
 * Designed to add weighted charge to a CSV file
 * Not 100% reusable…
 * @author bns
 *
 */

public class AddWeightedCharge extends LocalToolBase {
	public static final int RES_COL = 2;
	
	private static final NumberFormat formatter = new DecimalFormat("#0.000");
	
	private static final String[] FILENAMES = {
			"bondugula2-0-learn.csv",
			"bondugula2-1-learn.csv",
			"bondugula2-2-learn.csv",
			"bondugula2-3-learn.csv",
			"bondugula2-4-learn.csv",
			"bondugula2-5-learn.csv",
			"bondugula2-6-learn.csv",   //do we still have this?
			"bondugula2-6.1-learn.csv",
			"bondugula2-6.2-learn.csv", //TODO
			"bondugula2-6.3-learn.csv",
			"bondugula2-6.4-learn.csv", //TODO
			"bondugula2-6.5-learn.csv", //TODO
			"bondugula2-6.6-learn.csv", //TODO
			"bondugula2-6-learn.csv",
			"bondugula2-7-learn.csv",
	};
	
	public static void main(String[] args) {
		for(String filename: FILENAMES) {
			//contains the vkbat columns
			LabeledList<Integer> vkPredCols = new LabeledList<Integer>();
			LabeledList<VKPred> vkPreds = new LabeledList<VKPred>();
			//contains the lines of the file
			String[] lines = getFileLines("output/bon2-full-output/"+filename);
			String[] fields = lines[0].split(",");
			for(int ii = 0; ii < fields.length; ++ii) {
				if(VKPred.parse(fields[ii]) != VKPred.UNKNOWN) {
					vkPredCols.add(ii);
					vkPreds.add(VKPred.parse(fields[ii]));
				}
			}
			qp("loaded index maps");
			//qp(vkPreds);
			
			lines[0] += ",VKbat-Predicted-Charge,VKbat-Predicted-Charge-Per-Atom";
			for(int index = 1; index < lines.length; ++index) {
				fields = lines[index].split(",");
				AminoAcid aa = new AminoAcid(AminoType.parse(fields[RES_COL]));
				for(int index2 = 0; index2 < vkPreds.size(); ++index2) {
					try {
						aa.setVkbat(vkPreds.get(index2), SecondarySimple.parse(fields[vkPredCols.get(index2)]));
					} catch (UnmappedEnumValueException UEVE) {
						
					}
				}
				double net = Charge.getVKPredictedNetCharge(aa, false, false);
				double avg = Charge.getVKPredictedNetCharge(aa, true, false);
				//qp("net: " + net);
				//qp("avg: " + avg);
				//System.exit(0);
				if(!lines[index].endsWith(",")) {
					lines[index] += ",";
				}
				
				lines[index] += formatter.format(net) + "," + formatter.format(avg);
			}
			filename = filename.substring(0, filename.length()-4);
			filename = "output/" + filename + "+.csv";
			writeFileLines(filename, lines);
		}
	}
}
