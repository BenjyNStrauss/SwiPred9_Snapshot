package dev.hssp;

import assist.Deconstructable;
import assist.numerical.Quaternion;
import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.ostream;

/**
 * from structure.h
 * @translator Benjamin Strauss
 *
 */

public class MChain implements Deconstructable {
	private String mChainID, mAuthChainID;
	private Vector<MResidue> mResidues;
	
	public MChain(MChain chain) {
		MResidue previous = null;

		for (MResidue residue: chain.mResidues){
			MResidue newResidue = new MResidue(residue);
			newResidue.SetPrev(previous);
		    mResidues.push_back(newResidue);
		    previous = newResidue;
		}
	}
	
	public MChain(String inChainID) {
		mChainID = (inChainID);
	}
	
	public MChain operator_eq(MChain chain) {
		for (MResidue residue: mResidues) {
			residue.deconstruct();
		}
		mResidues.clear();

		for ( MResidue residue: chain.mResidues) {
			mResidues.push_back(new MResidue(residue));
		}

		mChainID = chain.mChainID;

		return this;
	}
	
	public String GetChainID() { return mChainID; }
	
	public void SetChainID(String inChainID) {
		mChainID = inChainID;
		for(mResidues.begin(), mResidues.end(), boost::bind(MResidue::SetChainID, _1, inChainID));
	}
	
	public String GetAuthChainID() {
		return mAuthChainID;
	}
	
	public void SetAuthChainID(String inAuthChainID) {
		mAuthChainID = inAuthChainID;
	}
	
	public MResidue GetResidueBySeqNumber(long inSeqNumber, String inInsertionCode) {
		auto r = find_if(mResidues.begin(), mResidues.end(),
				boost::bind(MResidue::GetSeqNumber, _1) == inSeqNumber &&
				boost::bind(MResidue::GetInsertionCode, _1) == inInsertionCode);
		if (r == mResidues.end()) {
			throw new mas_exception("Residue "+inSeqNumber+inInsertionCode+" not found");
		}
		return r;
	}
	
	public void GetSequence(String outSequence) {
		for(MResidue r: GetResidues()) {
			outSequence += kResidueInfo[r.GetType().ordinal()].code;
		}
	}
	
	public void Translate(MPoint inTranslation) {
		for(mResidues.begin(), mResidues.end(), boost::bind(&MResidue::Translate, _1, inTranslation));
	}
	
	public void Rotate(Quaternion inRotation) {
		for(mResidues.begin(), mResidues.end(), boost::bind(MResidue::Rotate, _1, inRotation));
	}
	
	public void WritePDB(ostream os) {
		for (MChain chain: mChains) {
		    chain.WritePDB(os);
		}
	}
	
	public Vector<MResidue> GetResidues() { return mResidues; }
	
	public boolean Empty() { return mResidues.empty(); }

	@Override
	public void deconstruct() {
		// TODO Auto-generated method stub
	}
}
