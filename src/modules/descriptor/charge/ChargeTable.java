package modules.descriptor.charge;

import java.util.Objects;
import java.util.Set;

import assist.util.LabeledHash;
import assist.util.LabeledSet;
import biology.amino.SecondarySimple;
import biology.amino.SecondaryStructure;
import biology.descriptor.TMBRecordedAtom;
import biology.molecule.types.AminoType;
import chem.AminoAtom;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjamin Strauss
 *
 */

class ChargeTable extends LocalToolBase {
	private static boolean printedThisSession = false;
	
	private static final LabeledHash<AminoType, LabeledHash<TMBRecordedAtom, double[]>> TMB_TABLE =
			new LabeledHash<AminoType, LabeledHash<TMBRecordedAtom, double[]>>("TMB Table") {
		private static final long serialVersionUID = 1L;
		{
			put(AminoType.parse("ALA"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.621,0.024,-0.592,0.033,-0.598,0.027,-0.588,0.034}); //ALA
					put(TMBRecordedAtom.HN, new double[] {0.272,0.307,0.025,0.34,0.042,0.358,0.027,0.352,0.048}); //ALA
					put(TMBRecordedAtom.Cα, new double[] {0.034,0.267,0.019,0.267,0.013,0.272,0.011,0.264,0.012}); //ALA
					put(TMBRecordedAtom.CP, new double[] {0.597,0.582,0.031,0.56,0.025,0.559,0.02,0.562,0.023}); //ALA
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.574,0.042,-0.673,0.069,-0.701,0.049,-0.661,0.058}); //ALA
					put(TMBRecordedAtom.Cβ, new double[] {-0.183,-0.416,0.01,-0.392,0.017,-0.4,0.013,-0.38,0.013}); //ALA
				}
			});
			put(AminoType.parse("ARG"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.348,-0.627,0.024,-0.59,0.033,-0.595,0.026,-0.586,0.034}); //ARG
					put(TMBRecordedAtom.HN, new double[] {0.275,0.306,0.027,0.33,0.046,0.348,0.034,0.334,0.056}); //ARG
					put(TMBRecordedAtom.Cα, new double[] {-0.264,0.247,0.018,0.24,0.013,0.243,0.01,0.238,0.01}); //ARG
					put(TMBRecordedAtom.CP, new double[] {0.734,0.582,0.03,0.558,0.026,0.56,0.023,0.557,0.024}); //ARG
					put(TMBRecordedAtom.O,  new double[] {-0.589,-0.57,0.042,-0.658,0.071,-0.682,0.051,-0.658,0.065}); //ARG
					put(TMBRecordedAtom.Cβ, new double[] {-0.001,-0.224,0.01,-0.215,0.016,-0.224,0.013,-0.206,0.014}); //ARG
				}
			});
			put(AminoType.parse("ASN"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.622,0.026,-0.585,0.037,-0.589,0.026,-0.576,0.036}); //ASN
					put(TMBRecordedAtom.HN, new double[] {0.272,0.308,0.031,0.335,0.048,0.355,0.039,0.327,0.064}); //ASN
					put(TMBRecordedAtom.Cα, new double[] {0.014,0.25,0.024,0.244,0.016,0.243,0.014,0.239,0.015}); //ASN
					put(TMBRecordedAtom.CP, new double[] {0.597,0.585,0.031,0.561,0.029,0.559,0.019,0.564,0.023}); //ASN
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.581,0.041,-0.664,0.075,-0.693,0.048,-0.656,0.064}); //ASN
					put(TMBRecordedAtom.Cβ, new double[] {-0.204,-0.249,0.014,-0.238,0.018,-0.247,0.013,-0.233,0.015}); //ASN
				}
			});
			put(AminoType.parse("ASP"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.516,-0.599,0.027,-0.568,0.04,-0.574,0.027,-0.56,0.044}); //ASP
					put(TMBRecordedAtom.HN, new double[] {0.294,0.31,0.035,0.331,0.046,0.355,0.028,0.336,0.043}); //ASP
					put(TMBRecordedAtom.Cα, new double[] {0.038,0.253,0.022,0.246,0.016,0.246,0.01,0.242,0.016}); //ASP
					put(TMBRecordedAtom.CP, new double[] {0.537,0.598,0.032,0.57,0.027,0.573,0.024,0.57,0.026}); //ASP
					put(TMBRecordedAtom.O,  new double[] {-0.582,-0.605,0.042,-0.673,0.077,-0.698,0.043,-0.665,0.073}); //ASP
					put(TMBRecordedAtom.Cβ, new double[] {-0.03,-0.299,0.015,-0.276,0.017,-0.285,0.013,-0.271,0.016}); //ASP
				}
			});
			put(AminoType.parse("CYS"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.6,0.025,-0.558,0.041,-0.577,0.029,-0.55,0.038}); //CYS
					put(TMBRecordedAtom.HN, new double[] {0.272,0.306,0.021,0.335,0.047,0.355,0.025,0.315,0.063}); //CYS
					put(TMBRecordedAtom.Cα, new double[] {0.021,0.191,0.017,0.186,0.014,0.188,0.009,0.183,0.016}); //CYS
					put(TMBRecordedAtom.CP, new double[] {0.597,0.588,0.035,0.574,0.03,0.571,0.028,0.584,0.023}); //CYS			
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.585,0.039,-0.694,0.063,-0.718,0.051,-0.695,0.049}); //CYS
					put(TMBRecordedAtom.Cβ, new double[] {-0.123,0.037,0.013,0.096,0.022,0.086,0.022,0.103,0.02}); //CYS
				}
			});
			put(AminoType.parse("GLN"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.617,0.025,-0.584,0.034,-0.587,0.028,-0.578,0.038}); //GLN
					put(TMBRecordedAtom.HN, new double[] {0.272,0.304,0.034,0.336,0.044,0.352,0.033,0.337,0.051}); //GLN
					put(TMBRecordedAtom.Cα, new double[] {-0.003,0.238,0.025,0.234,0.013,0.236,0.012,0.233,0.013}); //GLN
					put(TMBRecordedAtom.CP, new double[] {0.597,0.586,0.029,0.562,0.024,0.565,0.02,0.563,0.022}); //GLN
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.579,0.043,-0.666,0.068,-0.687,0.048,-0.673,0.06}); //GLN
					put(TMBRecordedAtom.Cβ, new double[] {-0.004,-0.229,0.012,-0.212,0.017,-0.221,0.014,-0.203,0.013}); //GLN
				}
			});
			put(AminoType.parse("GLU"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.609,0.024,-0.575,0.036,-0.584,0.028,-0.564,0.038}); //GLU
					put(TMBRecordedAtom.HN, new double[] {0.272,0.306,0.036,0.338,0.041,0.356,0.026,0.339,0.047}); //GLU
					put(TMBRecordedAtom.Cα, new double[] {0.04,0.236,0.021,0.234,0.013,0.236,0.009,0.228,0.014}); //GLU
					put(TMBRecordedAtom.CP, new double[] {0.537,0.598,0.026,0.568,0.025,0.569,0.022,0.569,0.023}); //GLU
					put(TMBRecordedAtom.O,  new double[] {-0.582,-0.593,0.043,-0.681,0.069,-0.705,0.05,-0.676,0.063}); //GLU
					put(TMBRecordedAtom.Cβ, new double[] {0.056,-0.23,0.014,-0.207,0.017,-0.214,0.015,-0.199,0.013}); //GLU
				}
			});
			put(AminoType.parse("GLY"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.594,0.025,-0.566,0.036,-0.575,0.026,-0.563,0.034}); //GLY
					put(TMBRecordedAtom.HN, new double[] {0.272,0.304,0.025,0.335,0.046,0.363,0.03,0.346,0.038}); //GLY
					put(TMBRecordedAtom.Cα, new double[] {-0.025,0.115,0.021,0.108,0.018,0.119,0.017,0.103,0.015}); //GLY
					put(TMBRecordedAtom.CP, new double[] {0.597,0.6,0.028,0.581,0.028,0.575,0.025,0.58,0.023}); //GLY
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.565,0.041,-0.668,0.076,-0.706,0.059,-0.653,0.055}); //GLY
				}
			});
			put(AminoType.parse("HIS"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.615,0.023,-0.584,0.036,-0.589,0.031,-0.577,0.04}); //HIS
					put(TMBRecordedAtom.HN, new double[] {0.272,0.306,0.027,0.333,0.047,0.353,0.029,0.339,0.053}); //HIS
					put(TMBRecordedAtom.Cα, new double[] {-0.058,0.24,0.022,0.235,0.013,0.235,0.014,0.234,0.013}); //HIS
					put(TMBRecordedAtom.CP, new double[] {0.597,0.59,0.028,0.563,0.026,0.571,0.023,0.564,0.022}); //HIS
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.586,0.043,-0.663,0.075,-0.69,0.059,-0.657,0.067}); //HIS
					put(TMBRecordedAtom.Cβ, new double[] {-0.007,-0.16,0.012,-0.153,0.022,-0.167,0.02,-0.145,0.019}); //HIS
				}
			});
			put(AminoType.parse("ILE"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.616,0.02,-0.59,0.037,-0.601,0.027,-0.587,0.035}); //ILE
					put(TMBRecordedAtom.HN, new double[] {0.275,0.305,0.031,0.325,0.05,0.341,0.038,0.327,0.061}); //ILE
					put(TMBRecordedAtom.Cα, new double[] {0.06,0.218,0.023,0.214,0.014,0.217,0.013,0.213,0.013}); //ILE
					put(TMBRecordedAtom.CP, new double[] {0.597,0.588,0.028,0.56,0.027,0.562,0.025,0.565,0.024}); //ILE
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.574,0.04,-0.657,0.076,-0.698,0.054,-0.655,0.062}); //ILE
					put(TMBRecordedAtom.Cβ, new double[] {0.13,-0.042,0.011,-0.032,0.011,-0.039,0.008,-0.027,0.009}); //ILE
				}
			});
			put(AminoType.parse("LEU"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.617,0.022,-0.593,0.034,-0.6,0.026,-0.586,0.034}); //LEU
					put(TMBRecordedAtom.HN, new double[] {0.272,0.305,0.03,0.329,0.048,0.349,0.032,0.328,0.063}); //LEU
					put(TMBRecordedAtom.Cα, new double[] {-0.052,0.243,0.023,0.242,0.014,0.245,0.012,0.239,0.014}); //LEU
					put(TMBRecordedAtom.CP, new double[] {0.597,0.585,0.027,0.557,0.027,0.558,0.023,0.563,0.023}); //LEU
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.574,0.039,-0.663,0.079,-0.699,0.051,-0.649,0.065}); //LEU
					put(TMBRecordedAtom.Cβ, new double[] {-0.11,-0.251,0.015,-0.235,0.017,-0.245,0.014,-0.227,0.016}); //LEU
				}
			});
			put(AminoType.parse("LYS"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.624,0.027,-0.585,0.034,-0.592,0.023,-0.586,0.034}); //LYS
					put(TMBRecordedAtom.HN, new double[] {0.272,0.299,0.031,0.332,0.044,0.353,0.028,0.34,0.051}); //LYS
					put(TMBRecordedAtom.Cα, new double[] {-0.24,0.243,0.025,0.238,0.014,0.242,0.011,0.237,0.014}); //LYS
					put(TMBRecordedAtom.CP, new double[] {0.734,0.579,0.035,0.561,0.025,0.56,0.021,0.562,0.021}); //LYS
					put(TMBRecordedAtom.O,  new double[] {-0.589,-0.57,0.046,-0.652,0.069,-0.683,0.05,-0.662,0.056}); //LYS
					put(TMBRecordedAtom.Cβ, new double[] {-0.009,-0.222,0.011,-0.214,0.016,-0.224,0.012,-0.204,0.013}); //LYS
				}
			});
			put(AminoType.parse("MET"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.616,0.024,-0.592,0.032,-0.602,0.025,-0.583,0.037}); //MET
					put(TMBRecordedAtom.HN, new double[] {0.272,0.299,0.047,0.332,0.047,0.354,0.029,0.331,0.06}); //MET
					put(TMBRecordedAtom.Cα, new double[] {-0.024,0.244,0.026,0.244,0.014,0.249,0.011,0.24,0.016}); //MET
					put(TMBRecordedAtom.CP, new double[] {0.597,0.575,0.041,0.559,0.027,0.559,0.025,0.563,0.025}); //MET
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.575,0.037,-0.665,0.075,-0.706,0.05,-0.654,0.063}); //MET
					put(TMBRecordedAtom.Cβ, new double[] {0.003,-0.29,0.012,-0.274,0.015,-0.283,0.012,-0.266,0.014}); //MET
				}
			});
			put(AminoType.parse("PHE"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.616,0.026,-0.589,0.038,-0.603,0.027,-0.581,0.035}); //PHE
					put(TMBRecordedAtom.HN, new double[] {0.272,0.301,0.036,0.328,0.048,0.348,0.031,0.331,0.054}); //PHE
					put(TMBRecordedAtom.Cα, new double[] {-0.002,0.234,0.024,0.235,0.015,0.239,0.014,0.234,0.014}); //PHE
					put(TMBRecordedAtom.CP, new double[] {0.597,0.592,0.025,0.559,0.03,0.569,0.025,0.559,0.026}); //PHE
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.572,0.042,-0.656,0.075,-0.688,0.052,-0.644,0.071}); //PHE
					put(TMBRecordedAtom.Cβ, new double[] {-0.034,-0.168,0.014,-0.156,0.02,-0.171,0.015,-0.148,0.017}); //PHE
				}
			});
			put(AminoType.parse("PRO"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.255,-0.347,0.027,-0.31,0.03,-0.343,0.021}); //PRO
					put(TMBRecordedAtom.Cα, new double[] {-0.027,0.169,0.02,0.17,0.014,0.166,0.009}); //PRO
					put(TMBRecordedAtom.CP, new double[] {0.59,0.594,0.025,0.567,0.035,0.555,0.022}); //PRO
					put(TMBRecordedAtom.O,  new double[] {-0.548,-0.58,0.044,-0.67,0.077,-0.727,0.046}); //PRO
					put(TMBRecordedAtom.Cβ, new double[] {-0.007,-0.209,0.008,-0.196,0.011,-0.207,0.01}); //PRO
				}
			});
			put(AminoType.parse("SER"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.617,0.025,-0.579,0.039,-0.595,0.026,-0.571,0.037}); //SER
					put(TMBRecordedAtom.HN, new double[] {0.272,0.306,0.027,0.332,0.045,0.35,0.027,0.342,0.048}); //SER
					put(TMBRecordedAtom.Cα, new double[] {-0.025,0.208,0.02,0.193,0.014,0.197,0.013,0.19,0.014}); //SER
					put(TMBRecordedAtom.CP, new double[] {0.597,0.59,0.029,0.567,0.026,0.562,0.021,0.566,0.022}); //SER
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.578,0.039,-0.671,0.069,-0.698,0.049,-0.668,0.057}); //SER
					put(TMBRecordedAtom.Cβ, new double[] {0.212,0.075,0.012,0.079,0.017,0.071,0.014,0.085,0.015}); //SER
				}
			});
			put(AminoType.parse("THR"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.606,0.023,-0.573,0.037,-0.589,0.025,-0.569,0.036}); //THR
					put(TMBRecordedAtom.HN, new double[] {0.272,0.307,0.027,0.329,0.046,0.344,0.027,0.336,0.058}); //THR
					put(TMBRecordedAtom.Cα, new double[] {-0.039,0.187,0.019,0.169,0.014,0.173,0.012,0.168,0.014}); //THR
					put(TMBRecordedAtom.CP, new double[] {0.597,0.587,0.031,0.565,0.026,0.563,0.021,0.568,0.026}); //THR
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.574,0.039,-0.659,0.072,-0.69,0.049,-0.663,0.063}); //THR
					put(TMBRecordedAtom.Cβ, new double[] {0.365,0.225,0.011,0.232,0.012,0.225,0.009,0.238,0.01}); //THR
				}
			});
			put(AminoType.parse("TRP"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.612,0.024,-0.589,0.034,-0.602,0.025,-0.575,0.034}); //TRP
					put(TMBRecordedAtom.HN, new double[] {0.272,0.307,0.026,0.326,0.051,0.35,0.041,0.324,0.058}); //TRP
					put(TMBRecordedAtom.Cα, new double[] {-0.028,0.238,0.022,0.233,0.017,0.234,0.02,0.229,0.018}); //TRP
					put(TMBRecordedAtom.CP, new double[] {0.597,0.586,0.036,0.565,0.029,0.568,0.027,0.562,0.025}); //TRP
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.585,0.044,-0.669,0.072,-0.697,0.06,-0.661,0.074}); //TRP
					put(TMBRecordedAtom.Cβ, new double[] {-0.005,-0.146,0.011,-0.133,0.021,-0.142,0.02,-0.124,0.017}); //TRP
				}
			});
			put(AminoType.parse("TYR"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.612,0.025,-0.586,0.035,-0.599,0.026,-0.578,0.035}); //TYR
					put(TMBRecordedAtom.HN, new double[] {0.272,0.307,0.024,0.326,0.051,0.35,0.028,0.325,0.063}); //TYR
					put(TMBRecordedAtom.Cα, new double[] {-0.001,0.237,0.018,0.235,0.015,0.238,0.012,0.233,0.015}); //TYR
					put(TMBRecordedAtom.CP, new double[] {0.597,0.591,0.029,0.561,0.028,0.573,0.024,0.561,0.024}); //TYR
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.568,0.041,-0.662,0.073,-0.69,0.048,-0.663,0.063}); //TYR
					put(TMBRecordedAtom.Cβ, new double[] {-0.015,-0.167,0.011,-0.148,0.02,-0.165,0.015,-0.138,0.016}); //TYR
				}
			});
			put(AminoType.parse("VAL"), new LabeledHash<TMBRecordedAtom, double[]>() {
				private static final long serialVersionUID = 1L;
				{
					put(TMBRecordedAtom.N,  new double[] {-0.416,-0.614,0.02,-0.588,0.036,-0.602,0.026,-0.587,0.034}); //VAL
					put(TMBRecordedAtom.HN, new double[] {0.272,0.306,0.03,0.328,0.049,0.346,0.031,0.331,0.062}); //VAL
					put(TMBRecordedAtom.Cα, new double[] {-0.088,0.22,0.022,0.215,0.014,0.22,0.012,0.214,0.013}); //VAL
					put(TMBRecordedAtom.CP, new double[] {0.597,0.587,0.026,0.56,0.026,0.559,0.024,0.563,0.024}); //VAL
					put(TMBRecordedAtom.O,  new double[] {-0.568,-0.568,0.036,-0.661,0.072,-0.695,0.051,-0.657,0.064}); //VAL
					put(TMBRecordedAtom.Cβ, new double[] {0.299,-0.016,0.011,-0.005,0.012,-0.013,0.008,0,0.009}); //VAL
				}
			});
		}
	};
	
	//used to avoid duplicate error messages
	private static final LabeledSet<AminoType> WARNED = new LabeledSet<AminoType>();
	
	static Set<TMBRecordedAtom> getKeys(AminoType resType) { 
		Objects.requireNonNull(resType, "Null SideChain Type!");
		LabeledHash<TMBRecordedAtom, double[]> chargeHash = TMB_TABLE.get(resType);
		if(chargeHash == null) {
			chargeHash = TMB_TABLE.get(resType.standardize());
		}
		if(chargeHash == null) {
			return null;
		}
		return chargeHash.keySet();
	}
	
	public static double getAtomCharge(AminoType resType, SecondaryStructure secondary, TMBRecordedAtom atom) {
		return getAtomCharge(resType, secondary.simpleClassify(), atom);
	}
	
	public static double getAtomCharge(AminoType resType, SecondarySimple secondary, TMBRecordedAtom atom) {
		Objects.requireNonNull(resType, "Null Residue Type!");
		Objects.requireNonNull(secondary, "Null Secondary Structure!");
		Objects.requireNonNull(atom, "Null Atom Type!");
		
		LabeledHash<TMBRecordedAtom, double[]> chargeHash = TMB_TABLE.get(resType);
		if(chargeHash == null) {
			chargeHash = TMB_TABLE.get(resType.standardize());
		}
		if(chargeHash == null) {
			if(!WARNED.contains(resType)) {
				error("Error assigning charge: amino type \"" + resType + "\" is unrecognized and cannot be approximated.");
				WARNED.add(resType);
			}
			return Double.NaN;
		}
		
		if(chargeHash.get(atom) == null) { return Double.NaN; }
		
		switch(secondary) {
		case Helix:			return chargeHash.get(atom)[TMBRecordedColumn.HELIX.ordinal()];
		case Other:			return chargeHash.get(atom)[TMBRecordedColumn.FREE.ordinal()];
		case Sheet:
			if(chargeHash.get(atom).length > TMBRecordedColumn.SHEET.ordinal()) {
				return chargeHash.get(atom)[TMBRecordedColumn.SHEET.ordinal()];
			} else {
				return Double.NaN;
			}
		case Disordered:
			if(!printedThisSession) {
				qerr("Error, cannot assign charge to disordered residue");
				printedThisSession = true;
			}
			return Double.NaN;
		default:
			qerr("Error, cannot assign charge to residue with unknown secondary structure");
			return Double.NaN;
		}
	}
	
	public static double getAmber95(AminoType resType, TMBRecordedAtom atom) {
		Objects.requireNonNull(resType);
		Objects.requireNonNull(atom);
		
		LabeledHash<TMBRecordedAtom, double[]> chargeHash = TMB_TABLE.get(resType);
		if(chargeHash == null) {
			chargeHash = TMB_TABLE.get(resType.standardize());
		}
		
		if(chargeHash == null) {
			if(!WARNED.contains(resType)) {
				error("Error assigning charge: amino type \"" + resType + "\" is unrecognized and cannot be approximated.");
				WARNED.add(resType);
			}
			return Double.NaN;
		}
		
		//qp(resType + ":" + atom);
		if(chargeHash.get(atom) != null) {
			return chargeHash.get(atom)[TMBRecordedColumn.AMBER95.ordinal()];
		} else {
			//qp("NAN");
			return Double.NaN;
		}
	}
	
	public static double getAvgAmber95(AminoType resType) {
		Objects.requireNonNull(resType);
		
		LabeledHash<TMBRecordedAtom, double[]> chargeHash = TMB_TABLE.get(resType);
		if(chargeHash == null) {
			chargeHash = TMB_TABLE.get(resType.standardize());
		}
		if(chargeHash == null) {
			error("Amino type \"" + resType + "\" is unrecognized and cannot be approximated.");
			return Double.NaN;
		}
		
		double avg = 0;
		for(TMBRecordedAtom atom: chargeHash.keySet()) {
			avg += chargeHash.get(atom)[TMBRecordedColumn.AMBER95.ordinal()];
		}
		avg /= ((double) chargeHash.keySet().size());
		
		return avg;
	}
	
	static AminoAtom generateTMB(TMBRecordedAtom tmbAtom) {
		Objects.requireNonNull(tmbAtom, "Atoms cannot be null!");
		switch(tmbAtom) {
		/* Original Code: preserve in case bug occurs
		 * case CP:		return new AminoAtom(6,6,"Prime Carbon");
		 * case Cα:		return new AminoAtom(6,6,"Alpha Carbon");
		 * case Cβ:		return new AminoAtom(6,6,"Beta Carbon");
		 * case HN:		return new AminoAtom(8,7,"Nitrogen+Hydrogen");
		 * case N:			return new AminoAtom(7,7);	
		 * case O:			return new AminoAtom(8,8);	
		 */
		
		case CP:		return new AminoAtom(6,6, AminoAtom.AMINO_CP);
		case Cα:		return new AminoAtom(6,6, AminoAtom.AMINO_Cα);
		case Cβ:		return new AminoAtom(6,6, AminoAtom.AMINO_Cβ);
		case HN:		return new AminoAtom(8,7, AminoAtom.AMINO_HN);
		case N:			return new AminoAtom(7,7, AminoAtom.AMINO_N);	
		case O:			return new AminoAtom(8,8, AminoAtom.AMINO_O);
		default:		throw new InternalError();
		}
	}
	
	public static void main(String[] args) {
		translateTMB_raw();
	}
	
	//Atom,residue,a95,free,sd,buried,sd,helix,sd,sheet,sheet,sd
	private static void translateTMB_raw() {
		String[] lines = getFileLines("tmb-charges.txt");
		for(String line: lines) {
			String[] tokens = line.split(",");
			StringBuilder builder = new StringBuilder();
			builder.append("put(TMBRecordedAtom."+tokens[0]+", new double[] {");
			for(int index = 2; index < tokens.length; ++index) {
				builder.append(tokens[index] + ",");
			}
			trimLastChar(builder);
			builder.append("}); //"+tokens[1]);
			qp(builder.toString());
		}
	}
}
