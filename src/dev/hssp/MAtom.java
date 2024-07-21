package dev.hssp;

import assist.numerical.Quaternion;
import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.OStream;

/**
 * for now, MAtom contains exactly what the ATOM line contains in a PDB file
 * 
 * from structure.h
 * @translator Benjamin Strauss
 *
 */

public class MAtom extends CppTranslator {
	private static final String FORMAT = "ATOM  %5.5d  %3.3s%c%3.3s %1.1s%4.4d%1.1s   %8.3f%8.3f%8.3f%6.2f%6.2f          %2.2s%2.2s\n";
	//private static final String ATOM = "ATOM  ";
	//public static final DecimalFormat mSerial_format = new DecimalFormat("00000");
	
	long mSerial;
	String mName;
	char mAltLoc;
	String mResName;
	String mChainID, mAuthChainID;
	int mResSeq;
	String mICode;
	MAtomType mType;
	MPoint mLoc;
	double mOccupancy;
	double mTempFactor;
	String mElement;
	int mCharge;

	void SetChainID(String inChainID){ mChainID = inChainID;}
	String GetName() { return mName; }
	void Translate(MPoint inTranslation) { mLoc = mLoc.operator_plus_eq(inTranslation); }
	
	void Rotate(Quaternion inRotation) { mLoc.Rotate(inRotation); }
	
	void WritePDB(OStream os) {
		//  1 - 6  Record name "ATOM "
		//  7 - 11  Integer serial Atom serial number.
		//  13 - 16  Atom name Atom name.
		//  17    Character altLoc Alternate location indicator.
		//  18 - 20  Residue name resName Residue name.
		//  22    Character chainID Chain identifier.
		//  23 - 26  Integer resSeq Residue sequence number.
		//  27    AChar iCode Code for insertion of residues.
		//  31 - 38  Real(8.3) x Orthogonal coordinates for X in Angstroms.
		//  39 - 46  Real(8.3) y Orthogonal coordinates for Y in Angstroms.
		//  47 - 54  Real(8.3) z Orthogonal coordinates for Z in Angstroms.
		//  55 - 60  Real(6.2) occupancy Occupancy.
		//  61 - 66  Real(6.2) tempFactor Temperature factor.
		//  77 - 78  LString(2) element Element symbol, right-justified.
		//  79 - 80  LString(2) charge Charge on the atom.
		//boost::format atom("ATOM  %5.5d  %3.3s%c%3.3s %1.1s%4.4d%1.1s   %8.3f%8.3f%8.3f%6.2f%6.2f          %2.2s%2.2s");

		String charge = "";
		if (mCharge != 0) {
			charge += Math.abs(mCharge);
			if (mCharge > 0) {
				charge += '+';
			} else {
		      charge += '-';
			}
		}
		
		String tmp = String.format(FORMAT, mSerial, mName, mAltLoc, mResName, mChainID, mResSeq ,
				mICode, mLoc.mX , mLoc.mY , mLoc.mZ , mOccupancy , mTempFactor , mElement , charge);
		os.write(tmp);
	}

	MPoint operator_MPoint_AND() { return mLoc; }
}