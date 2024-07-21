package modules.descriptor.vkbat.jnet;

import java.util.ArrayList;

import assist.translation.cplusplus.CTranslator;
import install.DirectoryManager;
import utilities.exceptions.MessedUpSystemFileException;

/**
 * 
 * @refactorer Benjy Strauss
 *
 */

public class JNetMeta extends CTranslator {
	private static final int NUM_UNIT_TYPE_FIELDS = 5;
	private static final String JNET_FOLDER = DirectoryManager.FILES_PREDICT_JNET + "/";
	
	private static final int HMMSOL_INPUT_LEN = 408;
	private static final int PSISOL_INPUT_LEN = 340;
	//does NOT apply to consnet()
	private static final int STANDARD_HIDDEN_LEN = 20;
	
	protected static final int OK = 0;
	protected static final int Error = 1;
	protected static final int Not_Valid = 2;
	
	protected double Weights[];
	protected UnitType Units[];
	protected UnitType Sources[];
	
	protected double Act_Logistic(double sum, double bias)  {
		return (sum+bias<10000.0) ? ( 1.0/(1.0 + Math.exp(-sum-bias) ) ) : 0.0 ;
	}
	
	/**
	 * TODO change this back to protected when testing is finished!
	 * @param method
	 * @param num_src
	 * @param num_unit
	 */
	public void init(String method) {
		//initialize weights
		String weightData[] = getFileLines(JNET_FOLDER + method +"-weights.txt");
		ArrayList<String> weightStrings = new ArrayList<String>();
		
		for(String line: weightData) {
			String lineWeights[] = line.split(",");
			for(String weight: lineWeights) {
				String trimmed_weight = weight.trim();
				
				if(trimmed_weight.length() != 0) {
					weightStrings.add(weight.trim());
				}
			}
		}
		
		Weights = new double[weightStrings.size()];
		for(int index = 0; index < weightStrings.size(); ++index) {
			Weights[index] = Double.parseDouble(weightStrings.get(index));
		}
		
		String rawUnitFileData[] = getFileLines(JNET_FOLDER + method +"-units.txt");
		float unitFileData[][] = new float[rawUnitFileData.length][5];
		Units = new UnitType[rawUnitFileData.length];
		//qp("rawUnitFileData.length: " + rawUnitFileData.length);
		
		Units[0] = new UnitType(0.0, 0.0, 0, null, null);
		
		//parse the data from the unit file
		for(int index = 1; index < Units.length; ++index) {
			if(!rawUnitFileData[index].equals("")) {
				String[] values = rawUnitFileData[index].split(",");
				
				if(values.length != NUM_UNIT_TYPE_FIELDS) {
					throw new MessedUpSystemFileException(JNET_FOLDER + method +"-units.txt");
				}
				
				for(int index2 = 0; index2 < NUM_UNIT_TYPE_FIELDS; ++index2) {
					unitFileData[index][index2] = Float.parseFloat(values[index2]);
				}
				
				Units[index] = new UnitType(unitFileData[index][0], unitFileData[index][1], (int) unitFileData[index][2], null, null);
				Units[index].idNum = index;
			} else {
				throw new MessedUpSystemFileException(JNET_FOLDER + method +"-units.txt");
			}
		}
		
		String rawSourceFileData[] = getFileLines(JNET_FOLDER + method +"-sources.txt");
		Sources = new UnitType[rawSourceFileData.length];
		
		//if(method.equals("net2")) { qp(rawSourceFileData.length); }
		for(int index = 0; index < Sources.length; ++index) {
			//qp(rawSourceFileData[index]);
			Sources[index] = Units[Integer.parseInt(rawSourceFileData[index])];
		}
		
		//qp("Weights.length(" + method + "): " + Weights.length);
		
		//assign sources to the units
		for(int index = 0; index < Units.length; ++index) {
			
			int sourcesStartAt = (int) unitFileData[index][3];
			int weightsStartAt = (int) unitFileData[index][4];
			
			UnitType unitSouces[] = new UnitType[Sources.length - sourcesStartAt];
			float unitWeights[] = new float[Weights.length - weightsStartAt];
			
			for(int srcIndex = sourcesStartAt; srcIndex < Sources.length; ++srcIndex) {
				unitSouces[srcIndex - sourcesStartAt] = Sources[srcIndex];
			}
			
			for(int weightIndex = weightsStartAt; weightIndex < Weights.length; ++weightIndex) {
				unitWeights[weightIndex - weightsStartAt] = (float) Weights[weightIndex];
			}
			
			if(Units[index] != null) {
				Units[index].sources = unitSouces;
				Units[index].weights = unitWeights;
			} else {
				qerr("Error! Null Unit!");
			}
		}
	}

	float[] consnet(float[] in, float[] out, int init) {
		init("consnet");
		int member, source;
		float sum;
	  
		UnitType unit;

		/* layer definition section (names & member units) */

		UnitType Input[] = new UnitType[204];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[25];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
	  
		UnitType Output1[] = {Units[230], Units[231], Units[232]}; /* members */

		int Output[] = {230, 231, 232};

		for(member = 0; member < Input.length; member++) {
			Input[member].act = in[member];
		}

		for (member = 0; member < Hidden1.length; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		}

		for (member = 0; member < Output.length; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
			out[member] = Units[Output[member]].act;
		}

	  	return out;
	}
	
	float[] hmm1(float[] in, float[] out, int init) {
		init("hmm1");
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[408];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
		
	  	UnitType Output1[] = {Units[429], Units[430], Units[431]}; /* members */

	 	int Output[] = {429, 430, 431};

	 	for(member = 0; member < Input.length; member++) {
	 		Input[member].act = in[member];
	 	}

	 	for (member = 0; member < Hidden1.length; member++) {
	 		unit = Hidden1[member];
	 		sum = (float) 0.0;
	 		for (source = 0; source < unit.NoOfSources; source++) {
  				sum += unit.sources[source].act * unit.weights[source];
  			}
	 		unit.act = (float) Act_Logistic(sum, unit.Bias);
	 	};

	 	for (member = 0; member < Output.length; member++) {
	 		unit = Output1[member];
	 		sum = (float) 0.0;
	 		for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
	 		unit.act = (float) Act_Logistic(sum, unit.Bias);
	 	};

	 	for(member = 0; member < out.length; member++) {
	 		out[member] = Units[Output[member]].act;
	 	}

	 	return out;
	}
	
	float[] hmm2(float[] in, float[] out, int init) {
		init("hmm2");
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[57];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
		
	  	UnitType Output1[] = {Units[78], Units[79], Units[80]}; /* members */

	 	int Output[] = {78, 79, 80};

	 	for(member = 0; member < Input.length; member++) {
	 		Input[member].act = in[member];
	 	}

	 	for (member = 0; member < Hidden1.length; member++) {
	 		unit = Hidden1[member];
	 		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
  				sum += unit.sources[source].act * unit.weights[source];
  			}
	 		unit.act = (float) Act_Logistic(sum, unit.Bias);
	 	};

	 	for (member = 0; member < Output.length; member++) {
	 		unit = Output1[member];
	 		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
  				sum += unit.sources[source].act * unit.weights[source];
  			}
	  		
	 		unit.act = (float) Act_Logistic(sum, unit.Bias);
	 	};

	 	for(member = 0; member < out.length; member++) {
	 		out[member] = Units[Output[member]].act;
	 	}

	 	return out;
	}

	/**
	 * Commented loop is wrong (this method only) -- 7/31/19
	 * @param in
	 * @param out
	 * @param init
	 * @return
	 */
	float[] net1(float[] in, float[] out, int init) {
		init("net1");
		
		int member, source;
		float sum;
		UnitType unit;

		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[425];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		
		UnitType Hidden1[]  = new UnitType[9];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
			//qp(Hidden1[ii].idNum + "::" + Hidden1[ii].sources.length);
		}
		
	  	UnitType Output1[] = {Units[435], Units[436], Units[437]}; /* members */

	  	int Output[] = {435, 436, 437};
	  	
	  	for(member = 0; member < Input.length; member++) {
	  		Input[member].act = in[member];
	  	}

	  	for (member = 0; member < Hidden1.length; member++) {
	  		unit = Hidden1[member];
	  		sum = (float) 0.0;
	  		
	  		//qp(unit.idNum);
	  		//qp(unit.sources.length);
	  		
	  		for (source = 0; source < unit.NoOfSources; source++) {
	  			sum += unit.sources[source].act * unit.weights[source];
	  		}
	  		
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  		//fprintf(stderr, "unit[%d]->act %f\n", member, unit.act);
	  	};
	  	
	  	for (member = 0; member < Output1.length; member++) {
	  		unit = Output1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
  				sum += unit.sources[source].act * unit.weights[source];
  			}
	  		
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};

	  	for(member = 0; member < out.length; member++) {
	  		out[member] = Units[Output[member]].act;
	  	}
	  	
	  	return out;
	}
	
	float[] net1b(float[] in, float[] out, int init) {
		init("net1b");
		
		int member, source;
		float sum;
		UnitType unit;

		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[425];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		
		UnitType Hidden1[]  = new UnitType[9];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
		
	  	UnitType Output1[] = {Units[435], Units[436], Units[437]}; /* members */

	  	int Output[] = {435, 436, 437};

	  	for(member = 0; member < Input.length; member++) {
	  		Input[member].act = in[member];
	  	}

	  	for (member = 0; member < Hidden1.length; member++) {
	  		unit = Hidden1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
  		
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};

	  	for (member = 0; member < Output1.length; member++) {
	  		unit = Output1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};

	  	for(member = 0; member < out.length; member++) {
	  		out[member] = Units[Output[member]].act;
	  	}
	  	
	  	return out;
	}
	
	float[] net2(float[] in, float[] out, int init) {
		init("net2");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[76];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		
		UnitType Hidden1[] = new UnitType[15];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
		
		UnitType Output1[] = {Units[92], Units[93], Units[94]}; /* members */

		int Output[] = {92, 93, 94};

		for(member = 0; member < Input.length; member++) {
			Input[member].act = in[member];
		}

		for (member = 0; member < Hidden1.length; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output1.length; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
			out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] net2b(float[] in, float[] out, int init) {
		init("net2b");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[76];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		
		UnitType Hidden1[] = new UnitType[15];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
		
		UnitType Output1[] = {Units[92], Units[93], Units[94]}; /* members */

		int Output[] = {92, 93, 94};

		for(member = 0; member < Input.length; member++) {
			Input[member].act = in[member];
		}

		for (member = 0; member < Hidden1.length; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output1.length; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
			out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] hmmsol0(float[] in, float[] out, int init) {
		init("hmmsol0");
		
		int member, source;
		float sum;
		UnitType unit;

		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[HMMSOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		  
		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
			
		UnitType Output1[] = {Units[429], Units[430]}; /* members */

		int Output[] = {429, 430};

		for(member = 0; member < Input.length; member++) {
			Input[member].act = in[member];
		}

		for (member = 0; member < Hidden1.length; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < 2; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < 2; member++) {
			out[member] = Units[Output[member]].act;
		}
		
		return out;
	}
	
	float[] hmmsol5(float[] in, float[] out, int init) {
		init("hmmsol5");
		
		int member, source;
		float sum;
		UnitType unit;

		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[HMMSOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		  
		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
			
		UnitType Output1[] = {Units[429], Units[430]}; /* members */

		int Output[] = {429, 430};

		for(member = 0; member < Input.length; member++) {
			Input[member].act = in[member];
		}

		for (member = 0; member < Hidden1.length; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < 2; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < 2; member++) {
			out[member] = Units[Output[member]].act;
		}
		
		return out;
	}
	
	float[] hmmsol25(float[] in, float[] out, int init) {
		init("hmmsol25");
		int member, source;
		float sum;
		UnitType unit;

		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[HMMSOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
		  
		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
			
		UnitType Output1[] = {Units[429], Units[430]}; /* members */

		int Output[] = {429, 430};

		for(member = 0; member < Input.length; member++) {
			Input[member].act = in[member];
		}

		for (member = 0; member < Hidden1.length; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < 2; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act * unit.weights[source];
			}
			unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < 2; member++) {
			out[member] = Units[Output[member]].act;
		}
		
		return out;
	}
	
	float[] psisol0(float[] in, float[] out, int init) {
		init("psisol0");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[361], Units[362] }; /* members */

		int Output[] = {361, 362};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] psisol5(float[] in, float[] out, int init) {
		init("psisol5");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[361], Units[362] }; /* members */

		int Output[] = {361, 362};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		      sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] psisol25(float[] in, float[] out, int init) {
		init("psisol25");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[361], Units[362] }; /* members */

		int Output[] = {361, 362};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		      sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] psinet1(float[] in, float[] out, int init) {
		init("psinet1");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[9];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[350], Units[351], Units[352] }; /* members */

		int Output[] = {350, 351, 352};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		      sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output1.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < 2; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;	
	}
	
	float[] psinet1b(float[] in, float[] out, int init) {
		init("psinet1b");
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[9];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[350], Units[351], Units[352] }; /* members */

		int Output[] = {350, 351, 352};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		      sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output1.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] psinet1c(float[] in, float[] out, int init) {
		init("psinet1c");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[361], Units[362], Units[363] }; /* members */

		int Output[] = {361, 362, 363};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output1.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;
	}
	
	float[] psinet2(float[] in, float[] out, int init) {
		init("psinet2");
		int member, source;
		float sum;
	  	UnitType unit;


	  	/* layer definition section (names & member units) */

	  	UnitType Input[] = new UnitType[57];
	  	for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
	  	
	  	UnitType Hidden1[] = new UnitType[57];
	  	for(int ii = 0; ii < Hidden1.length; ++ii) {
	  		Hidden1[ii] = Units[ii+Input.length+1];
		}
	  	
	  	UnitType Output1[] = {Units[67], Units[68], Units[69]}; /* members */

	  	int Output[] = {67, 68, 69};

	  	for(member = 0; member < Input.length; member++) {
	  		Input[member].act = in[member];
	  	}

	  	for (member = 0; member < Hidden1.length; member++) {
	  		unit = Hidden1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
	  			sum += unit.sources[source].act * unit.weights[source];
	  		}
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};
	  
	  	for (member = 0; member < Output1.length; member++) {
	  		unit = Output1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
	  			sum += unit.sources[source].act * unit.weights[source];
	  		}
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};
	  	
	  	for(member = 0; member < out.length; member++) {
	  		out[member] = Units[Output[member]].act;
	  	}

	  	return out;
	}
	
	float[] psinet2b(float[] in, float[] out, int init) {
		init("psinet2b");
		int member, source;
		float sum;
	  	UnitType unit;
	  	
	  	/* layer definition section (names & member units) */
	  	UnitType Input[] = new UnitType[57];
	  	for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}
	  	
	  	UnitType Hidden1[] = new UnitType[57];
	  	for(int ii = 0; ii < Hidden1.length; ++ii) {
	  		Hidden1[ii] = Units[ii+Input.length+1];
		}
	  	
	  	UnitType Output1[] = {Units[67], Units[68], Units[69]}; /* members */

	  	int Output[] = {67, 68, 69};

	  	for(member = 0; member < Input.length; member++) {
	  		Input[member].act = in[member];
	  	}

	  	for (member = 0; member < Hidden1.length; member++) {
	  		unit = Hidden1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
	  			sum += unit.sources[source].act * unit.weights[source];
	  		}
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};
	  
	  	for (member = 0; member < Output1.length; member++) {
	  		unit = Output1[member];
	  		sum = (float) 0.0;
	  		for (source = 0; source < unit.NoOfSources; source++) {
	  			sum += unit.sources[source].act * unit.weights[source];
	  		}
	  		unit.act = (float) Act_Logistic(sum, unit.Bias);
	  	};
	  	
	  	for(member = 0; member < out.length; member++) {
	  		out[member] = Units[Output[member]].act;
	  	}

	  	return out;
	}
	
	float[] psinet2c(float[] in, float[] out, int init) {
		init("psinet2c");
		
		int member, source;
		float sum;
		UnitType unit;
		
		/* layer definition section (names & member units) */
		UnitType Input[] = new UnitType[PSISOL_INPUT_LEN];
		for(int ii = 0; ii < Input.length; ++ii) {
			Input[ii] = Units[ii+1];
		}

		UnitType Hidden1[]  = new UnitType[STANDARD_HIDDEN_LEN];
		for(int ii = 0; ii < Hidden1.length; ++ii) {
			Hidden1[ii] = Units[ii+Input.length+1];
		}
				  
		UnitType Output1[] = { Units[361], Units[362], Units[363] }; /* members */

		int Output[] = {361, 362, 363};

		for(member = 0; member < Input.length; member++) {
		    Input[member].act = in[member];
		}
		
		for (member = 0; member < Hidden1.length; member++) {
		    unit = Hidden1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for (member = 0; member < Output1.length; member++) {
		    unit = Output1[member];
		    sum = (float) 0.0;
		    for (source = 0; source < unit.NoOfSources; source++) {
		    	sum += unit.sources[source].act * unit.weights[source];
		    }
		    unit.act = (float) Act_Logistic(sum, unit.Bias);
		};

		for(member = 0; member < out.length; member++) {
		    out[member] = Units[Output[member]].act;
		}

		return out;
	}
}
