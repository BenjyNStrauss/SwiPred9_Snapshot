package dev.inProgress.sable.networks2;

import dev.inProgress.sable.NetworkBase;
import dev.inProgress.sable.Valid;
import dev.inProgress.sable.pUnit;
import install.DirectoryManager;

/*********************************************************
  TMPENTROP2+weight_0_0.c
  --------------------------------------------------------
  generated at Tue Aug  3 10:17:31 2004
  by snns2c ( Bernward Kett 1995 )
*********************************************************/

public class ENTR2_W extends NetworkBase {
	private static final String DATA_FILE = DirectoryManager.FILES_PREDICT_SABLE + "/ENTR2_W.txt";
	private static boolean initialized = false;


	static int[] Sources = null;
	static float[] Weights = null;
	static pUnit[] Units = null;

	public ENTR2_W() {
		if(!initialized) {
			Object[] temp = init(DATA_FILE);
			Sources = (int[])   temp[0];
			Weights = (float[]) temp[1];
			Units   = (pUnit[]) temp[2];
			initialized = true;
		}
	}

	public int net(float[] in, float[] out, int init) {
		int member, source;
		float sum;
		pUnit unit;
		/* layer definition section (names & member units) */
		pUnit[] Input = pUnit.getUnits(Units, 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99);
		pUnit[] Hidden1 = pUnit.getUnits(Units, 100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129);
		pUnit[] Output1 = pUnit.getUnits(Units, 130,131,132,133,134,135,136,137,138);
		pUnit[] Special1 = pUnit.getUnits(Units, 139);
		pUnit[] Special2 = pUnit.getUnits(Units, 140);
		pUnit[] Special3 = pUnit.getUnits(Units, 141);
		pUnit[] Special4 = pUnit.getUnits(Units, 142);
		pUnit[] Special5 = pUnit.getUnits(Units, 143);
		pUnit[] Special6 = pUnit.getUnits(Units, 144);
		pUnit[] Special7 = pUnit.getUnits(Units, 145);
		pUnit[] Special8 = pUnit.getUnits(Units, 146);
		pUnit[] Special9 = pUnit.getUnits(Units, 147);
		pUnit[] Special10 = pUnit.getUnits(Units, 148);
		pUnit[] Special11 = pUnit.getUnits(Units, 149);
		pUnit[] Special12 = pUnit.getUnits(Units, 150);
		pUnit[] Special13 = pUnit.getUnits(Units, 151);
		pUnit[] Special14 = pUnit.getUnits(Units, 152);
		pUnit[] Special15 = pUnit.getUnits(Units, 153);
		pUnit[] Special16 = pUnit.getUnits(Units, 154);
		pUnit[] Special17 = pUnit.getUnits(Units, 155);
		pUnit[] Special18 = pUnit.getUnits(Units, 156);
		pUnit[] Special19 = pUnit.getUnits(Units, 157);
		pUnit[] Special20 = pUnit.getUnits(Units, 158);
		pUnit[] Special21 = pUnit.getUnits(Units, 159);
		pUnit[] Special22 = pUnit.getUnits(Units, 160);
		pUnit[] Special23 = pUnit.getUnits(Units, 161);
		pUnit[] Special24 = pUnit.getUnits(Units, 162);
		pUnit[] Special25 = pUnit.getUnits(Units, 163);
		pUnit[] Special26 = pUnit.getUnits(Units, 164);
		pUnit[] Special27 = pUnit.getUnits(Units, 165);
		pUnit[] Special28 = pUnit.getUnits(Units, 166);
		pUnit[] Special29 = pUnit.getUnits(Units, 167);
		pUnit[] Special30 = pUnit.getUnits(Units, 168);
		int[] Output = {130, 131, 132, 133, 134, 135, 136, 137, 138};
		for(member = 0; member < 99; member++) {
			Input[member].act = in[member];
		}
		for (member = 0; member < 30; member++) {
			unit = Hidden1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Logistic(sum, unit.Bias);
		};
		for (member = 0; member < 9; member++) {
			unit = Output1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Logistic(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special1[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special2[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special3[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special4[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special5[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special6[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special7[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special8[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special9[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special10[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special11[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special12[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special13[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special14[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special15[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special16[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special17[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special18[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special19[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special20[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special21[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special22[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special23[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special24[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special25[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special26[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special27[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special28[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special29[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special30[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for(member = 0; member < 9; member++) {
			out[member] = Units[Output[member]].act;
		}
		return Valid.OK.ordinal();
	}
}
