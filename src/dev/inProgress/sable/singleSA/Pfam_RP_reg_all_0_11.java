package dev.inProgress.sable.singleSA;

import dev.inProgress.sable.NetworkBase;
import dev.inProgress.sable.Valid;
import dev.inProgress.sable.pUnit;
import install.DirectoryManager;

/*********************************************************
  Pfam_RP_reg_all_0_11.c
  --------------------------------------------------------
  generated at Tue Jan 18 09:55:17 2005
  by snns2c ( Bernward Kett 1995 ) 
*********************************************************/

public class Pfam_RP_reg_all_0_11 extends NetworkBase {
	private static final String DATA_FILE = DirectoryManager.FILES_PREDICT_SABLE + "/Pfam_RP_reg_all_0_11.txt";
	private static boolean initialized = false;


	static int[] Sources = null;
	static float[] Weights = null;
	static pUnit[] Units = null;

	public Pfam_RP_reg_all_0_11() {
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
		int[] Output = {330};
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
		for(member = 0; member < 1; member++) {
			out[member] = Units[Output[member]].act;
		}
		return Valid.OK.ordinal();
	}
}