package dev.hssp;

import java.io.File;
import java.util.Set;

import assist.Deconstructable;
import assist.base.Assist;
import assist.numerical.Quaternion;
import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.istream;
import assist.translation.cplusplus.ostream;
import assist.translation.cplusplus.std;
import assist.util.Pair;

/**
 * from structure.h
 * @translator Benjamin Strauss
 *
 */

public class MProtein implements Deconstructable, HSSP_Constants {
	private String mID, mHeader;
	
	private Vector<String> mDbRef;
	private String mCompound, mSource, mAuthor;
	private Vector<MChain> mChains;
	private long mResidueCount, mChainBreaks;
	
	private Vector<Pair<MResidue,MResidue>> mSSBonds;
	private long mIgnoredWaterMolecules;
	
	// statistics
	private long mNrOfHBondsInParallelBridges, mNrOfHBondsInAntiparallelBridges;
	private long[] mParallelBridgesPerLadderHistogram = new long[kHistogramSize];
	private long[] mAntiparallelBridgesPerLadderHistogram = new long[kHistogramSize];
	private long[] mLaddersPerSheetHistogram = new long[kHistogramSize];
	
	/**
	 * 
	 * @param inID
	 * @param inChain
	 * 
	 */
	public MProtein() {
		std.fill(mParallelBridgesPerLadderHistogram, mParallelBridgesPerLadderHistogram + kHistogramSize, 0);
		std.fill(mAntiparallelBridgesPerLadderHistogram, mAntiparallelBridgesPerLadderHistogram + kHistogramSize, 0);
		std.fill(mLaddersPerSheetHistogram, mLaddersPerSheetHistogram + kHistogramSize, 0);
		
		//mID=(inID);
		mChainBreaks=(0);
		mIgnoredWaterMolecules=(0);
		mNrOfHBondsInParallelBridges=(0);
		mNrOfHBondsInAntiparallelBridges=(0);
		mResidueCount=(0);
	}
	
	/**
	 * 
	 * @param inID
	 * @param inChain
	 *
	 */
	public MProtein(String inID, MChain inChain) {
		std.fill(mParallelBridgesPerLadderHistogram, mParallelBridgesPerLadderHistogram + kHistogramSize, 0);
		std.fill(mAntiparallelBridgesPerLadderHistogram, mAntiparallelBridgesPerLadderHistogram + kHistogramSize, 0);
		std.fill(mLaddersPerSheetHistogram, mLaddersPerSheetHistogram + kHistogramSize, 0);
		mChains.push_back(inChain);
		
		mID=(inID);
		mChainBreaks=(0);
		mIgnoredWaterMolecules=(0);
		mNrOfHBondsInParallelBridges=(0);
		mNrOfHBondsInAntiparallelBridges=(0);
		mResidueCount=(0);
	}

	//MProtein(std::istream& is, bool inCAlphaOnly = false);
	
	public void ReadPDB(istream is) { ReadPDB(is, false); }
	
	public void ReadPDB(istream is, boolean inCAlphaOnly) {
		
	}
	
	public void ReadmmCIF(istream is) { ReadmmCIF(is, false); }
	
	public void ReadmmCIF(istream is, boolean inCAlphaOnly) {
		mResidueCount = 0;
		mChainBreaks = 0;
		mIgnoredWaterMolecules = 0;
		mNrOfHBondsInParallelBridges = 0;
		mNrOfHBondsInAntiparallelBridges = 0;

		std.fill(mParallelBridgesPerLadderHistogram,
		            mParallelBridgesPerLadderHistogram + kHistogramSize, 0);
		std.fill(mAntiparallelBridgesPerLadderHistogram,
		            mAntiparallelBridgesPerLadderHistogram + kHistogramSize, 0);
		std.fill(mLaddersPerSheetHistogram,
		            mLaddersPerSheetHistogram + kHistogramSize, 0);

		Vector<Pair<MResidueID,MResidueID>> ssbonds;
		Set<Character> terminatedChains;

		// Read the mmCIF data into a mmCIF file class
		// Using http://mmcif.rcsb.org/dictionaries/pdb-correspondence/pdb2mmcif-2010.html
		// as a reference.

		mmCIF::file data = new mmCIF::file(is);

		// ID
		mID = data.get("_entry.id");

		// HEADER
		String keywords = data.get("_struct_keywords.text").substr(0, 39);
		
		Object tmp = data.get("_database_PDB_rev.date_original");
		mHeader = "HEADER    "+keywords+" "+String(39 - keywords.length(), ' ')+tmp+" "+mID;

		// COMPND
		for(mmCIF::row desc: data["_entity"]) {
			if (desc["type"] == "polymer") {
				String s = desc["pdbx_description"];
				s = s.trim();
				mCompound = (mCompound.isEmpty() ? mCompound : mCompound + "; ") + s;
			}
		}

		if (mCompound.isEmpty()) {
		    mCompound = data.get_joined("_struct.pdbx_descriptor", "; ");
		}

		// SOURCE
		mSource = data.get_joined("_entity_src_nat.pdbx_organism_scientific", "; ");
		if (mSource.isEmpty()) {
			mSource = data.get_joined("_entity_src_gen.pdbx_gene_src_scientific_name", "; ");
		}
		if (mSource.isEmpty()) {
		    mSource = data.get_joined("_pdbx_entity_src_syn.organism_scientific", "; ");
		}

		// AUTHOR
		mAuthor = data.get_joined("_audit_author.name", "; ");

		// ssbonds

		for(mmCIF::row ss: data["_struct_conn"]) {
		    if (ss["conn_type_id"] != "disulf") {
		    	continue;
		    }

		    Pair<MResidueID,MResidueID> ssbond;

		    if (ss["ptnr1_label_seq_id"].compare(".") == 0 || ss["ptnr1_label_seq_id"].compare("?") == 0) {
		    	continue;
		    }

		    ssbond.x.chain = ss["ptnr1_label_asym_id"];
		    ssbond.x.seqNumber = (long) ss["ptnr1_label_seq_id"];
		    ssbond.x.insertionCode = ss["pdbx_ptnr1_PDB_ins_code"];
		    if (ssbond.x.insertionCode == "?") {
		    	ssbond.x.insertionCode.clear();
		    }

		    if (ss["ptnr2_label_seq_id"].compare(".") == 0 || ss["ptnr2_label_seq_id"].compare("?") == 0) {
		    	continue;
		    }

		    ssbond.y.chain = ss["ptnr2_label_asym_id"];
		    ssbond.y.seqNumber = (long) ss["ptnr2_label_seq_id"];
		    ssbond.y.insertionCode = ss["pdbx_ptnr2_PDB_ins_code"];
		    if (ssbond.y.insertionCode == "?") {
		    	ssbond.y.insertionCode.clear();
		    }

		    ssbonds.push_back(ssbond);
		}

		Vector<MAtom> atoms;
		char firstAltLoc = 0;

		// remap label_seq_id to auth_seq_id
		Map<String, Map<Long,Long> > seq_id_map;

		boolean hasModelNum = false;
		int modelNum = 0;

		for ( mmCIF::row atom: data["_atom_site"]) {
			// skip over NMR models other than the first
			if (!hasModelNum) {
				modelNum = atoi(atom["pdbx_PDB_model_num"].c_str());
				hasModelNum = true;
		    }
		    if (atoi(atom["pdbx_PDB_model_num"]) != modelNum) {
		    	continue;
		    }

		    String label_seq_id = atom["label_seq_id"];

		    MAtom a;

		    a.mSerial = (int) atom["id"];
		    a.mName = atom["auth_atom_id"];
		    a.mAltLoc = atom["label_alt_id"] == "." ? ' ' : atom["label_alt_id"][0];
		    a.mResName = atom["auth_comp_id"];
		    a.mChainID = atom["label_asym_id"];
		    a.mAuthChainID = atom["auth_asym_id"];
		    a.mResSeq = boost::lexical_cast<uint32>(atom["auth_seq_id"]);
		    a.mICode = atom["pdbx_PDB_ins_code"] == "?" ? "" : atom["pdbx_PDB_ins_code"];

		    // map seq_id
		    if (label_seq_id == "?" || label_seq_id == ".") {
		    	seq_id_map[a.mChainID][a.mResSeq] = a.mResSeq;
		    } else {
		    	seq_id_map[a.mChainID][(long) (label_seq_id)] = a.mResSeq;
		    }

		    a.mLoc.mX = ParseFloat(atom["Cartn_x"]);
		    a.mLoc.mY = ParseFloat(atom["Cartn_y"]);
		    a.mLoc.mZ = ParseFloat(atom["Cartn_z"]);

		    a.mOccupancy = ParseFloat(atom["occupancy"]);
		    a.mTempFactor = ParseFloat(atom["B_iso_or_equiv"]);
		    a.mElement = atom["type_symbol"];
		    a.mCharge = atom["pdbx_formal_charge"] != "?" ? (int) atom["pdbx_formal_charge"] : 0;

		    try {
		    	a.mType = MapElement(a.mElement);
		    } catch (Exception e){
		    	if (VERBOSE) {
		    		System.err.println(e.what());
		    	}

		    	continue;
		    }

		    if (a.mType == kHydrogen) {
		    	continue;
		    }

		    if (! atoms.empty() &&
		    		(a.mChainID != atoms.back().mChainID || (a.mResSeq != atoms.back().mResSeq ||
		    		(a.mResSeq == atoms.back().mResSeq && a.mICode != atoms.back().mICode)))) {
		    	AddResidue(atoms);
		    	atoms.clear();
		    	firstAltLoc = 0;
		    }

		    if (a.mAltLoc != ' ') {
		    	if (firstAltLoc == 0) {
		    		firstAltLoc = a.mAltLoc;
		    	}
		    	if (a.mAltLoc == firstAltLoc) {
		    		a.mAltLoc = 'A';
		    	}
		    }

		    if (firstAltLoc != 0 && a.mAltLoc != ' ' && a.mAltLoc != firstAltLoc) {
		    	if (VERBOSE) {
		    		System.err.println("skipping alternate atom record " + a.mResName);
		    	}
		    	continue;
		    }

		    atoms.push_back(a);
		}

		if (! atoms.empty()) {
			AddResidue(atoms);
		}

		// map the sulfur bridges
		long ssbondNr = 1;
		
		for (SSBond ssbond: ssbonds) {
			try {
				MResidue first = GetResidue( ssbond.first.chain,
						seq_id_map[ssbond.first.chain][ssbond.first.seqNumber],
						ssbond.first.insertionCode);
		     	MResidue second = GetResidue( ssbond.second.chain,
		     			seq_id_map[ssbond.second.chain][ssbond.second.seqNumber],
		     			ssbond.second.insertionCode);

		     	if (first == second) {
		     		throw mas_exception("first and second residue are the same");
		     	}

		     	first.SetSSBridgeNr(ssbondNr);
		     	second.SetSSBridgeNr(ssbondNr);

		     	mSSBonds.push_back(SSBond.make_pair(first, second));

		     	++ssbondNr;
			} catch (Exception e) {
		    	if (VERBOSE) {
		    		System.err.println("invalid residue referenced in SSBOND record: "+ e.what());
		    	}
		    }
		}

		mChains.erase(remove_if(mChains.begin(), mChains.end(),
		                          boost::bind(MChain::Empty, _1)),
		                          mChains.end());

		if (VERBOSE && mIgnoredWaterMolecules) {
			System.err.println("Ignored " + mIgnoredWaterMolecules + " water molecules");
		}

		if (mChains.empty()) {
			throw mas_exception("empty protein, or no valid complete residues");
		}
	}
	
	public String GetID() { return mID; }
	public String GetHeader() { return mHeader; }
	
	public String GetCompound() {
		String result = "COMPND    " + mCompound;
		Assist.padStringTo(result, 81);
		return result.substring(0, 80);
	}
	
	public String GetSource() {
		String result = "SOURCE    " + mSource;
		Assist.padStringTo(result, 81);
		return result.substring(0, 80);
	}
	
	public String GetAuthor() {
		String result = "AUTHOR    " + mAuthor;
		Assist.padStringTo(result, 81);
		return result.substring(0, 80);
	}
	
	public Vector<String> GetDbRef() { return mDbRef; }
	
	public void CalculateSecondaryStructure() { CalculateSecondaryStructure(true); }
	
	public void CalculateSecondaryStructure(boolean inPreferPiHelices) {
		Vector<MResidue> residues = new Vector<MResidue>();
		residues.reserve(mResidueCount);
		for (MChain chain: mChains) {
			residues.insert(residues.end(), chain->GetResidues().begin(), chain.GetResidues().end());
		}

		if (VERBOSE) {
			System.err.println("using " + residues.size() + " residues");
		}

		boost::thread t(boost::bind(&MProtein::CalculateAccessibilities, this, boost::ref(residues)));

		CalculateHBondEnergies(residues);
		CalculateBetaSheets(residues);
		CalculateAlphaHelices(residues, inPreferPiHelices);

		t.join();
	}
	
	//long outNrOfHBondsPerDistance[11]
	public void GetStatistics(long outNrOfResidues, long outNrOfChains, long outNrOfSSBridges, long outNrOfIntraChainSSBridges,
			long outNrOfHBonds, long outNrOfHBondsPerDistance[]) {
		outNrOfResidues = mResidueCount;
		outNrOfChains = mChains.size() + mChainBreaks;
		outNrOfSSBridges = mSSBonds.size();

		outNrOfIntraChainSSBridges = 0;
		for (Vector<Pair<MResidue,MResidue>>.const_iterator ri = mSSBonds.begin(); ri != mSSBonds.end(); ++ri) {
			if (ri.first.GetChainID() == ri.second.GetChainID() &&
					(MResidue.NoChainBreak(ri.first, ri.second) || MResidue.NoChainBreak(ri.first, ri.second))) {
				++outNrOfIntraChainSSBridges;
			}
		}

		outNrOfHBonds = 0;
		for (MChain chain: mChains) {
			for ( MResidue r: chain.GetResidues()) {
				HBond[] donor = r.Donor();

				for (int i = 0; i < 2; ++i) {
					if (donor[i].residue != null && donor[i].energy < kMaxHBondEnergy) {
						++outNrOfHBonds;
						int k = donor[i].residue.GetNumber() - r.GetNumber();
						if (k >= -5 && k <= 5) {
							outNrOfHBondsPerDistance[k + 5] += 1;
						}
					}
				}
			}
		}
	}
	
	public void GetCAlphaLocations(String inChainID, Vector<MPoint> outPoints) {
		String chainID = inChainID;
		if (chainID.isEmpty()) {
			chainID = mChains.front().GetChainID();
		}

		for(MResidue r: GetChain(chainID).GetResidues()) {
			outPoints.push_back(r.GetCAlpha());
		}
	}
	
	public MPoint GetCAlphaPosition(String inChainID, long inPDBResSeq) {
		String chainID = inChainID;
		if (chainID.isEmpty()) {
			chainID = mChains.front().GetChainID();
		}

		MPoint result;
		for(MResidue r: GetChain(chainID).GetResidues()) {
			if (r.GetSeqNumber() != inPDBResSeq)
				continue;

			result = r.GetCAlpha();
		}

		return result;
	}
	
	public void GetSequence(String inChainID, entry outEntry) {
		String chainID = inChainID;
		if (chainID.isEmpty()) {
			chainID = mChains.front().GetChainID();
		}

		String seq;
		for(MResidue r: GetChain(chainID).GetResidues()) {
			seq += kResidueInfo[r.GetType()].code;
		    outEntry.m_positions.push_back(r.GetSeqNumber());
		    outEntry.m_ss += r.GetSecondaryStructure();
		}

		outEntry.m_seq = encode(seq);
	}
	
	public void GetSequence(String inChainID, Sequence outSequence) {
		String chainID = inChainID;
		if (chainID.isEmpty()) {
		    chainID = mChains.front().GetChainID();
		}

		String seq;
	  	for(MResidue r: GetChain(chainID).GetResidues()) {
		    seq += kResidueInfo[r.GetType()].code;
	  	}

	  	outSequence = encode(seq);
	}
	
	public void Center() {
		Vector<MPoint> p;
		GetPoints(p);

		MPoint t = CenterPoints(p);

		Translate(MPoint(-t.mX, -t.mY, -t.mZ));
	}
	
	public void Translate(MPoint inTranslation) {
		for(MChain chain: mChains) {
		    chain.Translate(inTranslation);
		}
	}
	
	public void Rotate(Quaternion inRotation) {
		for(MChain chain: mChains) {
		    chain.Rotate(inRotation);
		}
	}
	
	public void WritePDB(ostream os) {
		for(MChain chain: mChains) {
			chain.WritePDB(os);
		}
	}
	
	public void GetPoints(Vector<MPoint> outPoints) {
		for ( MChain chain: mChains) {
			for ( MResidue r: chain.GetResidues())  {
				r.GetPoints(outPoints);
			}
		}
	}
	
	public String GetFirstChainID() {
		return mChains.front().GetChainID();
	}
	
	public void SetChain(String inChainID, MChain inChain) {
		MChain chain = new MChain(GetChain(inChainID));
		chain = inChain;
		chain.SetChainID(inChainID);
	}
	
	public MChain GetChain(String inChainID) {
		for (int i = 0; i < mChains.size(); ++i) {
		    if (mChains.get(i).GetChainID().equals(inChainID)) {
		    	return mChains.get(i);
		    }
		}

		throw new mas_exception("Chain not found");
		return mChains.front();
	}
	
	public Vector<MChain> GetChains() { return mChains; }
	
	public void GetSequences(OutputIterator outSequences){
		Vector<MChain>.const_iterator chain;
		for (chain = mChains.begin(); chain != mChains.end(); ++chain) {
			String seq;
			chain.GetSequence(seq);
			*outSequences++ = seq;
		}
	}
	
	// statistics
	public long GetNrOfHBondsInParallelBridges() {
		return mNrOfHBondsInParallelBridges;
	}
	
	public long GetNrOfHBondsInAntiparallelBridges() {
		return mNrOfHBondsInAntiparallelBridges;
	}
	
	//
	public void GetResiduesPerAlphaHelixHistogram(long[] outHistogram) {
		std.fill(outHistogram, outHistogram + 30, 0);

		for (MChain chain: mChains) {
			int helixLength = 0;

			for (MResidue r: chain.GetResidues()) {
				if (r.GetSecondaryStructure() == alphahelix) {
					++helixLength;
				} else if (helixLength > 0) {
					if (helixLength > kHistogramSize) {
						helixLength = kHistogramSize;
					}

					outHistogram[helixLength - 1] += 1;
					helixLength = 0;
				}
			}
		}
	}
	
	public void GetParallelBridgesPerLadderHistogram(long[] outHistogram) {
		std.copy(mParallelBridgesPerLadderHistogram, mParallelBridgesPerLadderHistogram + kHistogramSize, outHistogram);
	}
	
	public void GetAntiparallelBridgesPerLadderHistogram(long[] outHistogram) {
		std.copy(mAntiparallelBridgesPerLadderHistogram, mAntiparallelBridgesPerLadderHistogram + kHistogramSize,outHistogram);
	}
	
	public void GetLaddersPerSheetHistogram(long[] outHistogram) {
		std.copy(mLaddersPerSheetHistogram, mLaddersPerSheetHistogram + kHistogramSize, outHistogram);
	}
	
	private void AddResidue(Vector<MAtom> inAtoms) {
		boolean hasN = false, hasCA = false, hasC = false, hasO = false;
		for( MAtom atom: inAtoms) {
			if (! hasN && atom.GetName() == "N") {
				hasN = true;
			}
		    if (! hasCA && atom.GetName() == "CA") {
		    	hasCA = true;
		    }
		    if (! hasC && atom.GetName() == "C") {
		    	hasC = true;
		    }
		    if (! hasO && atom.GetName() == "O") {
		    	hasO = true;
		    }
		}

		if (hasN && hasCA && hasC && hasO) {
			MChain chain = GetChain(inAtoms.front().mChainID);
		    chain.SetAuthChainID(inAtoms.front().mAuthChainID);

		    Vector<MResidue> residues = new Vector<MResidue>(chain.GetResidues());

		    MResidue prev = null;
		    if (! residues.empty()) {
		    	prev = residues.back();
		    }

		    long resNumber = mResidueCount + mChains.size() + mChainBreaks;
		    MResidue r = new MResidue(resNumber, prev, inAtoms);
		    // check for chain breaks
		    if (prev != null && ! prev.ValidDistance(r)) {
		    	if (VERBOSE) {
		    		System.err.println( boost::format("The distance between residue %1% and %2% is larger than the maximum peptide bond length")
		            % prev->GetNumber() % resNumber );
		    	}

		    	++mChainBreaks;
		    	r.SetNumber(resNumber + 1);
		    }

		    residues.push_back(r);
		    ++mResidueCount;
		} else if (String(inAtoms.front().mResName).equals("HOH")) {
		    ++mIgnoredWaterMolecules;
		} else if (VERBOSE) {
		    System.err.println( "ignoring incomplete residue " + inAtoms.front().mResName
		              + " (" + inAtoms.front().mResSeq + ')' );
		}
	}
	
	private void CalculateHBondEnergies(Vector<MResidue> inResidues) {
		if (mas.VERBOSE != 0) {
			System.err.println("Calculate H-bond energies");
		}

		// Calculate the HBond energies
		for (int i = 0; i + 1 < inResidues.size(); ++i) {
			MResidue ri = inResidues.get(i);

			for (int j = i + 1; j < inResidues.size(); ++j) {
				MResidue rj = inResidues.get(j);

				if (Distance(ri.GetCAlpha(), rj.GetCAlpha()) < kMinimalCADistance) {
					MResidue.CalculateHBondEnergy(ri, rj);
			        if (j != i + 1) {
			        	MResidue.CalculateHBondEnergy(rj, ri);
			        }
				}
			}
		}
	}
	
	private void CalculateAlphaHelices(Vector<MResidue> inResidues, boolean inPreferPiHelices) {
		if (mas.VERBOSE != 0) {
			System.err.println("Calculate alpha helices");
		}

		// Helix and Turn
		for (MChain chain: mChains) {
			for (int stride = 3; stride <= 5; ++stride) {
				Vector<MResidue> res = new Vector<MResidue>(chain.GetResidues());
				if (res.size() < stride) {
					continue;
				}

				for (int i = 0; i + stride < res.size(); ++i) {
					if (MResidue.TestBond(res.get(i + stride), res.get(i)) && MResidue.NoChainBreak(res.get(i), res.get(i + stride))) {
						res.get(i + stride).SetHelixFlag(stride, helixEnd);
						for (int j = i + 1; j < i + stride; ++j) {
							if (res.get(j).GetHelixFlag(stride) == helixNone) {
								res.get(j).SetHelixFlag(stride, helixMiddle);
							}
						}

						if (res.get(i).GetHelixFlag(stride) == helixEnd) {
							res.get(i).SetHelixFlag(stride, helixStartAndEnd);
						} else {
							res.get(i).SetHelixFlag(stride, helixStart);
						}
					}
				}
			}
		}

		for (MResidue r: inResidues) {
			double kappa = r.Kappa();
			r.SetBend(kappa != 360 && kappa > 70);
		}

		for (int i = 1; i + 4 < inResidues.size(); ++i) {
			if (inResidues.get(i).IsHelixStart(4) && inResidues.get(i - 1).IsHelixStart(4)) {
				for (int j = i; j <= i + 3; ++j) {
					inResidues.get(j).SetSecondaryStructure(alphahelix);
				}
			}
		}

		for (int i = 1; i + 3 < inResidues.size(); ++i) {
			if (inResidues.get(i).IsHelixStart(3) && inResidues.get(i - 1).IsHelixStart(3)) {
				boolean empty = true;
				for (int j = i; empty && j <= i + 2; ++j) {
					empty = inResidues.get(j).GetSecondaryStructure() == loop ||
			                inResidues.get(j).GetSecondaryStructure() == helix_3;
				}
				if (empty) {
					for (int j = i; j <= i + 2; ++j) {
			          inResidues.get(j).SetSecondaryStructure(helix_3);
					}
				}
			}
		}

		for (int i = 1; i + 5 < inResidues.size(); ++i) {
			if (inResidues.get(i).IsHelixStart(5) && inResidues.get(i - 1).IsHelixStart(5)) {
				boolean empty = true;
				for (int j = i; empty && j <= i + 4; ++j) {
					empty = inResidues.get(j).GetSecondaryStructure() == loop ||
			                inResidues.get(j).GetSecondaryStructure() == helix_5 ||
			                (inPreferPiHelices && inResidues.get(j).GetSecondaryStructure() == alphahelix);
				}
				if (empty) {
					for (int j = i; j <= i + 4; ++j) {
						inResidues.get(j).SetSecondaryStructure(helix_5);
					}
				}
			}
		}

		for (int i = 1; i + 1 < inResidues.size(); ++i) {
			if (inResidues.get(i).GetSecondaryStructure() == loop) {
				boolean isTurn = false;
				for (int stride = 3; stride <= 5 && ! isTurn; ++stride) {
					for (int k = 1; k < stride && ! isTurn; ++k) {
						isTurn = (i >= k) && inResidues.get(i - k).IsHelixStart(stride);
					}
				}

				if (isTurn) {
					inResidues.get(i).SetSecondaryStructure(turn);
				} else if (inResidues.get(i).IsBend()) {
			        inResidues.get(i).SetSecondaryStructure(bend);
				}
			}
		}
	}
	
	private void CalculateBetaSheets(Vector<MResidue> inResidues) {
		if (mas.VERBOSE != 0) {
			System.err.println("Calculate beta sheets" );
		}

		// Calculate Bridges
		Vector<MBridge> bridges = new Vector<MBridge>();
		if (inResidues.size() > 4) {
			for (int i = 1; i + 4 < inResidues.size(); ++i) {
				MResidue ri = inResidues.get(i);

				for (int j = i + 3; j + 1 < inResidues.size(); ++j) {
					MResidue rj = inResidues.get(j);

					MBridgeType type = ri.TestBridge(rj);
					if (type == btNoBridge) {
						continue;
					}

			        boolean found = false;
			        for (MBridge bridge: bridges) {
			        	if (type != bridge.type || i != bridge.i.back() + 1) {
			        		continue;
			        	}

			        	if (type == btParallel && bridge.j.back() + 1 == j) {
			        		bridge.i.push_back(i);
			        		bridge.j.push_back(j);
			        		found = true;
			        		break;
			        	}

			        	if (type == btAntiParallel && bridge.j.front() - 1 == j) {
			        		bridge.i.push_back(i);
			        		bridge.j.push_front(j);
			        		found = true;
			        		break;
			        	}
			        }

			        if (! found) {
			        	MBridge bridge = {};

			        	bridge.type = type;
			        	bridge.i.push_back(i);
			        	bridge.chainI = ri.GetChainID();
			        	bridge.j.push_back(j);
			        	bridge.chainJ = rj.GetChainID();

			        	bridges.push_back(bridge);
			        }
				}
			}
		}

		// extend ladders
		sort(bridges.begin(), bridges.end());

		for (int i = 0; i < bridges.size(); ++i) {
			for (int j = i + 1; j < bridges.size(); ++j) {
				int ibi = bridges[i].i.front();
				int iei = bridges[i].i.back();
				int jbi = bridges[i].j.front();
				int jei = bridges[i].j.back();
				int ibj = bridges[j].i.front();
				int iej = bridges[j].i.back();
				int jbj = bridges[j].j.front();
				int jej = bridges[j].j.back();

				if (bridges[i].type != bridges[j].type || 
						MResidue.NoChainBreak(inResidues[std.min(ibi, ibj)], inResidues[std.max(iei, iej)]) == false ||
						MResidue.NoChainBreak(inResidues[std.min(jbi, jbj)], inResidues[std.max(jei, jej)]) == false ||
						ibj - iei >= 6 || (iei >= ibj && ibi <= iej)) {
					continue;
				}

				boolean bulge;
				if (bridges[i].type == btParallel) {
					bulge = ((jbj - jei < 6 && ibj - iei < 3) || (jbj - jei < 3));
				} else {
			        bulge = ((jbi - jej < 6 && ibj - iei < 3) || (jbi - jej < 3));
				}

				if (bulge) {
					bridges[i].i.insert(bridges[i].i.end(), bridges[j].i.begin(), bridges[j].i.end());
					if (bridges[i].type == btParallel) {
						bridges[i].j.insert(bridges[i].j.end(), bridges[j].j.begin(), bridges[j].j.end());
					} else {
						bridges[i].j.insert(bridges[i].j.begin(), bridges[j].j.begin(), bridges[j].j.end());
					}
					bridges.erase(bridges.begin() + j);
			        --j;
				}
			}
		}

		// Sheet
		Set<MBridge> ladderset;
		for(MBridge bridge: bridges) {
			ladderset.insert(bridge);

			uint32 n = bridge.i.size();
			if (n > kHistogramSize) {
				n = kHistogramSize;
			}

			if (bridge.type == btParallel) {
				mParallelBridgesPerLadderHistogram[n - 1] += 1;
			} else {
				mAntiparallelBridgesPerLadderHistogram[n - 1] += 1;
			}
		}

		int sheet = 1, ladder = 0;
		while (! ladderset.empty()) {
			Set<MBridge> sheetset;
			sheetset.insert(ladderset.begin());
			ladderset.erase(ladderset.begin());

			boolean done = false;
			while (! done) {
				done = true;
				for (MBridge a: sheetset) {
			        for (MBridge b: ladderset) {
			        	if (Linked(a, b)) {
			        		sheetset.insert(b);
			        		ladderset.erase(b);
			        		done = false;
			        		break;
			        	}
			        }
			        if (! done) {
			        	break;
			        }
				}
			}

	    	for (MBridge bridge: sheetset) {
	    		bridge.ladder = ladder;
	    		bridge.sheet = sheet;
	    		bridge.link = sheetset;

	    		++ladder;
	    	}

	    	int nrOfLaddersPerSheet = sheetset.size();
	    	if (nrOfLaddersPerSheet > kHistogramSize) {
	    		nrOfLaddersPerSheet = kHistogramSize;
	    	}
	    	if (nrOfLaddersPerSheet == 1 && (sheetset.begin()).i.size() > 1) {
	    		mLaddersPerSheetHistogram[0] += 1;
	    	} else if (nrOfLaddersPerSheet > 1) {
	    		mLaddersPerSheetHistogram[nrOfLaddersPerSheet - 1] += 1;
	    	}

	    	++sheet;
		}

		for (MBridge bridge: bridges) {
			// find out if any of the i and j set members already have
			// a bridge assigned, if so, we're assigning bridge 2

			int betai = 0, betaj = 0;

			for (int l: bridge.i) {
				if (inResidues[l].GetBetaPartner(0).residue != null) {
					betai = 1;
					break;
				}
			}

			for (uint32 l: bridge.j) {
				if (inResidues[l].GetBetaPartner(0).residue != null) {
					betaj = 1;
			        break;
				}
			}

			MSecondaryStructure ss = betabridge;
			if (bridge.i.size() > 1) {
				ss = strand;
			}

			if (bridge.type == btParallel) {
				mNrOfHBondsInParallelBridges += bridge.i.back() - bridge.i.front() + 2;

				Deque<Integer>::iterator j = bridge.j.begin();
				for (uint32 i: bridge.i) {
					inResidues[i].SetBetaPartner(betai, inResidues[*j++], bridge.ladder, true);
				}

				j = bridge.i.begin();
				for (uint32 i: bridge.j) {
			        inResidues[i].SetBetaPartner(betaj, inResidues[*j++], bridge.ladder, true);
				}
			} else {
				mNrOfHBondsInAntiparallelBridges += bridge.i.back() - bridge.i.front() + 2;

				std::deque<uint32>::reverse_iterator j = bridge.j.rbegin();
				for (int i: bridge.i) {
			        inResidues[i].SetBetaPartner(betai, inResidues[*j++], bridge.ladder, false);
				}

				j = bridge.i.rbegin();
				for (int i: bridge.j) {
			        inResidues[i].SetBetaPartner(betaj, inResidues[*j++], bridge.ladder,  false);
				}
			}

			for (uint32 i = bridge.i.front(); i <= bridge.i.back(); ++i) {
				if (inResidues[i].GetSecondaryStructure() != strand) {
					inResidues[i].SetSecondaryStructure(ss);
				}
				inResidues[i]->SetSheet(bridge.sheet);
			}

		    for (uint32 i = bridge.j.front(); i <= bridge.j.back(); ++i)  {
		    	if (inResidues[i]->GetSecondaryStructure() != strand) {
		    		inResidues[i]->SetSecondaryStructure(ss);
		    	}
		    	inResidues[i]->SetSheet(bridge.sheet);
		    }
		}
	}
	
	private void CalculateAccessibilities(Vector<MResidue> inResidues) {
		if (mas.VERBOSE != 0) {
			System.err.println("Calculate accessibilities" );
		}

		uint32 nr_of_threads = boost::thread::hardware_concurrency();
		if (nr_of_threads <= 1){
			foreach (MResidue* residue, inResidues)
			residue.CalculateSurface(inResidues);
		} else {
			MResidueQueue queue;

			boost::thread_group t;
			
			for (uint32 ti = 0; ti < nr_of_threads; ++ti) {
				t.create_thread(boost::bind(&MProtein::CalculateAccessibility, this, boost::ref(queue), boost::ref(inResidues)));
			}

			for (MResidue* residue: inResidues) {
				queue.put(residue);
			}

			queue.put(nullptr);

			t.join_all();
		}
	}
	
	// a thread entry point
	private void CalculateAccessibility(MResidueQueue inQueue, Vector<MResidue> inResidues) {
		// make sure the MSurfaceDots is constructed once
		(void) MSurfaceDots::Instance();

		for (;;) {
			MResidue* residue = inQueue.get();
			if (residue == nullptr) {
				break;
			}
			
			residue->CalculateSurface(inResidues);
		}

		inQueue.put(nullptr);
	}

	@Override
	public void deconstruct() {
		
	}
}
