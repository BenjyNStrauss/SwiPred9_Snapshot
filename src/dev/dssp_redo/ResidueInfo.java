package dev.dssp_redo;

import java.util.OptionalDouble;

import assist.translation.cplusplus.Tuple3;
import assist.translation.cplusplus.Vector;
import assist.util.LabeledMap;
import assist.util.Pair;
import biology.molecule.types.AminoType;

/**
 * public friend class iterator;
 * 
 * 
 * @translator Benjamin Strauss
 *
 */

public class ResidueInfo {
	//residue_info() = default;
	//residue_info(const residue_info &rhs) = default;
	//residue_info &operator=(const residue_info &rhs) = default;
	
	static LabeledMap<AminoType, String[]> kChiAtomsMap = new LabeledMap<AminoType, String[]>() {
		private static final long serialVersionUID = 1L;

		{
			put(AminoType.Aspartic__Acid,	new String[]{ "CG", "OD1" });
			put(AminoType.Asparagine,		new String[]{ "CG", "OD1" });
			put(AminoType.Arginine,			new String[]{ "CG", "CD", "NE", "CZ" });
			put(AminoType.Histidine,		new String[]{ "CG", "ND1" });
			put(AminoType.Glycine,			new String[]{ "CG", "CD", "OE1" });
			put(AminoType.Glutamic__Acid,	new String[]{ "CG", "CD", "OE1" });
			put(AminoType.Serine,			new String[]{ "OG" });
			put(AminoType.Threonine,		new String[]{ "OG1" });
			put(AminoType.Lysine,			new String[]{ "CG", "CD", "CE", "NZ" });
			put(AminoType.Tyrosine,			new String[]{ "CG", "CD1" });
			put(AminoType.Phenylalanine,	new String[]{ "CG", "CD1" });
			put(AminoType.Leucine,			new String[]{ "CG", "CD1" });
			put(AminoType.Tryptophan,		new String[]{ "CG", "CD1" });
			put(AminoType.Cysteine,			new String[]{ "SG" });
			put(AminoType.Isoleucine,		new String[]{ "CG1", "CD1" });
			put(AminoType.Methionine,		new String[]{ "CG", "SD", "CE" });
			//TODO This needs a check…
			put(AminoType.Selenomethionine,	new String[]{ "CG", "SE", "CE" });
			put(AminoType.Proline,			new String[]{ "CG", "CD" });
			put(AminoType.Valine,			new String[]{ "CG1" });

		}
	};
	
	Residue m_impl = null;
	private ResidueInfo(Residue res){
		m_impl = res;
	}
	
	//explicit operator bool
	public boolean to_boolean() { return ! empty(); }
	public boolean empty() { return m_impl == null; }

	public String asym_id() { return m_impl.mAsymID; }
	public int seq_id() { return m_impl.mSeqID; }
	public String alt_id() { return m_impl.mAltID; }
	public String compound_id() { return m_impl.mCompoundID; }
	
	/** @return Single letter for residue compound type, or 'X' in case it is not known */
	public char compound_letter() { return AminoType.parse(compound_id()).letter; };

	public String auth_asym_id() { return m_impl.mAuthAsymID; }
	public int auth_seq_id() { return m_impl.mAuthSeqID; }

	public String pdb_strand_id() { return m_impl.mPDBStrandID; }
	public int pdb_seq_num() { return m_impl.mPDBSeqNum; }
	public String pdb_ins_code() { return m_impl.mPDBInsCode; }

	public OptionalDouble alpha() { return m_impl.mAlpha; }
	public OptionalDouble kappa() { return m_impl.mKappa; }
	public OptionalDouble phi() { return m_impl.mPhi; }
	public OptionalDouble psi() { return m_impl.mPsi; }
	public OptionalDouble tco() { return m_impl.mTCO; }
	public OptionalDouble omega() { return m_impl.mOmega; }

	boolean is_pre_pro() {
		return m_impl.mType != AminoType.Proline && m_impl.mNext != null && m_impl.mNext.mType == AminoType.Proline;
	}
	
	boolean is_cis() { return Math.abs(omega().orElse(360)) < 30.0f; }

	double chiral_volume() { return m_impl.mChiralVolume; }
	
	int nr_of_chis() {
		String[] i = kChiAtomsMap.get(m_impl.mType);

		return (i != null) ? i.length : 0;
	}
	
	double chi(int index) {
		double result = 0;

		AminoType type = m_impl.mType;

		String[] i = kChiAtomsMap.get(type);
		if (i != null && index < i.length) {
			Vector<String> atoms = new Vector<String>("N", "CA", "CB");

			atoms.addAll(i);

			// in case we have a positive chiral volume we need to swap atoms
			if (m_impl.mChiralVolume > 0) {
				if (type == AminoType.Leucine) {
					atoms.set(atoms.size()-1, "CD2");
				}
				if (type == AminoType.Valine) {
					atoms.set(atoms.size()-1, "CG2");
				}
			}

			result = DSSP_cpp.dihedral_angle(
				m_impl.get_atom(atoms.get(index + 0)),
				m_impl.get_atom(atoms.get(index + 1)),
				m_impl.get_atom(atoms.get(index + 2)),
				m_impl.get_atom(atoms.get(index + 3)));
		}

		return result;
	}

	Tuple3<Double, Double, Double> ca_location() { 
		return new Tuple3<Double, Double, Double>(m_impl.mCAlpha.x, m_impl.mCAlpha.y, m_impl.mCAlpha.z );
	}

	ChainBreakType chain_break() {
		return m_impl.mChainBreak;
	}

	/// \brief the internal number in DSSP
	int nr() {
		return m_impl.mNumber;
	}

	StructureType type() { return m_impl.mSecondaryStructure; }

	int ssBridgeNr() { return m_impl.mSSBridgeNr; }

	HelixPositionType helix(HelixType helixType) {
		return m_impl.GetHelixFlag(helixType);
	}

	boolean is_alpha_helix_end_before_start() {
		boolean result = false;

		if (m_impl.mNext != null) {
			result = m_impl.GetHelixFlag(HelixType.alpha) == HelixPositionType.End && m_impl.mNext.GetHelixFlag(HelixType.alpha) == HelixPositionType.Start;
		}
		
		return result;
	}

	boolean bend() { return m_impl.IsBend(); }

	double accessibility() { return m_impl.mAccessibility; }

	/// \brief returns resinfo, ladder and parallel
	Tuple3<ResidueInfo, Integer, Boolean> bridge_partner(int i) {
		BridgePartner bp = m_impl.GetBetaPartner(i);

		ResidueInfo ri = new ResidueInfo(bp.m_residue);
		
		//ri was "std.move(ri)"
		return new Tuple3<ResidueInfo, Integer, Boolean>(ri, bp.ladder, bp.parallel);
	}

	public int sheet() { return m_impl.GetSheet(); }
	public int strand() { return m_impl.GetStrand(); }

	/// \brief return resinfo and the energy of the bond
	public Pair<ResidueInfo, Double> acceptor(int i) {
		HBond a = m_impl.mHBondAcceptor[i];
		return new Pair<ResidueInfo, Double>( new ResidueInfo(a.res), a.energy );
	}
	
	public Pair<ResidueInfo, Double> donor(int i) {
		HBond d = m_impl.mHBondDonor[i];
		return new Pair<ResidueInfo, Double>( new ResidueInfo(d.res), d.energy );
	}
	
	/**
	 * brief Simple compare equals
	 * bool operator==(const residue_info &rhs)
	 */
	public boolean equals(Object rhs) {
		if(rhs instanceof ResidueInfo) {
			return m_impl == ((ResidueInfo) rhs).m_impl;
		} else {
			return false;
		}	
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @return true if there is a bond between two residues
	 */
	public boolean test_bond(ResidueInfo a, ResidueInfo b) {
		return a != null && b != null && DSSP_cpp.TestBond(a.m_impl, b.m_impl);
	}
}
