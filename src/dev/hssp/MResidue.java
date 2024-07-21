package dev.hssp;

import assist.Deconstructable;
import assist.numerical.Quaternion;
import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.ostream;
import assist.util.Pair;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class MResidue implements Deconstructable, HSSP_Constants {
	protected String mChainID;
	protected MResidue mPrev;
	protected MResidue mNext;
	protected long mSeqNumber, mNumber;
	protected String mInsertionCode;
	protected MResidueType mType;
	protected short mSSBridgeNr;
	protected double mAccessibility;
	protected MSecondaryStructure  mSecondaryStructure;
	protected MAtom mC, mN, mCA, mO, mH;
	protected HBond[] mHBondDonor = new HBond[2];
	protected HBond[] mHBondAcceptor = new HBond[2];
	protected Vector<MAtom> mSideChain;
	protected MBridgeParner[] mBetaPartner = new MBridgeParner[2];
	protected long mSheet;
	protected MHelixFlag[] mHelixFlags = new MHelixFlag[3];  //
	protected boolean mBend;
	// The 3D box containing all atoms
	protected MPoint[] mBox = new MPoint[2];
	// and the 3d Sphere containing all atoms
	protected MPoint mCenter;
	protected double mRadius;
	
	public MResidue(MResidue residue) {
		//TODO
	}
	
	public MResidue(int inNumber, char inTypeCode, MResidue inPrevious) {
		//TODO
	}
	
	public MResidue(int inNumber, MResidue inPrevious, Vector<MAtom> inAtoms) {
		//TODO
	}
	
	public void SetChainID(String inChainID) {
		mChainID = inChainID;

		mC.SetChainID(inChainID);
		mCA.SetChainID(inChainID);
		mO.SetChainID(inChainID);
		mN.SetChainID(inChainID);
		mH.SetChainID(inChainID);
		for(mSideChain.begin(), mSideChain.end(), boost.bind(MAtom.SetChainID, _1, inChainID));
	}
	
	public String GetChainID() { return mChainID; }
	
	public MResidueType GetType() { return mType; }
	
	public MAtom GetCAlpha() { return mCA; }
	public MAtom GetC() { return mC; }
	public MAtom GetN() { return mN; }
	public MAtom GetO() { return mO; }
	public MAtom GetH() { return mH; }
	
	public double Phi() {
		double result = 360;
		if (mPrev != null && NoChainBreak(mPrev, this)) {
			result = DihedralAngle(mPrev->GetC(), GetN(), GetCAlpha(), GetC());
		}
		return result;
	}
	
	public double Psi() {
		double result = 360;
		if (mNext != null && NoChainBreak(this, mNext)) {
			result = DihedralAngle(GetN(), GetCAlpha(), GetC(), mNext.GetN());
		}
		return result;
	}
	
	public Pair<Double, Character> Alpha() {
		double alpha = 360;
		char chirality = ' ';

		MResidue nextNext = (mNext != null) ? mNext.Next() : null;
		if (mPrev != null && nextNext != null && NoChainBreak(mPrev, nextNext)) {
			alpha = DihedralAngle(mPrev.GetCAlpha(), GetCAlpha(), mNext.GetCAlpha(), nextNext.GetCAlpha());
			if (alpha < 0) {
				chirality = '-';
			} else {
				chirality = '+';
			}
		}
		return new Pair<Double, Character>(alpha, chirality);
	}
	
    public double Kappa() {
    	  double result = 360;
    	  MResidue prevPrev = (mPrev != null) ? mPrev.Prev() : null;
    	  MResidue nextNext = (mNext != null) ? mNext.Next() : null;
    	  if (prevPrev != null && nextNext != null && NoChainBreak(prevPrev, nextNext)) {
    		  double ckap = primatives_3d.CosinusAngle(GetCAlpha(), prevPrev.GetCAlpha(), nextNext.GetCAlpha(), GetCAlpha());
    		  double skap = Math.sqrt(1 - ckap * ckap);
    	    result = Math.atan2(skap, ckap) * 180 / kPI;
    	  }
    	  return result;
    	}
    
	public double TCO() {
		double result = 0;
		if (mPrev != null && NoChainBreak(mPrev, this)) {
			result = primatives_3d.CosinusAngle(GetC(), GetO(), mPrev.GetC(), mPrev.GetO());
		}
		return result;
	}
	
	public double Accessibility() { return mAccessibility; }
	
	public void SetSecondaryStructure(MSecondaryStructure inSS) { mSecondaryStructure = inSS; }
	
	public MSecondaryStructure GetSecondaryStructure() { return mSecondaryStructure; }
	
	public MResidue Next() { return mNext; }
	public MResidue Prev() { return mPrev; }
	
	public void SetPrev(MResidue inResidue) {
		mPrev = inResidue;
		mPrev.mNext = this;
	}
	
	public void SetBetaPartner(int n, MResidue inResidue, long inLadder, boolean inParallel) {
		assert(n == 0 || n == 1);

		mBetaPartner[n].residue = inResidue;
		mBetaPartner[n].ladder = inLadder;
		mBetaPartner[n].parallel = inParallel;
	}
	
	public MBridgeParner GetBetaPartner(int n) {
		assert(n == 0 || n == 1);
		return mBetaPartner[n];
	}
	
	public void SetSheet(long inSheet) { mSheet = inSheet; }
	public long GetSheet() { return mSheet; }
	
	public boolean IsBend() { return mBend; }
	public void SetBend(boolean inBend) { mBend = inBend; }
	
	public MHelixFlag GetHelixFlag(int inHelixStride) {
		assert(inHelixStride == 3 || inHelixStride == 4 || inHelixStride == 5);
		return mHelixFlags[inHelixStride - 3];
	}
	
	public boolean IsHelixStart(int inHelixStride) {
		assert(inHelixStride == 3 || inHelixStride == 4 || inHelixStride == 5);
		return mHelixFlags[inHelixStride - 3] == helixStart || mHelixFlags[inHelixStride - 3] == helixStartAndEnd;
	}
	
	public void SetHelixFlag(int inHelixStride, MHelixFlag inHelixFlag) {
		assert(inHelixStride == 3 || inHelixStride == 4 || inHelixStride == 5);
		mHelixFlags[inHelixStride - 3] = inHelixFlag;
	}
	
	public void SetSSBridgeNr(short inBridgeNr){
		if (mType != MResidueType.kCysteine) {
		    throw new mas_exception("Only cysteine residues can form sulphur bridges");
		}
		mSSBridgeNr = inBridgeNr;
	}
	
	public short GetSSBridgeNr() {
		if (mType != MResidueType.kCysteine) {
			throw new mas_exception("Only cysteine residues can form sulphur bridges");
		}
		return mSSBridgeNr;
	}
	
	public void AddAtom(MAtom inAtom) {
		//TODO find implementation!
	}
		
	public HBond[] Donor() { return mHBondDonor; }
	public HBond[] Acceptor() { return mHBondAcceptor; }
	
	public boolean ValidDistance(MResidue inNext) {
		return Distance(GetC(), inNext.GetN()) <= kMaxPeptideBondLength;
	}
	
	public static boolean TestBond(MResidue a, MResidue b) {
		return a.TestBond(b);
	}
	
	// bridge functions
	public MBridgeType TestBridge(MResidue inResidue) {
		return Distance(GetC(), inNext.GetN()) <= kMaxPeptideBondLength;
	}
	
	public long GetSeqNumber() { return mSeqNumber; }
	public String GetInsertionCode() { return mInsertionCode; }
	
	public void SetNumber(long inNumber) { mNumber = inNumber; }
	public long GetNumber() { return mNumber; }
	
	public void Translate(MPoint inTranslation) {
		mN.Translate(inTranslation);
		mCA.Translate(inTranslation);
		mC.Translate(inTranslation);
		mO.Translate(inTranslation);
		mH.Translate(inTranslation);
		for_each(mSideChain.begin(), mSideChain.end(), boost.bind(MAtom.Translate, _1, inTranslation));
	}
	
	public void Rotate(Quaternion inRotation) {
		mN.Rotate(inRotation);
		mCA.Rotate(inRotation);
		mC.Rotate(inRotation);
		mO.Rotate(inRotation);
		mH.Rotate(inRotation);
		for_each(mSideChain.begin(), mSideChain.end(), boost.bind(MAtom.Rotate, _1, inRotation));
	}
	
	public void WritePDB(ostream os) {
		mN.WritePDB(os);
		mCA.WritePDB(os);
		mC.WritePDB(os);
		mO.WritePDB(os);

		for(mSideChain.begin(), mSideChain.end(), boost, bind(MAtom.WritePDB, _1, boost.ref(os)));
	}
	
	static double CalculateHBondEnergy(MResidue inDonor, MResidue inAcceptor) {
		double result = 0;

		if (inDonor.mType != MResidueType.kProline){
			double distanceHO = Distance(inDonor.GetH(), inAcceptor.GetO());
			double distanceHC = Distance(inDonor.GetH(), inAcceptor.GetC());
		    double distanceNC = Distance(inDonor.GetN(), inAcceptor.GetC());
		    double distanceNO = Distance(inDonor.GetN(), inAcceptor.GetO());

		    if (distanceHO < kMinimalDistance || distanceHC < kMinimalDistance ||
		    		distanceNC < kMinimalDistance || distanceNO < kMinimalDistance) {
		    	result = kMinHBondEnergy;
		    } else {
		    	result = kCouplingConstant / distanceHO - kCouplingConstant / distanceHC + kCouplingConstant / distanceNC - kCouplingConstant / distanceNO;
		    }
		    // DSSP compatibility mode:
		    result = bm.round(result * 1000) / 1000;

		    if (result < kMinHBondEnergy) {
		    	result = kMinHBondEnergy;
		    }
		}

		// update donor
		if (result < inDonor.mHBondAcceptor[0].energy) {
			inDonor.mHBondAcceptor[1] = inDonor.mHBondAcceptor[0];
			inDonor.mHBondAcceptor[0].residue = inAcceptor;
			inDonor.mHBondAcceptor[0].energy = result;
		} else if (result < inDonor.mHBondAcceptor[1].energy) {
		    inDonor.mHBondAcceptor[1].residue = inAcceptor;
		    inDonor.mHBondAcceptor[1].energy = result;
		}
		
		// and acceptor
		if (result < inAcceptor.mHBondDonor[0].energy) {
		    inAcceptor.mHBondDonor[1] = inAcceptor.mHBondDonor[0];
		    inAcceptor.mHBondDonor[0].residue = inDonor;
		    inAcceptor.mHBondDonor[0].energy = result;
		} else if (result < inAcceptor.mHBondDonor[1].energy) {
		    inAcceptor.mHBondDonor[1].residue = inDonor;
		    inAcceptor.mHBondDonor[1].energy = result;
		}

		return result;
	}
	
	public Vector<MAtom> GetSideChain() { return mSideChain; }
	
	public void GetPoints(Vector<MPoint> outPoints) {
		outPoints.push_back(mN);
		outPoints.push_back(mCA);
		outPoints.push_back(mC);
		outPoints.push_back(mO);
		for(MAtom a: mSideChain) {
			outPoints.push_back(a);
		}
	}
	
	public void CalculateSurface(Vector<MResidue> inResidues) {
		Vector<MResidue> neighbours = new Vector<MResidue>();

		for (MResidue r: inResidues) {
		    MPoint center;
		    double radius;
		    r.GetCenterAndRadius(center, radius);

		    if (Distance(mCenter, center) < mRadius + radius) {
		    	neighbours.push_back(r);
		    }
		}

		mAccessibility = CalculateSurface(mN, kRadiusN, neighbours) +
				CalculateSurface(mCA, kRadiusCA, neighbours) +
				CalculateSurface(mC, kRadiusC, neighbours) +
				CalculateSurface(mO, kRadiusO, neighbours);

		for(MAtom atom: mSideChain) {
		    mAccessibility += CalculateSurface(atom, kRadiusSideAtom, neighbours);
		}
	}
	
	public Pair<MPoint, Double> GetCenterAndRadius() {
		new Pair<MPoint, Double>(mCenter, mRadius);
	}
	
	public static boolean NoChainBreak(MResidue from, MResidue to) {
		boolean result = true;
		for (MResidue r = from; result && r != to; r = r.mNext) {
			MResidue next = r.mNext;
			if (next == null) {
				result = false;
			} else {
				result = next.mNumber == r.mNumber + 1;
			}
		}
		return result;
	}
	
	protected double CalculateSurface(MAtom inAtom, double inRadius, Vector<MResidue> inResidues) {
		MAccumulator accumulate = new MAccumulator();

		for (MResidue r: inResidues) {
		    if (r.AtomIntersectsBox(inAtom, inRadius)) {
		    	accumulate.operator_parens(inAtom, r.mN, inRadius, kRadiusN);
		    	accumulate.operator_parens(inAtom, r.mCA, inRadius, kRadiusCA);
		    	accumulate.operator_parens(inAtom, r.mC, inRadius, kRadiusC);
		    	accumulate.operator_parens(inAtom, r.mO, inRadius, kRadiusO);

		    	for (MAtom atom: r.mSideChain) {
		    		accumulate.operator_parens(inAtom, atom, inRadius, kRadiusSideAtom);
		    	}
		    }
		}

		accumulate.sort();

		double radius = inRadius + kRadiusWater;
		double surface = 0;

		MSurfaceDots surfaceDots = MSurfaceDots.Instance();

		for (int i = 0; i < surfaceDots.size(); ++i) {
			MPoint xx = surfaceDots.operator_brackets(i) * radius;

			boolean free = true;
			for (int k = 0; free && k < accumulate.m_x.size(); ++k) {
				free = accumulate.m_x.get(k).radius < DistanceSquared(xx, accumulate.m_x.get(k).location);
			}

			if (free) {
				surface += surfaceDots.weight();
			}
		}

		return surface * radius * radius;
	}
	
	protected boolean TestBond(MResidue other) {
		return (mHBondAcceptor[0].residue == other && mHBondAcceptor[0].energy < kMaxHBondEnergy) ||
				(mHBondAcceptor[1].residue == other && mHBondAcceptor[1].energy < kMaxHBondEnergy);
	}
	
	protected void ExtendBox(MAtom atom, double inRadius) {
		if (mBox[0].mX > atom.mLoc.mX - inRadius) {
			mBox[0].mX = atom.mLoc.mX - inRadius;
		}
		if (mBox[0].mY > atom.mLoc.mY - inRadius) {
			mBox[0].mY = atom.mLoc.mY - inRadius;
		}
		if (mBox[0].mZ > atom.mLoc.mZ - inRadius) {
			mBox[0].mZ = atom.mLoc.mZ - inRadius;
		}
		if (mBox[1].mX < atom.mLoc.mX + inRadius) {
			mBox[1].mX = atom.mLoc.mX + inRadius;
		}
		if (mBox[1].mY < atom.mLoc.mY + inRadius) {
			mBox[1].mY = atom.mLoc.mY + inRadius;
		}
		if (mBox[1].mZ < atom.mLoc.mZ + inRadius) {
			mBox[1].mZ = atom.mLoc.mZ + inRadius;
		}
	}
	
	protected boolean AtomIntersectsBox(MAtom atom, double inRadius) {
		return atom.mLoc.mX + inRadius >= mBox[0].mX && atom.mLoc.mX - inRadius <= mBox[1].mX &&
				atom.mLoc.mY + inRadius >= mBox[0].mY && atom.mLoc.mY - inRadius <= mBox[1].mY &&
				atom.mLoc.mZ + inRadius >= mBox[0].mZ && atom.mLoc.mZ - inRadius <= mBox[1].mZ;
	}
	
	private MResidue operator_eq(MResidue residue);
	
	@SuppressWarnings("deprecation")
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
