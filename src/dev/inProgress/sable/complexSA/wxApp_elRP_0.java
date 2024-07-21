package dev.inProgress.sable.complexSA;

import dev.inProgress.sable.NetworkBase;
import dev.inProgress.sable.Valid;
import dev.inProgress.sable.pUnit;
import install.DirectoryManager;

/*********************************************************
  Pfam_elRp_0_forceW7new390.c
  --------------------------------------------------------
  generated at Tue Aug  3 11:06:27 2004
  by snns2c ( Bernward Kett 1995 )
*********************************************************/

public class wxApp_elRP_0 extends NetworkBase {
	private static final String DATA_FILE = DirectoryManager.FILES_PREDICT_SABLE + "/wxApp_elRP_0.txt";
	private static boolean initialized = false;


	static int[] Sources = null;
	static float[] Weights = null;
	static pUnit[] Units = null;

	public wxApp_elRP_0() {
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
		pUnit[] Input = pUnit.getUnits(Units, 1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269);
		pUnit[] Hidden1 = pUnit.getUnits(Units, 270,271,272,273,274,275,276,277,278,279,280,281,282,283,284,285,286,287,288,289,290,291,292,293,294,295,296,297,298,299);
		pUnit[] Hidden2 = pUnit.getUnits(Units, 300,301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329);
		pUnit[] Output1 = pUnit.getUnits(Units, 330);
		pUnit[] Special1 = pUnit.getUnits(Units, 331);
		pUnit[] Special2 = pUnit.getUnits(Units, 332);
		pUnit[] Special3 = pUnit.getUnits(Units, 333);
		pUnit[] Special4 = pUnit.getUnits(Units, 334);
		pUnit[] Special5 = pUnit.getUnits(Units, 335);
		pUnit[] Special6 = pUnit.getUnits(Units, 336);
		pUnit[] Special7 = pUnit.getUnits(Units, 337);
		pUnit[] Special8 = pUnit.getUnits(Units, 338);
		pUnit[] Special9 = pUnit.getUnits(Units, 339);
		pUnit[] Special10 = pUnit.getUnits(Units, 340);
		pUnit[] Special11 = pUnit.getUnits(Units, 341);
		pUnit[] Special12 = pUnit.getUnits(Units, 342);
		pUnit[] Special13 = pUnit.getUnits(Units, 343);
		pUnit[] Special14 = pUnit.getUnits(Units, 344);
		pUnit[] Special15 = pUnit.getUnits(Units, 345);
		pUnit[] Special16 = pUnit.getUnits(Units, 346);
		pUnit[] Special17 = pUnit.getUnits(Units, 347);
		pUnit[] Special18 = pUnit.getUnits(Units, 348);
		pUnit[] Special19 = pUnit.getUnits(Units, 349);
		pUnit[] Special20 = pUnit.getUnits(Units, 350);
		pUnit[] Special21 = pUnit.getUnits(Units, 351);
		pUnit[] Special22 = pUnit.getUnits(Units, 352);
		pUnit[] Special23 = pUnit.getUnits(Units, 353);
		pUnit[] Special24 = pUnit.getUnits(Units, 354);
		pUnit[] Special25 = pUnit.getUnits(Units, 355);
		pUnit[] Special26 = pUnit.getUnits(Units, 356);
		pUnit[] Special27 = pUnit.getUnits(Units, 357);
		pUnit[] Special28 = pUnit.getUnits(Units, 358);
		pUnit[] Special29 = pUnit.getUnits(Units, 359);
		pUnit[] Special30 = pUnit.getUnits(Units, 360);
		pUnit[] Special31 = pUnit.getUnits(Units, 361);
		pUnit[] Special32 = pUnit.getUnits(Units, 362);
		pUnit[] Special33 = pUnit.getUnits(Units, 363);
		pUnit[] Special34 = pUnit.getUnits(Units, 364);
		pUnit[] Special35 = pUnit.getUnits(Units, 365);
		pUnit[] Special36 = pUnit.getUnits(Units, 366);
		pUnit[] Special37 = pUnit.getUnits(Units, 367);
		pUnit[] Special38 = pUnit.getUnits(Units, 368);
		pUnit[] Special39 = pUnit.getUnits(Units, 369);
		pUnit[] Special40 = pUnit.getUnits(Units, 370);
		pUnit[] Special41 = pUnit.getUnits(Units, 371);
		pUnit[] Special42 = pUnit.getUnits(Units, 372);
		pUnit[] Special43 = pUnit.getUnits(Units, 373);
		pUnit[] Special44 = pUnit.getUnits(Units, 374);
		pUnit[] Special45 = pUnit.getUnits(Units, 375);
		pUnit[] Special46 = pUnit.getUnits(Units, 376);
		pUnit[] Special47 = pUnit.getUnits(Units, 377);
		pUnit[] Special48 = pUnit.getUnits(Units, 378);
		pUnit[] Special49 = pUnit.getUnits(Units, 379);
		pUnit[] Special50 = pUnit.getUnits(Units, 380);
		pUnit[] Special51 = pUnit.getUnits(Units, 381);
		pUnit[] Special52 = pUnit.getUnits(Units, 382);
		pUnit[] Special53 = pUnit.getUnits(Units, 383);
		pUnit[] Special54 = pUnit.getUnits(Units, 384);
		pUnit[] Special55 = pUnit.getUnits(Units, 385);
		pUnit[] Special56 = pUnit.getUnits(Units, 386);
		pUnit[] Special57 = pUnit.getUnits(Units, 387);
		pUnit[] Special58 = pUnit.getUnits(Units, 388);
		pUnit[] Special59 = pUnit.getUnits(Units, 389);
		pUnit[] Special60 = pUnit.getUnits(Units, 390);
		int Output[] = {330};
		for(member = 0; member < 269; member++) {
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
		for (member = 0; member < 1; member++) {
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
		for(member = 0; member < 1; member++) {
			out[member] = Units[Output[member]].act;
		}
		return Valid.OK.ordinal();
	}
}
