package modules.descriptor.vkbat.choufasman;

import assist.util.LabeledHash;
import utilities.LocalToolBase;

/**
 * 
 * see: http://prowl.rockefeller.edu/aainfo/chou.htm
 * @translator Benjamin Strauss
 *
 */

class CFStruct extends LocalToolBase {
	static final String ACCEPTABLE_RESIDUES = "ACDEFGHIKLMNPQRSTVWY";
	static final String CHOU_FASMAN_VALUES = "["+ACCEPTABLE_RESIDUES+"]+";
	
	static final LabeledHash<String, CFStruct> CF = new LabeledHash<String, CFStruct>() {
		private static final long serialVersionUID = 1L;
		{
			put("Alanine",		 new CFStruct('A', 142,   83,   66,   0.06,   0.076,  0.035,  0.058));
			put("Arginine",		 new CFStruct('R',  98,   93,   95,   0.070,  0.106,  0.099,  0.085));
			put("Aspartic Acid", new CFStruct('N', 101,   54,  146,   0.147,  0.110,  0.179,  0.081));
			put("Asparagine",	 new CFStruct('D',  67,   89,  156,   0.161,  0.083,  0.191,  0.091));
			put("Cysteine",		 new CFStruct('C',  70,  119,  119,   0.149,  0.050,  0.117,  0.128));
			put("Glutamic Acid", new CFStruct('E', 151,   37,   74,   0.056,  0.060,  0.077,  0.064));
			put("Glutamine",	 new CFStruct('Q', 111,  110,   98,   0.074,  0.098,  0.037,  0.098));
			put("Glycine",		 new CFStruct('G',  57,   75,  156,   0.102,  0.085,  0.190,  0.152));
			put("Histidine",	 new CFStruct('H', 100,   87,   95,   0.140,  0.047,  0.093,  0.054));
			put("Isoleucine",	 new CFStruct('I', 108,  160,   47,   0.043,  0.034,  0.013,  0.056));
			put("Leucine",		 new CFStruct('L', 121,  130,   59,   0.061,  0.025,  0.036,  0.070));
			put("Lysine",		 new CFStruct('K', 114,   74,  101,   0.055,  0.115,  0.072,  0.095));
			put("Methionine",	 new CFStruct('M', 145,  105,   60,   0.068,  0.082,  0.014,  0.055));
			put("Phenylalanine", new CFStruct('F', 113,  138,   60,   0.059,  0.041,  0.065,  0.065));
			put("Proline",		 new CFStruct('P',  57,   55,  152,   0.102,  0.301,  0.034,  0.068));
			put("Serine",		 new CFStruct('S',  77,   75,  143,   0.120,  0.139,  0.125,  0.106));
			put("Threonine",	 new CFStruct('T',  83,  119,   96,   0.086,  0.108,  0.065,  0.079));
			put("Tryptophan",	 new CFStruct('W', 108,  137,   96,   0.077,  0.013,  0.064,  0.167));
			put("Tyrosine",		 new CFStruct('Y',  69,  147,  114,   0.082,  0.065,  0.114,  0.125));
			put("Valine",		 new CFStruct('V', 106,  170,   50,   0.062,  0.048,  0.028,  0.053));
			//put("Isoleucine",	 new CFStruct('I', 108,  160,   47,   0.043,  0.034,  0.013,  0.056));
			//put("Isoleucine",	 new CFStruct('I', 108,  160,   47,   0.043,  0.034,  0.013,  0.056));
		}
	};
	
	double[] data;
	
	public CFStruct(char id, int v1, int v2, int v3, double v4, double v5, double v6, double v7) {
		data = new double[8];
		data[0] = id;
		//qp(data[0]);
		data[1] = v1;
		data[2] = v2;
		data[3] = v3;
		data[4] = v4;
		data[5] = v5;
		data[6] = v6;
		data[7] = v7;
	}
}
