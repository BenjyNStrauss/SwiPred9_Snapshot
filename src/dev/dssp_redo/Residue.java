package dev.dssp_redo;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import assist.numerical.DecimalCoordinate;
import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.Vector;
import assist.util.Pair;
import biology.molecule.types.AminoType;
import dev.cif.cif;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Residue extends CppTranslator {
	Residue mNext = null;
	Residue mPrev = null;

	// const Monomer &mM;

	String mAsymID;
	int mSeqID;
	String mCompoundID;
	String mAltID;
	int mNumber;
	boolean mComplete = false;

	String mAuthAsymID;
	int mAuthSeqID;

	String mPDBStrandID;
	int mPDBSeqNum;
	String mPDBInsCode;

	DecimalCoordinate mCAlpha, mC, mN, mO, mH;
	DecimalCoordinate[] mBox = new DecimalCoordinate[2];
	double mRadius;
	DecimalCoordinate mCenter;
	Vector<Pair<String, DecimalCoordinate>> mSideChain;
	double mAccessibility = 0;
	double mChiralVolume = 0;

	// float mAlpha = 360, mKappa = 360, mPhi = 360, mPsi = 360, mTCO = 0, mOmega = 360;
	OptionalDouble mAlpha;
	OptionalDouble mKappa;
	OptionalDouble mPhi;
	OptionalDouble mPsi;
	OptionalDouble mTCO;
	OptionalDouble mOmega;

	AminoType mType;
	int mSSBridgeNr = 0;
	StructureType mSecondaryStructure = StructureType.Loop;
	HBond[] mHBondDonor = new HBond[2];
	HBond[] mHBondAcceptor = new HBond[2];
	BridgePartner[] mBetaPartner = new BridgePartner[2];
	int mSheet = 0;
	int mStrand = 0;	// Added to ease the writing of mmCIF's struct_sheet and friends
	HelixPositionType[] mHelixFlags = { HelixPositionType.None, HelixPositionType.None, HelixPositionType.None, HelixPositionType.None }; //
	boolean mBend = false;
	ChainBreakType mChainBreak = ChainBreakType.None;

	int m_seen = 0, m_model_nr = 0;
	DecimalCoordinate[] m_chiralAtoms = new DecimalCoordinate[4];
	
	//Residue(residue &&) = default;
	//Residue &operator=(residue &&) = default;

	Residue(int model_nr, String pdb_strand_id, int pdb_seq_num, String pdb_ins_code) {
		mPDBStrandID = pdb_strand_id;
		mPDBSeqNum = pdb_seq_num;
		mPDBInsCode = pdb_ins_code;
		mChainBreak = ChainBreakType.None;
		m_model_nr = model_nr;
		
		// update the box containing all atoms
		mBox[0].x = mBox[0].y = mBox[0].z = Double.MAX_VALUE;
		mBox[1].x = mBox[1].y = mBox[1].z = -Double.MAX_VALUE;

		mH = new DecimalCoordinate( Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE );

		for (int i = 0; i < m_chiralAtoms.length; i++) {
			m_chiralAtoms[i] = new DecimalCoordinate(0,0,0);
		}
	}

	void addAtom(cif.row_handle atom) {
		String asymID, compID, atomID, type, authAsymID;
		Optional<String> altID;
		int seqID, authSeqID;
		OptionalInt model;
		float x, y, z;

		cif.tie(asymID, compID, atomID, altID, type, seqID, model, x, y, z, authAsymID, authSeqID) =
			atom.get("label_asym_id", "label_comp_id", "label_atom_id", "label_alt_id", "type_symbol", "label_seq_id",
				"pdbx_PDB_model_num", "Cartn_x", "Cartn_y", "Cartn_z",
				"auth_asym_id", "auth_seq_id");

		if (model.isPresent() && model.getAsInt() != m_model_nr) {
			return;
		}

		if (m_seen == 0) {
			mAsymID = asymID;
			mCompoundID = compID;
			mSeqID = seqID;

			mAuthSeqID = authSeqID;
			mAuthAsymID = authAsymID;

			mType = AminoType.parse(mCompoundID);

			if (altID.isPresent()) {
				mAltID = altID.get();
			}
		}

		switch(atomID) {
		case "CA":
			m_seen |= 1;
			mCAlpha = new DecimalCoordinate( x, y, z );
			ExtendBox(mCAlpha, DSSP_cpp.kRadiusCA + 2 * DSSP_cpp.kRadiusWater);

			if (mType == AminoType.Valine) {
				m_chiralAtoms[1] = mCAlpha;
			}
			break;
		case "C":
			m_seen |= 2;
			mC = new DecimalCoordinate( x, y, z );
			ExtendBox(mC, DSSP_cpp.kRadiusC + 2 * DSSP_cpp.kRadiusWater);
			break;
		case "N":	
			m_seen |= 4;
			mH = mN = new DecimalCoordinate( x, y, z );
			ExtendBox(mN, DSSP_cpp.kRadiusN + 2 * DSSP_cpp.kRadiusWater);
			break;
		case "O":
			m_seen |= 8;
			mO = new DecimalCoordinate( x, y, z );
			ExtendBox(mO, DSSP_cpp.kRadiusO + 2 * DSSP_cpp.kRadiusWater);
			break;
		case "H":
			m_seen |= 16;
			mSideChain.emplace_back(new Pair<>(atomID, new DecimalCoordinate( x, y, z )));
			ExtendBox(new DecimalCoordinate( x, y, z ), DSSP_cpp.kRadiusSideAtom + 2 * DSSP_cpp.kRadiusWater);

			if (mType == AminoType.Leucine) {
				switch(atomID) {
				case "CG":	m_chiralAtoms[0] = new DecimalCoordinate( x, y, z );	break;
				case "CB":	m_chiralAtoms[1] = new DecimalCoordinate( x, y, z );	break;
				case "CD1":	m_chiralAtoms[2] = new DecimalCoordinate( x, y, z );	break;
				case "CD2":	m_chiralAtoms[3] = new DecimalCoordinate( x, y, z );	break;
				}
			} else if (mType == AminoType.Valine) {
				switch(atomID) {
				case "CB":	m_chiralAtoms[0] = new DecimalCoordinate( x, y, z );	break;
				case "CG1":	m_chiralAtoms[2] = new DecimalCoordinate( x, y, z );	break;
				case "CG2":	m_chiralAtoms[3] = new DecimalCoordinate( x, y, z );	break;
				}
			}
			
		}
	}

	void finish() {
		final int kSeenAll = (1 | 2 | 4 | 8);
		mComplete = (m_seen & kSeenAll) == kSeenAll;

		if (mType == AminoType.Valine || mType == AminoType.Leucine) {
			mChiralVolume = DSSP_cpp.dot_product(DSSP_cpp.op_minus(m_chiralAtoms[1], m_chiralAtoms[0]),
					DSSP_cpp.cross_product(DSSP_cpp.op_minus(m_chiralAtoms[2], m_chiralAtoms[0]), DSSP_cpp.op_minus(m_chiralAtoms[3], m_chiralAtoms[0])));
		}

		mRadius = mBox[1].x - mBox[0].x;
		if (mRadius < mBox[1].y - mBox[0].y) {
			mRadius = mBox[1].y - mBox[0].y;
		}
		if (mRadius < mBox[1].z - mBox[0].z) {
			mRadius = mBox[1].z - mBox[0].z;
		}

		mCenter.x = (mBox[0].x + mBox[1].x) / 2;
		mCenter.y = (mBox[0].y + mBox[1].y) / 2;
		mCenter.z = (mBox[0].z + mBox[1].z) / 2;
	}

	void assignHydrogen() {
		// assign the Hydrogen
		mH = mN;

		if (mType != AminoType.Proline && mPrev != null) {
			DecimalCoordinate pc = mPrev.mC;
			DecimalCoordinate po = mPrev.mO;

			double CODistance = DSSP_cpp.distance(pc, po);

			mH.x += (pc.x - po.x) / CODistance;
			mH.y += (pc.y - po.y) / CODistance;
			mH.z += (pc.z - po.z) / CODistance;
		}
	}

	void SetSecondaryStructure(StructureType inSS) { mSecondaryStructure = inSS; }
	StructureType GetSecondaryStructure() { return mSecondaryStructure; }

	void SetBetaPartner(int n, Residue inResidue, int inLadder, boolean inParallel) {
		if(!(n == 0 || n == 1)) { throw new AssertionError(); }

		mBetaPartner[n].m_residue = inResidue;
		mBetaPartner[n].ladder = inLadder;
		mBetaPartner[n].parallel = inParallel;
	}

	BridgePartner GetBetaPartner(int n) {
		if(!(n == 0 || n == 1)) { throw new AssertionError(); }
		return mBetaPartner[n];
	}

	void SetSheet(int inSheet) { mSheet = inSheet; }
	int GetSheet() { return mSheet; }

	void SetStrand(int inStrand) { mStrand = inStrand; }
	int GetStrand() { return mStrand; }

	boolean IsBend() { return mBend; }
	void SetBend(boolean inBend) { mBend = inBend; }

	HelixPositionType GetHelixFlag(HelixType helixType) {
		int stride = helixType.ordinal();
		__assert__(stride < 4);
		return mHelixFlags[stride];
	}

	boolean IsHelixStart(HelixType helixType) {
		int stride = helixType.ordinal();
		__assert__(stride < 4);
		return mHelixFlags[stride] == HelixPositionType.Start || mHelixFlags[stride] == HelixPositionType.StartAndEnd;
	}

	void SetHelixFlag(HelixType helixType, HelixPositionType inHelixFlag) {
		int stride = helixType.ordinal();
		__assert__(stride < 4);
		mHelixFlags[stride] = inHelixFlag;
	}

	void SetSSBridgeNr(int inBridgeNr) {
		if (mType != AminoType.Cysteine) {
			throw new RuntimeException("Only cysteine residues can form sulphur bridges");
		}
		mSSBridgeNr = inBridgeNr;
	}

	long GetSSBridgeNr() {
		if (mType != AminoType.Cysteine) {
			throw new RuntimeException("Only cysteine residues can form sulphur bridges");
		}
		return mSSBridgeNr;
	}

	double CalculateSurface(final Vector<Residue> inResidues) {
		Vector<Residue> neighbours = new Vector<Residue>();

		for (Residue r : inResidues) {
			DecimalCoordinate center = r.mCenter;
			double radius = r.mRadius;

			if (DSSP_cpp.distance_sq(mCenter, center) < (mRadius + radius) * (mRadius + radius)) {
				neighbours.push_back(r);
			}
		}

		mAccessibility = CalculateSurface(mN, DSSP_cpp.kRadiusN, neighbours) +
		                 CalculateSurface(mCAlpha, DSSP_cpp.kRadiusCA, neighbours) +
		                 CalculateSurface(mC, DSSP_cpp.kRadiusC, neighbours) +
		                 CalculateSurface(mO, DSSP_cpp.kRadiusO, neighbours);

		//for (const auto &[name, atom] : mSideChain)
		for(Pair<String, DecimalCoordinate> pair: mSideChain) {
			mAccessibility += CalculateSurface(pair.y, DSSP_cpp.kRadiusSideAtom, neighbours);
		}

		return mAccessibility;
	}
	
	float CalculateSurface(final DecimalCoordinate inAtom, float inRadius, final Vector<Residue> inNeighbours) {
		Accumulator accumulate = new Accumulator();

		for (Residue r : inNeighbours) {
			if (r.AtomIntersectsBox(inAtom, inRadius)) {
				accumulate.apply(inAtom, r.mN, inRadius, DSSP_cpp.kRadiusN);
				accumulate.apply(inAtom, r.mCAlpha, inRadius, DSSP_cpp.kRadiusCA);
				accumulate.apply(inAtom, r.mC, inRadius, DSSP_cpp.kRadiusC);
				accumulate.apply(inAtom, r.mO, inRadius, DSSP_cpp.kRadiusO);

				for (final Pair<String, DecimalCoordinate> pair : r.mSideChain) {
					accumulate.apply(inAtom, pair.y, inRadius, DSSP_cpp.kRadiusSideAtom);
				}
			}
		}

		accumulate.sort();

		float radius = inRadius + DSSP_cpp.kRadiusWater;
		float surface = 0;

		MSurfaceDots surfaceDots = MSurfaceDots.Instance();

		for (int i = 0; i < surfaceDots.size(); ++i) {
			DecimalCoordinate tmp = surfaceDots.operator_brackets(i);
			//Could this be a break
			DecimalCoordinate xx = new DecimalCoordinate(tmp.x * radius, tmp.y* radius, tmp.z* radius);

			boolean free = true;
			for (int k = 0; free && k < accumulate.m_x.size(); ++k) {
				free = accumulate.m_x.get(k).radius < DSSP_cpp.distance_sq(xx, accumulate.m_x.get(k).location);
			}
				
			if (free) {
				surface += surfaceDots.weight();
			}
		}

		return surface * radius * radius;
	}

	boolean AtomIntersectsBox(final DecimalCoordinate atom, float inRadius) {
		return atom.x + inRadius >= mBox[0].x &&
		       atom.x - inRadius <= mBox[1].x &&
		       atom.y + inRadius >= mBox[0].y &&
		       atom.y - inRadius <= mBox[1].y &&
		       atom.z + inRadius >= mBox[0].z &&
		       atom.z - inRadius <= mBox[1].z;
	}

	void ExtendBox(DecimalCoordinate atom, float inRadius) {
		if (mBox[0].x > atom.x - inRadius) {
			mBox[0].x = atom.x - inRadius;
		}
		if (mBox[0].y > atom.y - inRadius) {
			mBox[0].y = atom.y - inRadius;
		}
		if (mBox[0].z > atom.z - inRadius) {
			mBox[0].z = atom.z - inRadius;
		}
		if (mBox[1].x < atom.x + inRadius) {
			mBox[1].x = atom.x + inRadius;
		}
		if (mBox[1].y < atom.y + inRadius) {
			mBox[1].y = atom.y + inRadius;
		}
		if (mBox[1].z < atom.z + inRadius) {
			mBox[1].z = atom.z + inRadius;
		}
	}

	DecimalCoordinate get_atom(String name) {
		switch(name) {
		case "CA":		return mCAlpha;
		case "C":		return mC;
		case "N":		return mN;
		case "O":		return mO;
		case "H":		return mH;
		default:
			for (final Pair<String, DecimalCoordinate> pair : mSideChain){
				if (pair.x.equals(name)) {
					return pair.y;
				}
			}
		}
		
		return new DecimalCoordinate(0,0,0);
	}
};
