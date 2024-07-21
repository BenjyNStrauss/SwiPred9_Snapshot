package dev.inProgress.sable.networks2;

import dev.inProgress.sable.NetworkBase;
import dev.inProgress.sable.Valid;
import dev.inProgress.sable.pUnit;
import install.DirectoryManager;

/*********************************************************
  test_newN4.c
  --------------------------------------------------------
  generated at Tue Aug  3 10:34:15 2004
  by snns2c ( Bernward Kett 1995 )
*********************************************************/

public class te4_S extends NetworkBase {
	private static final String DATA_FILE = DirectoryManager.FILES_PREDICT_SABLE + "/te4_S.txt";
	private static boolean initialized = false;


	static int[] Sources = null;
	static float[] Weights = null;
	static pUnit[] Units = null;

	public te4_S() {
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
		pUnit[] Input = pUnit.getUnits(Units, 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110);
		pUnit[] Hidden1 = pUnit.getUnits(Units, 111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140);
		pUnit[] Hidden2 = pUnit.getUnits(Units, 141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170);
		pUnit[] Output1 = pUnit.getUnits(Units, 171,172,173,174,175,176,177,178,179);
		pUnit[] Special1 = pUnit.getUnits(Units, 180);
		pUnit[] Special2 = pUnit.getUnits(Units, 181);
		pUnit[] Special3 = pUnit.getUnits(Units, 182);
		pUnit[] Special4 = pUnit.getUnits(Units, 183);
		pUnit[] Special5 = pUnit.getUnits(Units, 184);
		pUnit[] Special6 = pUnit.getUnits(Units, 185);
		pUnit[] Special7 = pUnit.getUnits(Units, 186);
		pUnit[] Special8 = pUnit.getUnits(Units, 187);
		pUnit[] Special9 = pUnit.getUnits(Units, 188);
		pUnit[] Special10 = pUnit.getUnits(Units, 189);
		pUnit[] Special11 = pUnit.getUnits(Units, 190);
		pUnit[] Special12 = pUnit.getUnits(Units, 191);
		pUnit[] Special13 = pUnit.getUnits(Units, 192);
		pUnit[] Special14 = pUnit.getUnits(Units, 193);
		pUnit[] Special15 = pUnit.getUnits(Units, 194);
		pUnit[] Special16 = pUnit.getUnits(Units, 195);
		pUnit[] Special17 = pUnit.getUnits(Units, 196);
		pUnit[] Special18 = pUnit.getUnits(Units, 197);
		pUnit[] Special19 = pUnit.getUnits(Units, 198);
		pUnit[] Special20 = pUnit.getUnits(Units, 199);
		pUnit[] Special21 = pUnit.getUnits(Units, 200);
		pUnit[] Special22 = pUnit.getUnits(Units, 201);
		pUnit[] Special23 = pUnit.getUnits(Units, 202);
		pUnit[] Special24 = pUnit.getUnits(Units, 203);
		pUnit[] Special25 = pUnit.getUnits(Units, 204);
		pUnit[] Special26 = pUnit.getUnits(Units, 205);
		pUnit[] Special27 = pUnit.getUnits(Units, 206);
		pUnit[] Special28 = pUnit.getUnits(Units, 207);
		pUnit[] Special29 = pUnit.getUnits(Units, 208);
		pUnit[] Special30 = pUnit.getUnits(Units, 209);
		pUnit[] Special31 = pUnit.getUnits(Units, 210);
		pUnit[] Special32 = pUnit.getUnits(Units, 211);
		pUnit[] Special33 = pUnit.getUnits(Units, 212);
		pUnit[] Special34 = pUnit.getUnits(Units, 213);
		pUnit[] Special35 = pUnit.getUnits(Units, 214);
		pUnit[] Special36 = pUnit.getUnits(Units, 215);
		pUnit[] Special37 = pUnit.getUnits(Units, 216);
		pUnit[] Special38 = pUnit.getUnits(Units, 217);
		pUnit[] Special39 = pUnit.getUnits(Units, 218);
		pUnit[] Special40 = pUnit.getUnits(Units, 219);
		pUnit[] Special41 = pUnit.getUnits(Units, 220);
		pUnit[] Special42 = pUnit.getUnits(Units, 221);
		pUnit[] Special43 = pUnit.getUnits(Units, 222);
		pUnit[] Special44 = pUnit.getUnits(Units, 223);
		pUnit[] Special45 = pUnit.getUnits(Units, 224);
		pUnit[] Special46 = pUnit.getUnits(Units, 225);
		pUnit[] Special47 = pUnit.getUnits(Units, 226);
		pUnit[] Special48 = pUnit.getUnits(Units, 227);
		pUnit[] Special49 = pUnit.getUnits(Units, 228);
		pUnit[] Special50 = pUnit.getUnits(Units, 229);
		pUnit[] Special51 = pUnit.getUnits(Units, 230);
		pUnit[] Special52 = pUnit.getUnits(Units, 231);
		pUnit[] Special53 = pUnit.getUnits(Units, 232);
		pUnit[] Special54 = pUnit.getUnits(Units, 233);
		pUnit[] Special55 = pUnit.getUnits(Units, 234);
		pUnit[] Special56 = pUnit.getUnits(Units, 235);
		pUnit[] Special57 = pUnit.getUnits(Units, 236);
		pUnit[] Special58 = pUnit.getUnits(Units, 237);
		pUnit[] Special59 = pUnit.getUnits(Units, 238);
		pUnit[] Special60 = pUnit.getUnits(Units, 239);
		int[] Output = {171, 172, 173, 174, 175, 176, 177, 178, 179};
		for(member = 0; member < 110; member++) {
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
		for (member = 0; member < 30; member++) {
			unit = Hidden2[member];
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
		for (member = 0; member < 1; member++) {
			unit = Special31[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special32[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special33[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special34[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special35[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special36[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special37[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special38[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special39[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special40[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special41[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special42[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special43[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special44[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special45[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special46[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special47[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special48[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special49[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special50[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special51[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special52[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special53[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special54[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special55[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special56[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special57[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special58[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special59[member];
			sum = (float) 0.0;
			for (source = 0; source < unit.NoOfSources; source++) {
				sum += unit.sources[source].act
							 * unit.weights[source];
			}
			unit.act = Act_Identity(sum, unit.Bias);
		};
		for (member = 0; member < 1; member++) {
			unit = Special60[member];
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
