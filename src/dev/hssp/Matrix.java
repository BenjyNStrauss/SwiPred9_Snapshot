package dev.hssp;

/**
 * from blast.cpp
 * @translator Benjamin Strauss
 *
 */

public class Matrix {
	private MMatrixData mData;
	
	public Matrix(String inName, int inGapOpen, int inGapExtend) {
		mData.mName = null;
		for (MMatrixData data = kMMatrixData; data.mName != null; ++data) {
			if (ba::iequals(inName, data.mName) && inGapOpen == data.mGapOpen && inGapExtend == data.mGapExtend) {
				mData = data;
				break;
			}
		}

		if (mData.mName == null) {
		    throw new mas_exception("Unsupported matrix/gap combination ("+inName+"/"+inGapOpen+"/"+inGapExtend+")");
		}
	}

	public byte operator_opcp (char inAA1, char inAA2) {
		return operator_opcp(ResidueNr(inAA1), ResidueNr(inAA2));
	}
	
	public byte operator_opcp (byte inAA1, byte inAA2) {
		byte result;

		if (inAA1 >= inAA2) {
			result = mData.mMatrix[(inAA1 * (inAA1 + 1)) / 2 + inAA2];
		} else {
		    result = mData.mMatrix[(inAA2 * (inAA2 + 1)) / 2 + inAA1];
		}

		return result;
	}

	public int OpenCost() { return mData.mGapOpen; }
	public int ExtendCost() { return mData.mGapExtend; }

	public double GappedLambda() { return mData.mGappedStats.lambda; }
	public double GappedKappa() { return mData.mGappedStats.kappa; }
	public double GappedEntropy() { return mData.mGappedStats.entropy; }
	public double GappedAlpha() { return mData.mGappedStats.alpha; }
	public double GappedBeta() { return mData.mGappedStats.beta; }

	public double UngappedLambda() { return mData.mUngappedStats.lambda; }
	public double UngappedKappa() { return mData.mUngappedStats.kappa; }
	public double UngappedEntropy() { return mData.mUngappedStats.entropy; }
	public double UngappedAlpha() { return mData.mUngappedStats.alpha; }
	public double UngappedBeta() { return mData.mUngappedStats.beta; }	  
}
