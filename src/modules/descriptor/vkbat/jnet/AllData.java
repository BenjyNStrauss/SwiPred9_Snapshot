package modules.descriptor.vkbat.jnet;

import java.util.ArrayList;

import utilities.LocalToolBase;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class AllData extends LocalToolBase {
	protected final int maxSeqLen;
	
	int   []  seqs;
	int   []	secs;
	float []	conserv;
	float 	cav;
	float 	constant;
	float []	smcons;
	//	int   	numseq;
	int   [][]segdef; 
	int   [][]segbin	= new int[4][100]; 
	int   []	numsegs = new int[4]; 
	int   [][]profile;
	int   []	posn	; 
	int   	lens;
	int   [][]profmat;
	int   [][]psimat2; 
	float [][]psimat; 
	float [][]hmmmat;
	
	public AllData(JNet jnetModule) {
		maxSeqLen = jnetModule.maxSeqLen;
		conserv = new float[maxSeqLen];
		smcons	= new float[maxSeqLen];
		segdef	= new int[4][maxSeqLen]; 
		profile = new int[maxSeqLen][24];
		posn	= new int[maxSeqLen];
		profmat	= new int[maxSeqLen][24];
		psimat2 = new int[maxSeqLen][20]; 
		psimat	= new float[maxSeqLen][20];
		hmmmat	= new float[maxSeqLen][24];
	}
	
	public String conserv() {
		StringBuilder megaBuilder = new StringBuilder();
		for(int ii = 0; ii < conserv.length; ++ii) {
			 megaBuilder.append("conserv[" + ii + "] = " + conserv[ii] + "\n");
		}
		megaBuilder.append('\n');
		return megaBuilder.toString();
	}
	
	public ArrayList<String> profMat() {
		ArrayList<String> megaBuilder = new ArrayList<String>();
		StringBuilder lineBuilder = new StringBuilder();
		for(int ii = 0; ii < profmat.length; ++ii) {
			lineBuilder.append("profmat[" + ii + "]: ");
			for(int jj = 0; jj < profmat[ii].length; ++jj) {
				lineBuilder.append(profmat[ii][jj] < 0 ? "" : " ");
				lineBuilder.append(profmat[ii][jj] + ",");
			}
			megaBuilder.add(lineBuilder.toString());
			lineBuilder.setLength(0);
		}
		return megaBuilder;
	}
	
	public String profile() {
		StringBuilder megaBuilder = new StringBuilder();
		for(int ii = 0; ii < profile.length; ++ii) {
			for(int jj = 0; jj < profile[ii].length; ++jj) {
				if(profile[ii][jj] != 0) {
					megaBuilder.append("profile[" + ii + "][" + jj + "] = " + profile[ii][jj] + "\n");
				}
			}
		}
		megaBuilder.append('\n');
		return megaBuilder.toString();
	}
	
	/**
	 * 
	 * @return a string containing data on all of the fields
	 */
	public String toMegaString() {
		StringBuilder megaBuilder = new StringBuilder();
		
		for(int jj = 0; jj < seqs.length; ++jj) {
			megaBuilder.append("seqs[" + jj + "] = " + seqs[jj] + "\n");
		}
		
		megaBuilder.append('\n');
		
		if(secs != null) {
			for(int ii = 0; ii < secs.length; ++ii) {
					megaBuilder.append("secs[" + ii + "] = " + secs[ii] + "\n");
			}
		} else {
			megaBuilder.append("secs = null\n");
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < conserv.length; ++ii) {
			megaBuilder.append("conserv[" + ii + "] = " + conserv[ii] + "\n");
		}
		megaBuilder.append('\n');
		
		megaBuilder.append("cav = " + cav);
		megaBuilder.append('\n');
		megaBuilder.append("constant = " + constant);
		megaBuilder.append("\n\n");
		
		for(int ii = 0; ii < smcons.length; ++ii) {
			megaBuilder.append("smcons[" + ii + "] = " + smcons[ii] + "\n");
		}
		megaBuilder.append('\n');
		
		//megaBuilder.append("numseq = " + numseq);
		//megaBuilder.append('\n');
		
		for(int ii = 0; ii < segdef.length; ++ii) {
			for(int jj = 0; jj < segdef[ii].length; ++jj) {
				megaBuilder.append("segdef[" + ii + "][" + jj + "] = " + segdef[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < segbin.length; ++ii) {
			for(int jj = 0; jj < segbin[ii].length; ++jj) {
				megaBuilder.append("segbin[" + ii + "][" + jj + "] = " + segbin[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < numsegs.length; ++ii) {
			megaBuilder.append("numsegs[" + ii + "] = " + numsegs[ii] + "\n");
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < profile.length; ++ii) {
			for(int jj = 0; jj < profile[ii].length; ++jj) {
				megaBuilder.append("profile[" + ii + "][" + jj + "] = " + profile[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < posn.length; ++ii) {
			megaBuilder.append("posn[" + ii + "] = " + posn[ii] + "\n");
		}
		megaBuilder.append('\n');
		
		megaBuilder.append("lens = " + lens);
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < profmat.length; ++ii) {
			for(int jj = 0; jj < profmat[ii].length; ++jj) {
				megaBuilder.append("profmat[" + ii + "][" + jj + "] = " + profmat[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < psimat2.length; ++ii) {
			for(int jj = 0; jj < psimat2[ii].length; ++jj) {
				megaBuilder.append("psimat2[" + ii + "][" + jj + "] = " + psimat2[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < psimat.length; ++ii) {
			for(int jj = 0; jj < psimat[ii].length; ++jj) {
				megaBuilder.append("psimat[" + ii + "][" + jj + "] = " + psimat[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		for(int ii = 0; ii < hmmmat.length; ++ii) {
			for(int jj = 0; jj < hmmmat[ii].length; ++jj) {
				megaBuilder.append("hmmmat[" + ii + "][" + jj + "] = " + hmmmat[ii][jj] + "\n");
			}
		}
		megaBuilder.append('\n');
		
		return megaBuilder.toString();
	}
}
