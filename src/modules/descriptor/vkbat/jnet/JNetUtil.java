package modules.descriptor.vkbat.jnet;

import java.util.ArrayList;

import install.DirectoryManager;
import utilities.LocalToolBase;
import utilities.exceptions.MessedUpSystemFileException;

/**
 * 
 * @refactorer Benjy Strauss
 *
 */

public abstract class JNetUtil extends LocalToolBase {
	private static final int NUM_UNIT_TYPE_FIELDS = 5;
	private static final String JNET_FOLDER = DirectoryManager.FILES_PREDICT_JNET + "/";
	
	protected static final int OK = 0;
	protected static final int Error = 1;
	protected static final int Not_Valid = 2;
	
	protected double Weights[];
	protected UnitType Units[];
	protected UnitType Sources[];
	
	protected double Act_Logistic(double sum, double bias)  {
		return (sum+bias<10000.0) ? ( 1.0/(1.0 + Math.exp(-sum-bias) ) ) : 0.0 ;
	}
	
	public static final JNetHelperStruct init(String classType, int num_src, int num_unit) {
		JNetHelperStruct retval = new JNetHelperStruct();
		//initialize weights
		String weightData[] = getFileLines(JNET_FOLDER + classType +"-weights.txt");
		ArrayList<String> weightStrings = new ArrayList<String>();
		
		for(String line: weightData) {
			String lineWeights[] = line.split(",");
			for(String weight: lineWeights) {
				weightStrings.add(weight.trim());
			}
		}
		
		retval.Weights = new double[weightStrings.size()];
		for(int index = 0; index < weightStrings.size(); ++index) {
			retval.Weights[index] = Double.parseDouble(weightStrings.get(index));
		}
		
		retval.Sources = new UnitType[num_src];
		retval.Units = new UnitType[num_unit];
		
		retval.Units[0] = new UnitType(0.0, 0.0, 0, null, null);
		
		String rawUnitFileData[] = getFileLines(JNET_FOLDER + classType +"-units.txt");
		float unitFileData[][] = new float[retval.Units.length][5];
		for(int index = 1; index < retval.Units.length; ++index) {
			if(!rawUnitFileData[index].equals("")) {
				
				String[] values = rawUnitFileData[index].split(",");
				
				if(values.length != NUM_UNIT_TYPE_FIELDS) {
					throw new MessedUpSystemFileException(JNET_FOLDER + classType +"-units.txt");
				}
				
				for(int index2 = 0; index2 < NUM_UNIT_TYPE_FIELDS; ++index2) {
					unitFileData[index][index2] = Float.parseFloat(values[index2]);
				}
				
				float weights[] = new float[1];
				weights[0] = (float) retval.Weights[(int) unitFileData[index][4]];
				
				retval.Units[index] = new UnitType(unitFileData[index][0], unitFileData[index][1], (int) unitFileData[index][2], null, weights);
			} else {
				throw new MessedUpSystemFileException(JNET_FOLDER + classType +"-units.txt");
			}
		}
		
		String rawSourceFileData[] = getFileLines(JNET_FOLDER + classType +"-sources.txt");
		for(int index = 0; index < retval.Sources.length; ++index) {
			retval.Sources[index] = retval.Units[Integer.parseInt(rawSourceFileData[index])];
		}
		
		for(int index = 1; index <retval. Units.length; ++index) {
			UnitType meta[] = new UnitType[1];
			meta[0] = retval.Sources[(int) unitFileData[index][3]];
			retval.Units[index].sources = meta;
		}
		
		return retval;
	}
	
	public abstract void callInit();
}
