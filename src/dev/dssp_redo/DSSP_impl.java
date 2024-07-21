package dev.dssp_redo;

import java.util.HashMap;

import assist.Deconstructable;
import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.Tuple5;
import assist.util.Pair;
import dev.cif.DataBlock;

/**
 * 
 * @translator Benjamin Strauss
 *
 * using namespace cif::literals;
 * using key_type = std::tuple<std::string,int>;
 * using index_type = std::map<key_type, size_t>;
 */

public class DSSP_impl implements Deconstructable {
	
	final DataBlock mDB;
	Vector<Residue> mResidues = new Vector<Residue>();
	Vector<Pair<Residue, Residue>> mSSBonds = new Vector<Pair<Residue, Residue>>();
	int m_min_poly_proline_stretch_length;
	Statistics mStats = new Statistics();
	
	public DSSP_impl(final DataBlock db, int model_nr, int min_poly_proline_stretch_length) {
		mDB = db;
		m_min_poly_proline_stretch_length = min_poly_proline_stretch_length;
	
		if (cif.VERBOSE) {
			System.err.println("loading residues");
		}
	
		int resNumber = 0;
	
		auto pdbx_poly_seq_scheme = mDB["pdbx_poly_seq_scheme"];
		auto atom_site = mDB["atom_site"];
		
		HashMap<Pair<String, Integer>, Integer> index = new HashMap<Pair<String, Integer>, Integer>();
	
		mResidues.reserve(pdbx_poly_seq_scheme.size());
	
		for (final Tuple5<String, Integer, String, Integer, String> multi: pdbx_poly_seq_scheme.rows<String, Integer, String, Integer, String>("asym_id", "seq_id", "pdb_strand_id", "pdb_seq_num", "pdb_ins_code")) {
			//multi = [asym_id, seq_id, pdb_strand_id, pdb_seq_num, pdb_ins_code];
			
			index.put(new Pair<String, Integer>(multi.a, multi.b), mResidues.size());
			mResidues.emplace_back(model_nr, multi.c, multi.d, multi.e);
		}
	
		for (auto atom : atom_site) {
			String asym_id;
			int seq_id;
	
			cif.tie(asym_id, seq_id) = atom.get("label_asym_id", "label_seq_id");
			
			auto i = index.find(new Pair<String, Integer>(asym_id, seq_id));
			if (i == index.end()) {
				continue;
			}
			
			mResidues[i.second].addAtom(atom);
		}
	
		for (Residue residue : mResidues) {
			residue.finish();
		}
		
		mResidues.erase(std.remove_if(mResidues.begin(), mResidues.end(), [](final Residue r) { return ! r.mComplete; }), mResidues.end());
		mStats.count.chains = 1;
	
		chain_break_type brk = chain_break_type::NewChain;
		for (size_t i = 0; i < mResidues.size(); ++i) {
			auto residue = mResidues[i];
			++resNumber;
	
			if (i > 0) {
				if (distance(mResidues[i - 1].mC, mResidues[i].mN) > kMaxPeptideBondLength) {
					++mStats.count.chains;
					if (mResidues[i - 1].mAsymID == mResidues[i].mAsymID) {
						brk = ChainBreakType.Gap;
					} else {
						brk = ChainBreakType.NewChain;
					}
	
					++resNumber;
				}
			}
	
			residue.mChainBreak = brk;
			residue.mNumber = resNumber;
	
			brk = chain_break_type::None;
		}
	
		mStats.count.residues = static_cast<uint32_t>(mResidues.size());
	
		for (size_t i = 0; i + 1 < mResidues.size(); ++i) {
			auto cur = mResidues[i];
	
			auto next = mResidues[i + 1];
			next.mPrev = cur;
			cur.mNext = next;
		}
	
		for (size_t i = 0; i < mResidues.size(); ++i) {
			auto cur = mResidues[i];
	
			if (i >= 2 && i + 2 < mResidues.size()) {
				auto prevPrev = mResidues[i - 2];
				auto nextNext = mResidues[i + 2];
	
				if (NoChainBreak(prevPrev, nextNext) && prevPrev.mSeqID + 4 == nextNext.mSeqID) {
					float ckap = cosinus_angle(cur.mCAlpha, prevPrev.mCAlpha,
							nextNext.mCAlpha, cur.mCAlpha);
					float skap = Math.sqrt(1 - ckap * ckap);
	
					float kappa = Math.atan2(skap, ckap) * (180 / kPI);
					if (! Float.isNaN(kappa)) {
						cur.mKappa = kappa;
					}
				}
			}
	
			if (i + 1 < mResidues.size()) {
				auto next = mResidues[i + 1];
				next.assignHydrogen();
				if (NoChainBreak(cur, next)) {
					cur.mPsi = dihedral_angle(cur.mN, cur.mCAlpha, cur.mC, next.mN);
					cur.mOmega = dihedral_angle(cur.mCAlpha, cur.mC, next.mN, next.mCAlpha);
				}
			}
	
			if (i > 0) {
				auto prev = mResidues[i - 1];
				if (NoChainBreak(prev, cur)) {
					cur.mTCO = cosinus_angle(cur.mC, cur.mO, prev.mC, prev.mO);
					cur.mPhi = dihedral_angle(prev.mC, cur.mN, cur.mCAlpha, cur.mC);
				}
			}
	
			if (i >= 1 && i + 2 < mResidues.size()) {
				auto prev = mResidues[i - 1];
				auto next = mResidues[i + 1];
				auto nextNext = mResidues[i + 2];
	
				if (NoChainBreak(prev, nextNext))
					cur.mAlpha = dihedral_angle(prev.mCAlpha, cur.mCAlpha, next.mCAlpha, nextNext.mCAlpha);
			}
		}
	}

	boolean findRes(final String asymID, int seqID) {
		return std.find_if(mResidues.begin(), mResidues.end(), [&](auto r)
			{ return r.mAsymID == asymID && r.mSeqID == seqID; });
	}

	void calculateSurface() {
		DSSP_cpp.CalculateAccessibilities(mResidues, mStats);
	}
	
	/**
	 * using namespace cif::literals;
	 */
	void calculateSecondaryStructure() {
		if (cif.VERBOSE) {
			System.err.println("calculating secondary structure");
		}

		for (auto [asym1, seq1, asym2, seq2] : mDB["struct_conn"].find<String, int, String, int>("conn_type_id"_key == "disulf",
				 "ptnr1_label_asym_id", "ptnr1_label_seq_id", "ptnr2_label_asym_id", "ptnr2_label_seq_id"))
		{
			auto r1 = findRes(asym1, seq1);
			if (r1 == mResidues.end()) {
				if (cif::VERBOSE > 0) {
					System.err.println("Missing (incomplete?) residue for SS bond when trying to find " + asym1 + '/' + seq1);
				}
				continue;
				// throw std::runtime_error("Invalid file, missing residue for SS bond");
			}

			auto r2 = findRes(asym2, seq2);
			if (r2 == mResidues.end()) {
				if (cif.VERBOSE > 0)
					System.err.println("Missing (incomplete?) residue for SS bond when trying to find " + asym2 + '/' + seq2);
				continue;
				// throw std::runtime_error("Invalid file, missing residue for SS bond");
			}

			mSSBonds.emplace_back(r1, r2);
		}

		// Prefetch the c-alpha positions. No, really, that might be the trick

		Sector<DecimalCoordinate> cAlphas = new Sector<DecimalCoordinate>();
		cAlphas.reserve(mResidues.size());
		for (auto r : mResidues) {
			cAlphas.emplace_back(r.mCAlpha);
		}

		std.unique_ptr<cif.progress_bar> progress;
		if (cif.VERBOSE == 0 || cif.VERBOSE == 1) {
			progress.reset(new cif.progress_bar((mResidues.size() * (mResidues.size() - 1)) / 2, "calculate distances"));
		}

		// Calculate the HBond energies
		Vector<Pair<Integer, Integer>> near = new Vector<Pair<Integer, Integer>>();

		for (uint32_t i = 0; i + 1 < mResidues.size(); ++i) {
			auto cai = cAlphas[i];

			for (int j = i + 1; j < mResidues.size(); ++j) {
				auto caj = cAlphas[j];

				if (distance_sq(cai, caj) > (kMinimalCADistance * kMinimalCADistance)) {
					continue;
				}

				near.emplace_back(i, j);
			}

			if (progress) {
				progress.consumed(mResidues.size() - i - 1);
			}
		}

		if (cif::VERBOSE > 0) {
			System.err.println("Considering " + near.size() + " pairs of residues");
		}
		
		progress.reset(nullptr);

		CalculateHBondEnergies(mResidues, near);
		CalculateBetaSheets(mResidues, mStats, near);
		CalculateAlphaHelices(mResidues, mStats);
		CalculatePPHelices(mResidues, mStats, m_min_poly_proline_stretch_length);

		if (cif::VERBOSE > 1) {
			for (auto r : mResidues) {
				char helix[] = new char[5];
				for (HelixType helixType : HelixType.values()) {
					switch (r.GetHelixFlag(helixType)) {
						case HelixPositionType.Start: helix[(int)(HelixType)] = '>'; break;
						case HelixPositionType.Middle: helix[(int)(HelixType)] = helixType == HelixType.pp ? 'P' : '3' + (char)(helixType); break;
						case HelixPositionType.StartAndEnd: helix[(int)(HelixType)] = 'X'; break;
						case HelixPositionType.End: helix[(int)(HelixType)] = '<'; break;
						case HelixPositionType.None: helix[(int)(HelixType)] = ' '; break;
					}
				}

				String id = r.mAsymID + ':' + r.mSeqID + '/' + r.mCompoundID;

				System.err.println(id + new String(12 - id.length(), ' ')
						  + (char) (r.mSecondaryStructure) + ' ' + helix);
			}
		}

		// finish statistics
		mStats.count.SS_bridges = static_cast<uint32_t>(mSSBonds.size());

		mStats.count.intra_chain_SS_bridges = 0;
		long ssBondNr = 0;
		for (final Pair<Residue, Residue> ab: mSSBonds) {
			if (ab.x == ab.y) {
				if (cif::VERBOSE > 0) {
					System.err.println( "In the SS bonds list, the residue " + ab.x.mAsymID + ':' + ab.x.mSeqID + " is bonded to itself");
				}
				continue;
			}

			if (ab.x.mAsymID == ab.y.mAsymID && NoChainBreak(ab.x, ab.y)) {
				++mStats.count.intra_chain_SS_bridges;
			}

			ab.x.mSSBridgeNr = ab.y.mSSBridgeNr = ++ssBondNr;
		}

		mStats.count.H_bonds = 0;
		for (auto r : mResidues) {
			auto donor = r.mHBondDonor;

			for (int i = 0; i < 2; ++i) {
				if (donor[i].res != null && donor[i].energy < kMaxHBondEnergy) {
					++mStats.count.H_bonds;
					auto k = donor[i].res.mNumber - r.mNumber;
					if (k >= -5 && k <= 5)
						mStats.count.H_Bonds_per_distance[k + 5] += 1;
				}
			}
		}
	}

	String GetPDBHEADERLine() {
		String keywords;
		auto cat1 = mDB["struct_keywords"];

		for (auto r : cat1) {
			keywords = FixStringLength(r["pdbx_keywords"].as<String>(), 40);
			break;
		}

		String date;
		for (auto r : mDB["pdbx_database_status"]) {
			date = r["recvd_initial_deposition_date"].as<String>();
			if (date.empty()) {
				continue;
			}
			date = cif2pdbDate(date);
			break;
		}

		if (date.empty()) {
			for (auto r : mDB["database_PDB_rev"]) {
				date = r["date_original"].as<String>();
				if (date.empty())
					continue;
				date = cif2pdbDate(date);
				break;
			}
		}

		date = FixStringLength(date, 9);

		//   0         1         2         3         4         5         6         7         8
		//   HEADER    xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxDDDDDDDDD   IIII
		char header[] = "HEADER    xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxDDDDDDDDD   IIII".toCharArray();

		std.copy(keywords.begin(), keywords.end(), header + 10);
		std.copy(date.begin(), date.end(), header + 50);

		String id = mDB.name();
		if (id.length() < 4) {
			id.insert(id.end(), 4 - id.length(), ' ');
		} else if (id.length() > 4) {
			id.erase(id.begin() + 4, id.end());
		}

		std.copy(id.begin(), id.end(), header + 62);

		return FixStringLength(header);
	}
	
	/**
	 * using namespace std::placeholders;
	 * using namespace cif::literals;
	 * 
	 * @return
	 */
	String GetPDBCOMPNDLine() {
		// COMPND

		int molID = 0;
		Vector<String> cmpnd = new Vector<String>();

		for (auto r : mDB["entity"].find("type"_key == "polymer")) {
			String entityID = r["id"].as<String>();

			++molID;
			cmpnd.push_back("MOL_ID: " + molID);

			String molecule = r["pdbx_description"].as<String>();
			cmpnd.push_back("MOLECULE: " + molecule);

			auto poly = mDB["entity_poly"].find("entity_id"_key == entityID);
			if (! poly.empty()) {
				String chains = poly.front()["pdbx_strand_id"].as<String>();
				cif.replace_all(chains, ",", ", ");
				cmpnd.push_back("CHAIN: " + chains);
			}

			String fragment = r["pdbx_fragment"].as<String>();
			if (! fragment.empty()) {
				cmpnd.push_back("FRAGMENT: " + fragment);
			}

			for (auto sr : mDB["entity_name_com"].find("entity_id"_key == entityID)) {
				String syn = sr["name"].as<String>();
				if (! syn.empty()) {
					cmpnd.push_back("SYNONYM: " + syn);
				}
			}

			String mutation = r["pdbx_mutation"].as<String>();
			if (! mutation.empty()) {
				cmpnd.push_back("MUTATION: " + mutation);
			}

			String ec = r["pdbx_ec"].as<String>();
			if (! ec.empty()) {
				cmpnd.push_back("EC: " + ec);
			}

			if (r["src_method"] == "man" || r["src_method"] == "syn") {
				cmpnd.push_back("ENGINEERED: YES");
			}

			String details = r["details"].as<String>();
			if (! details.empty()) {
				cmpnd.push_back("OTHER_DETAILS: " + details);
			}
		}

		return FixStringLength("COMPND    " + cif.join(cmpnd, "; "), kTruncateAt);
	}
	
	/**
	 * using namespace cif::literals;
	 * @return
	 */
	String GetPDBSOURCELine() {
		// SOURCE
		
		int molID = 0;
		Vector<String> source = new Vector<String>();

		for (auto r : mDB["entity"]) {
			if (r["type"] != "polymer") {
				continue;
			}

			String entityID = r["id"].as<String>();

			++molID;
			source.push_back("MOL_ID: " + molID.toString());

			if (r["src_method"] == "syn") {
				source.push_back("SYNTHETIC: YES");
			}

			auto gen = mDB["entity_src_gen"];
			final Pair<String, String> kGenSourceMapping[] = {
				new Pair<String, String>( "gene_src_common_name", "ORGANISM_COMMON" ),
				new Pair<String, String>( "pdbx_gene_src_gene", "GENE" ),
				new Pair<String, String>( "gene_src_strain", "STRAIN" ),
				new Pair<String, String>( "pdbx_gene_src_cell_line", "CELL_LINE" ),
				new Pair<String, String>(  "pdbx_gene_src_organelle", "ORGANELLE" ),
				new Pair<String, String>(  "pdbx_gene_src_cellular_location", "CELLULAR_LOCATION" ),
				new Pair<String, String>(  "pdbx_gene_src_scientific_name", "ORGANISM_SCIENTIFIC" ),
				new Pair<String, String>(  "pdbx_gene_src_ncbi_taxonomy_id", "ORGANISM_TAXID" ),
				new Pair<String, String>(  "pdbx_host_org_scientific_name", "EXPRESSION_SYSTEM" ),
				new Pair<String, String>(  "pdbx_host_org_ncbi_taxonomy_id", "EXPRESSION_SYSTEM_TAXID" ),
				new Pair<String, String>(  "pdbx_host_org_strain", "EXPRESSION_SYSTEM_STRAIN" ),
				new Pair<String, String>(  "pdbx_host_org_variant", "EXPRESSION_SYSTEM_VARIANT" ),
				new Pair<String, String>(  "pdbx_host_org_cellular_location", "EXPRESSION_SYSTEM_CELLULAR_LOCATION" ),
				new Pair<String, String>(  "pdbx_host_org_vector_type", "EXPRESSION_SYSTEM_VECTOR_TYPE" ),
				new Pair<String, String>(  "pdbx_host_org_vector", "EXPRESSION_SYSTEM_VECTOR" ),
				new Pair<String, String>(  "pdbx_host_org_gene", "EXPRESSION_SYSTEM_GENE" ),
				new Pair<String, String>(  "plasmid_name", "EXPRESSION_SYSTEM_PLASMID" )
			};

			for (auto gr : gen.find("entity_id"_key == entityID)) {
				for (auto m : kGenSourceMapping) {
					String cname, sname;
					tie(cname, sname) = m;

					String s = gr[cname].as<String>();
					if (! s.empty()) {
						source.push_back(sname + ": " + s);
					}
				}
			}

			auto nat = mDB["entity_src_nat"];
			final Pair<String, String> kNatSourceMapping[] = {
					new Pair<String, String>( "common_name", "ORGANISM_COMMON" ),
					new Pair<String, String>( "strain", "STRAIN" ),
					new Pair<String, String>( "pdbx_organism_scientific", "ORGANISM_SCIENTIFIC" ),
					new Pair<String, String>( "pdbx_ncbi_taxonomy_id", "ORGANISM_TAXID" ),
					new Pair<String, String>( "pdbx_cellular_location", "CELLULAR_LOCATION" ),
					new Pair<String, String>( "pdbx_plasmid_name", "PLASMID" ),
					new Pair<String, String>( "pdbx_organ", "ORGAN" ),
					new Pair<String, String>( "details", "OTHER_DETAILS" )
			};

			for (auto nr : nat.find("entity_id"_key == entityID)) {
				for (auto m : kNatSourceMapping) {
					String cname, sname;
					tie(cname, sname) = m;

					String s = nr[cname].as<String>();
					if (! s.empty()) {
						source.push_back(sname + ": " + s);	
					}
				}
			}
		}

		return FixStringLength("SOURCE    " + cif.join(source, "; "), kTruncateAt);
	}
	
	String GetPDBAUTHORLine() {
		// AUTHOR
		Vector<String> author = new Vector<String>();
		for (auto r : mDB["audit_author"]) {
			author.push_back(cif2pdbAuth(r["name"].as<String>()));
		}

		return FixStringLength("AUTHOR    " + cif.join(author, "; "), kTruncateAt);
	}

	@SuppressWarnings("deprecation")
	public void deconstruct() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
