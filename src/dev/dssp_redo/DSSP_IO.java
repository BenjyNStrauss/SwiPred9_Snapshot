package dev.dssp_redo;

import java.util.HashMap;

import assist.translation.cplusplus.Tuple3;
import assist.translation.cplusplus.Vector;
import assist.translation.cplusplus.OStream;
import assist.translation.cplusplus.time_t;
import assist.util.Pair;
import dev.cif.Category;
import dev.cif.DataBlock;

/**
 * SPDX-License-Identifier: BSD-2-Clause
 *
 * Copyright (c) 2020 NKI/AVL, Netherlands Cancer Institute
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * @translator Benjamin Strauss
 *
 * #include "dssp-io.hpp"
	#include "revision.hpp"

	#include <cif++.hpp>
	#include <cif++/dictionary_parser.hpp>

	#include <exception>
	#include <filesystem>
	#include <fstream>
	#include <iostream>
 *
 */

public class DSSP_IO {
	
	static String ResidueToDSSPLine(final ResidueInfo info) {
		/*
		    This is the header line for the residue lines in a DSSP file:

		    #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA
		 */

		// auto& residue = info.residue();
		ResidueInfo residue = info;

		if (residue.pdb_strand_id().length() > 1) {
			throw new RuntimeException("This file contains data that won't fit in the original DSSP format");
		}
			
		char code = residue.compound_letter();

		if (code == 'C') {// a cysteine
			int ssbridgenr = info.ssBridgeNr();
			if (ssbridgenr != 0) {
				code = (char) ('a' + ((ssbridgenr - 1) % 26));
			}
		}

		char ss = ' ';
		switch (info.type()) {
			case Alphahelix:	ss = 'H';		break;
			case Betabridge:	ss = 'B';		break;
			case Strand:		ss = 'E';		break;
			case Helix_3:		ss = 'G';		break;
			case Helix_5:		ss = 'I'; 		break;
			case Helix_PPII:	ss = 'P';		break;
			case Turn:			ss = 'T';		break;
			case Bend:			ss = 'S';		break;
			case Loop:			ss = ' ';		break;
		}

		char[] helix = new char[]{ ' ', ' ', ' ', ' ' };
		for (HelixType helixType : HelixType.values()) {
			switch (info.helix(helixType)) {
				case None:
					helix[helixType.ordinal()] = ' '; 
					break;
				case Start:
					helix[helixType.ordinal()] = '>';
					break;
				case End:
					helix[helixType.ordinal()] = '<';
					break;
				case StartAndEnd:
					helix[helixType.ordinal()] = 'X';
					break;
				case Middle:
					helix[helixType.ordinal()] = (helixType == HelixType.pp ? 'P' : (char)('3' + helixType.ordinal())); break;
			}
		}

		char bend = ' ';
		if (info.bend()) {
			bend = 'S';
		}

		double alpha = residue.alpha().orElse(360);
		char chirality = alpha == 360 ? ' ' : (alpha < 0 ? '-' : '+');

		int[] bp = new int[2];
		char[] bridgelabel = new char[]{ ' ', ' ' };
		for (int i : new int[]{ 0, 1 }) {
			//final auto &[p, ladder, parallel] = info.bridge_partner(i);
			final Tuple3<ResidueInfo, Integer, Boolean> bridgeData = info.bridge_partner(i);
			if (bridgeData.a == null) {
				continue;
			}

			bp[i] = bridgeData.a.nr() % 10000; // won't fit otherwise...
			bridgelabel[i] = (char) ((bridgeData.c ? 'a' : 'A') + bridgeData.b % 26);
		}

		char sheet = ' ';
		if (info.sheet() != 0) {
			sheet = (char) ('A' + (info.sheet() - 1) % 26);
		}

		String[] NHO = new String[2], ONH = new String[2];
		for (int i : new int[]{ 0, 1 }) {
			//final auto &[donor, donorE] = info.donor(i);
			final Pair<ResidueInfo, Double> donor = info.donor(i);
			//final auto &[acceptor, acceptorE] = info.acceptor(i);
			final Pair<ResidueInfo, Double> acceptor = info.acceptor(i);
			
			NHO[i] = ONH[i] = "0, 0.0";

			if (acceptor.x != null) {
				int d = acceptor.x.nr() - info.nr();
				NHO[i] = cif.format("%d,%3.1f", d, acceptor.y).str();
			}

			if (donor.x != null) {
				int d = donor.x.nr() - info.nr();
				ONH[i] = cif.format("%d,%3.1f", d, donor.y).str();
			}
		}

		
		// auto ca = residue.atomByID("CA");
		//final auto [cax, cay, caz] = residue.ca_location();
		final Tuple3<Double, Double, Double> ca = residue.ca_location();

		return cif.format("%5d%5d%1.1s%1.1s %c  %c%c%c%c%c%c%c%c%c%4d%4d%c%4.0f %11s%11s%11s%11s  %6.3f%6.1f%6.1f%6.1f%6.1f %6.1f %6.1f %6.1f",
			info.nr(), residue.pdb_seq_num(), residue.pdb_ins_code(), residue.pdb_strand_id(), code,
			ss, helix[3], helix[0], helix[1], helix[2], bend, chirality, bridgelabel[0], bridgelabel[1],
			bp[0], bp[1], sheet, Math.floor(info.accessibility() + 0.5),
			NHO[0], ONH[0], NHO[1], ONH[1],
			residue.tco().orElse(0),
			residue.kappa().orElse(360),
			residue.alpha().orElse(360),
			residue.phi().orElse(360),
			residue.psi().orElse(360),
			ca.a, ca.b, ca.c)
		    .str();
	}
	
	/**
	 * using namespace std::chrono;
	 */
	static void writeDSSP(final DSSP dssp, OStream os) {
		Statistics stats = dssp.get_statistics();

		time_t today = time_t.getCurrentTimeSeconds();
		std.tm tm = std.gmtime(today);

		String version = klibdsspVersionNumber;
		if (version.length() < 10) {
			version.insert(version.end(), 10 - version.length(), ' ');
		}

		os.write("==== Secondary Structure Definition by the program DSSP, NKI version " + version + "                    ==== DATE=" << std.put_time(tm, "%F") << "        ." << "\n"
		   + "REFERENCE W. KABSCH AND C.SANDER, BIOPOLYMERS 22 (1983) 2577-2637                                                              ." + "\n"
		   + dssp.get_pdb_header_line(PDB_RecordType.HEADER) + '.' + "\n"
		   + dssp.get_pdb_header_line(PDB_RecordType.COMPND) + '.' + "\n"
		   + dssp.get_pdb_header_line(PDB_RecordType.SOURCE) + '.' + "\n"
		   + dssp.get_pdb_header_line(PDB_RecordType.AUTHOR) + '.');

		os.write(cif.format("%5d%3d%3d%3d%3d TOTAL NUMBER OF RESIDUES, NUMBER OF CHAINS, NUMBER OF SS-BRIDGES(TOTAL,INTRACHAIN,INTERCHAIN)                .",
				  stats.count.residues, stats.count.chains, stats.count.SS_bridges, stats.count.intra_chain_SS_bridges, (stats.count.SS_bridges - stats.count.intra_chain_SS_bridges))
		   + "\n");

		os.write(cif.format("%8.1f   ACCESSIBLE SURFACE OF PROTEIN (ANGSTROM**2)                                                                         .", stats.accessible_surface)+"\n");

		// hydrogenbond summary

		os.write(cif.format("%5d%5.1f   TOTAL NUMBER OF HYDROGEN BONDS OF TYPE O(I)-->H-N(J)  , SAME NUMBER PER 100 RESIDUES                              .",
				stats.count.H_bonds, (stats.count.H_bonds * 100.0 / stats.count.residues))+"\n");

		os.write(cif.format("%5d%5.1f   TOTAL NUMBER OF HYDROGEN BONDS IN     PARALLEL BRIDGES, SAME NUMBER PER 100 RESIDUES                              .",
				stats.count.H_bonds_in_parallel_bridges, (stats.count.H_bonds_in_parallel_bridges * 100.0 / stats.count.residues))+"\n");

		os.write(cif.format("%5d%5.1f   TOTAL NUMBER OF HYDROGEN BONDS IN ANTIPARALLEL BRIDGES, SAME NUMBER PER 100 RESIDUES                              .", 
				stats.count.H_bonds_in_antiparallel_bridges, (stats.count.H_bonds_in_antiparallel_bridges * 100.0 / stats.count.residues))+"\n");

		for (int k = 0; k < 11; ++k) {
			os.write(cif.format("%5d%5.1f   TOTAL NUMBER OF HYDROGEN BONDS OF TYPE O(I)-->H-N(I%c%1d), SAME NUMBER PER 100 RESIDUES                              .",
					stats.count.H_Bonds_per_distance[k], (stats.count.H_Bonds_per_distance[k] * 100.0 / stats.count.residues), (k - 5 < 0 ? '-' : '+'), Math.abs(k - 5)) +"\n");
		}

		// histograms...
		os.write("  1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30     *** HISTOGRAMS OF ***           .\n");

		for (int hi : stats.histogram.residues_per_alpha_helix) {
			os.write(cif.format("%3d", hi));
		}
		os.write("    RESIDUES PER ALPHA HELIX         .\n");

		for (int hi : stats.histogram.parallel_bridges_per_ladder) {
			os.write(cif.format("%3d", hi));
		}
		os.write("    PARALLEL BRIDGES PER LADDER      .\n");

		for (int hi : stats.histogram.antiparallel_bridges_per_ladder) {
			os.write(cif.format("%3d", hi));
		}
		os.write("    ANTIPARALLEL BRIDGES PER LADDER  .\n");

		for (int hi : stats.histogram.ladders_per_sheet) {
			os.write(cif.format("%3d", hi));
		}
		os.write("    LADDERS PER SHEET                .\n");

		// per residue information

		os.write("  #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA\n");

		int last = 0;
		for (ResidueInfo ri : dssp) {
			// insert a break line whenever we detect missing residues
			// can be the transition to a different chain, or missing residues in the current chain

			if (ri.nr() != last + 1) {
				os.write( cif.format("%5d        !%c             0   0    0      0, 0.0     0, 0.0     0, 0.0     0, 0.0   0.000 360.0 360.0 360.0 360.0    0.0    0.0    0.0",
						  (last + 1), (ri.chain_break() == ChainBreakType.NewChain ? '*' : ' ')) + "\n");
			}

			os.write(ResidueToDSSPLine(ri) +"\n");
			last = ri.nr();
		}
	}

	// --------------------------------------------------------------------

	void writeBridgePairs(DataBlock db, final DSSP dssp) {
		auto hb = db["dssp_struct_bridge_pairs"];

		hb.add_column("id");
		hb.add_column("label_comp_id");
		hb.add_column("label_seq_id");
		hb.add_column("label_asym_id");
		hb.add_column("auth_seq_id");
		hb.add_column("auth_asym_id");
		hb.add_column("pdbx_PDB_ins_code");
		
		// force right order
		for (String da : new String[]{ "acceptor_", "donor_" }) {
			for (String i : new String[]{ "1_", "2_" }) {
				for (String n : new String[]{ "label_comp_id", "label_seq_id", "label_asym_id", "auth_seq_id", "auth_asym_id", "pdbx_PDB_ins_code", "energy" }) {
					hb.add_column(da + i + n);
				}
			}
		}

		for (ResidueInfo res : dssp) {
			cif.row_initializer data = new cif.@row_initializer(
				{
					{ "id", hb.get_unique_id("") },
					{ "label_comp_id", res.compound_id() },
					{ "label_seq_id", res.seq_id() },
					{ "label_asym_id", res.asym_id() },
					// { "auth_comp_id", res.compound_id() },
					{ "auth_seq_id", res.auth_seq_id() },
					{ "auth_asym_id", res.auth_asym_id() },
					{ "pdbx_PDB_ins_code", res.pdb_ins_code() }
				}
			);
			
			final int[] iter1 = new int[]{ 0, 1 };
			
			for (int i : iter1) {
				//final auto [acceptor, acceptorEnergy] = res.acceptor(i);
				
				final Pair<ResidueInfo, Double> acceptor = res.acceptor(i);
				final Pair<ResidueInfo, Double> donor = res.donor(i);

				if (acceptor.x != null) {
					if (i == 0) {
						data.emplace_back("acceptor_1_label_comp_id", acceptor.x.compound_id());
						data.emplace_back("acceptor_1_label_seq_id", acceptor.x.seq_id());
						data.emplace_back("acceptor_1_label_asym_id", acceptor.x.asym_id());
						// data.emplace_back("acceptor_1_auth_comp_id", acceptor.compound_id());
						data.emplace_back("acceptor_1_auth_seq_id", acceptor.x.auth_seq_id());
						data.emplace_back("acceptor_1_auth_asym_id", acceptor.x.auth_asym_id());
						data.emplace_back("acceptor_1_pdbx_PDB_ins_code", acceptor.x.pdb_ins_code());
						data.emplace_back("acceptor_1_energy", acceptor.y, 1);
					} else {
						data.emplace_back("acceptor_2_label_comp_id", acceptor.x.compound_id());
						data.emplace_back("acceptor_2_label_seq_id", acceptor.x.seq_id());
						data.emplace_back("acceptor_2_label_asym_id", acceptor.x.asym_id());
						// data.emplace_back("acceptor_2_auth_comp_id", acceptor.compound_id());
						data.emplace_back("acceptor_2_auth_seq_id", acceptor.x.auth_seq_id());
						data.emplace_back("acceptor_2_auth_asym_id", acceptor.x.auth_asym_id());
						data.emplace_back("acceptor_2_pdbx_PDB_ins_code", acceptor.x.pdb_ins_code());
						data.emplace_back("acceptor_2_energy", acceptor.y, 1);
					}
				}

				if (donor.x != null) {
					if (i == 0) {
						data.emplace_back("donor_1_label_comp_id", donor.x.compound_id());
						data.emplace_back("donor_1_label_seq_id", donor.x.seq_id());
						data.emplace_back("donor_1_label_asym_id", donor.x.asym_id());
						// data.emplace_back("donor_1_auth_comp_id", donor.x.compound_id());
						data.emplace_back("donor_1_auth_seq_id", donor.x.auth_seq_id());
						data.emplace_back("donor_1_auth_asym_id", donor.x.auth_asym_id());
						data.emplace_back("donor_1_pdbx_PDB_ins_code", donor.x.pdb_ins_code());
						data.emplace_back("donor_1_energy", donor.y, 1);
					} else {
						data.emplace_back("donor_2_label_comp_id", donor.x.compound_id());
						data.emplace_back("donor_2_label_seq_id", donor.x.seq_id());
						data.emplace_back("donor_2_label_asym_id", donor.x.asym_id());
						// data.emplace_back("donor_2_auth_comp_id", donor.compound_id());
						data.emplace_back("donor_2_auth_seq_id", donor.x.auth_seq_id());
						data.emplace_back("donor_2_auth_asym_id", donor.x.auth_asym_id());
						data.emplace_back("donor_2_pdbx_PDB_ins_code", donor.x.pdb_ins_code());
						data.emplace_back("donor_2_energy", donor.y, 1);
					}
				}
			}

			hb.emplace(data);
		}
	}

	/**
	 * using namespace cif::literals;
	 * using res_list = std::vector<dssp::residue_info>;
	 */
	void writeSheets(DataBlock db, final DSSP dssp) {
		
		// clean up old info first

		for (String sheet_cat : new String[]{ "struct_sheet", "struct_sheet_order", "struct_sheet_range", "struct_sheet_hbond", "pdbx_struct_sheet_hbond" }) {
			db.erase(remove_if(db.begin(), db.end(), [sheet_cat](final cif.category cat)
						 { return cat.name() == sheet_cat; }),
				db.end());
		}

		// create a list of strands, based on the SS info in DSSP. Store sheet number along with the strand.

		HashMap<Pair<Integer, Integer>, Vector<ResidueInfo>> strands = new HashMap<Pair<Integer, Integer>, Vector<ResidueInfo>>();
		LabeledSet<Integer> sheetNrs = new LabeledSet<Integer>();

		for (ResidueInfo res : dssp) {
			if (res.type() != StructureType.Strand && res.type() != StructureType.Betabridge) {
				continue;
			}
			
			Pair<Integer, Integer> p0 = new Pair<>(res.sheet(), res.strand());

			strands.get(p0).emplace_back(res);
			sheetNrs.insert(res.sheet());
		}

		// --------------------------------------------------------------------

		auto struct_sheet = db["struct_sheet"];
		auto struct_sheet_range = db["struct_sheet_range"];

		for (int sheetNr : sheetNrs) {
			auto sheetID = cif.cif_id_for_number(sheetNr - 1);

			struct_sheet.emplace(
				{
					{ "id", sheetID },
					{ "number_strands",
						std.count_if(strands.begin(), strands.end(), [nr = sheetNr](Pair<Pair<Integer, Integer>, res_list> final s) {
							final auto [strandID, strand] = s;
							return strand.front().sheet() == nr;
						})
					}
				}
			);
			
			//auto [strandTuple, strand]
			for (Pair<Integer, Integer> vals : strands.keySet()) {
				Integer strandTuple = vals.x;
				Integer strand = vals.y;
				
				//auto [strandTuple, strand]
				
				if (strand.front().sheet() != sheetNr) {
					continue;
				}
				
				String strandID = cif.cif_id_for_number(strand.front().strand() - 1);

				std.sort(strand.begin(), strand.end(), [](final ResidueInfo a, final ResidueInfo b)
					{ return a.nr() < b.nr(); });

				auto beg = strand.front();
				auto end = strand.back();

				struct_sheet_range.emplace({
					{ "sheet_id", sheetID },
					{ "id", strandID },
					{ "beg_label_comp_id", beg.compound_id() },
					{ "beg_label_asym_id", beg.asym_id() },
					{ "beg_label_seq_id", beg.seq_id() },
					{ "pdbx_beg_PDB_ins_code", beg.pdb_ins_code() },
					{ "end_label_comp_id", end.compound_id() },
					{ "end_label_asym_id", end.asym_id() },
					{ "end_label_seq_id", end.seq_id() },
					{ "pdbx_end_PDB_ins_code", end.pdb_ins_code() },
					{ "beg_auth_comp_id", beg.compound_id() },
					{ "beg_auth_asym_id", beg.auth_asym_id() },
					{ "beg_auth_seq_id", beg.auth_seq_id() },
					{ "end_auth_comp_id", end.compound_id() },
					{ "end_auth_asym_id", end.auth_asym_id() },
					{ "end_auth_seq_id", end.auth_seq_id() } }
				);
			}
		}
	}

	void writeLadders(DataBlock db, final DSSP dssp) {
		Vector<LadderInfo> ladders = new Vector<LadderInfo>();

		for (ResidueInfo res : dssp) {
			for (int i : new int[]{ 0, 1 }) {
				//final auto [p, ladder, parallel] = res.bridge_partner(i);
				Tuple3<ResidueInfo, Integer, Boolean> ladderTuple = res.bridge_partner(i);
				
				if (ladderTuple.a == null) {
					continue;
				}

				boolean is_new = true;
				for (LadderInfo l : ladders) {
					if (l.ladder != ladderTuple.b) {
						continue;
					}

					__assert__(l.parallel == ladderTuple.c);

					if (find_if(l.pairs.begin(), l.pairs.end(), [na = p.nr(), nb = res.nr()]
							(final boolean ladderTuple.c)
							{ return p.first.nr() == na && p.second.nr() == nb; }) != l.pairs.end()) {
						is_new = false;
						break;
					}

					l.pairs.emplace_back(res, p);
					is_new = false;
					break;
				}

				if (! is_new) {
					continue;
				}

				ladders.emplace_back(ladderTuple.b, res.sheet() - 1, ladderTuple.c, res, ladderTuple.a);
			}
		}

		std.sort(ladders.begin(), ladders.end());

		auto dssp_struct_ladder = db["dssp_struct_ladder"];

		for (final LadderInfo l : ladders) {
			//final auto [beg1, beg2] = l.pairs.front();
			final Pair<ResidueInfo, ResidueInfo> beg = l.pairs.front();
			//final auto [end1, end2] = l.pairs.back();
			final Pair<ResidueInfo, ResidueInfo> end = l.pairs.back();

			dssp_struct_ladder.emplace({
				{ "id", cif.cif_id_for_number(l.ladder) },
				{ "sheet_id", cif.cif_id_for_number(l.sheet) },
				{ "range_id_1", cif.cif_id_for_number(beg1.strand() - 1) },
				{ "range_id_2", cif.cif_id_for_number(beg2.strand() - 1) },
				{ "type", l.parallel ? "parallel" : "anti-parallel" },

				{ "beg_1_label_comp_id", beg1.compound_id() },
				{ "beg_1_label_asym_id", beg1.asym_id() },
				{ "beg_1_label_seq_id", beg1.seq_id() },
				{ "pdbx_beg_1_PDB_ins_code", beg1.pdb_ins_code() },
				{ "end_1_label_comp_id", end1.compound_id() },
				{ "end_1_label_asym_id", end1.asym_id() },
				{ "end_1_label_seq_id", end1.seq_id() },
				{ "pdbx_end_1_PDB_ins_code", end1.pdb_ins_code() },
				{ "beg_1_auth_comp_id", beg1.compound_id() },
				{ "beg_1_auth_asym_id", beg1.auth_asym_id() },
				{ "beg_1_auth_seq_id", beg1.auth_seq_id() },
				{ "end_1_auth_comp_id", end1.compound_id() },
				{ "end_1_auth_asym_id", end1.auth_asym_id() },
				{ "end_1_auth_seq_id", end1.auth_seq_id() },

				{ "beg_2_label_comp_id", beg2.compound_id() },
				{ "beg_2_label_asym_id", beg2.asym_id() },
				{ "beg_2_label_seq_id", beg2.seq_id() },
				{ "pdbx_beg_2_PDB_ins_code", beg2.pdb_ins_code() },
				{ "end_2_label_comp_id", end2.compound_id() },
				{ "end_2_label_asym_id", end2.asym_id() },
				{ "end_2_label_seq_id", end2.seq_id() },
				{ "pdbx_end_2_PDB_ins_code", end2.pdb_ins_code() },
				{ "beg_2_auth_comp_id", beg2.compound_id() },
				{ "beg_2_auth_asym_id", beg2.auth_asym_id() },
				{ "beg_2_auth_seq_id", beg2.auth_seq_id() },
				{ "end_2_auth_comp_id", end2.compound_id() },
				{ "end_2_auth_asym_id", end2.auth_asym_id() },
				{ "end_2_auth_seq_id", end2.auth_seq_id() } }
			);
		}
	}
	
	/**
	 * using namespace std::literals;
	 * using histogram_data_type = Pair<String, Integer>;
	 */
	void writeStatistics(DataBlock db, final DSSP dssp) {
		Statistics stats = dssp.get_statistics();

		Category dssp_statistics = db.operator_brackets("dssp_statistics");

		auto stats_i = dssp_statistics.emplace(
			{ { "entry_id", db.name() },
			{ "nr_of_residues", stats.count.residues },
			{ "nr_of_chains", stats.count.chains },
			{ "nr_of_ss_bridges_total", stats.count.SS_bridges },
			{ "nr_of_ss_bridges_intra_chain", stats.count.intra_chain_SS_bridges },
			{ "nr_of_ss_bridges_inter_chain", stats.count.SS_bridges - stats.count.intra_chain_SS_bridges } }
		);
		
		if (stats.accessible_surface > 0) {
			(stats_i)["accessible_surface_of_protein"] = stats.accessible_surface;
		}

		Category dssp_struct_hbonds = db.operator_brackets("dssp_statistics_hbond");

		dssp_struct_hbonds.emplace({ { "entry_id", db.name() },
			{ "type", "O(I)-->H-N(J)" },
			{ "count", stats.count.H_bonds },
			{ "count_per_100", stats.count.H_bonds * 100.0 / stats.count.residues, 1 } }
		);

		dssp_struct_hbonds.emplace({ { "entry_id", db.name() },
			{ "type", "PARALLEL BRIDGES" },
			{ "count", stats.count.H_bonds_in_parallel_bridges },
			{ "count_per_100", stats.count.H_bonds_in_parallel_bridges * 100.0 / stats.count.residues, 1 } });

		dssp_struct_hbonds.emplace({ { "entry_id", db.name() },
			{ "type", "ANTIPARALLEL BRIDGES" },
			{ "count", stats.count.H_bonds_in_antiparallel_bridges },
			{ "count_per_100", stats.count.H_bonds_in_antiparallel_bridges * 100.0 / stats.count.residues, 1 } });

		for (int k = 0; k < 11; ++k)
			dssp_struct_hbonds.emplace({ { "entry_id", db.name() },
				{ "type", "O(I)-->H-N(I"s + (char)(k - 5 < 0 ? '-' : '+') + std.to_string(abs(k - 5)) + ")" },
				{ "count", stats.count.H_Bonds_per_distance[k] },
				{ "count_per_100", stats.count.H_Bonds_per_distance[k] * 100.0 / stats.count.residues, 1 } });

		Category dssp_statistics_histogram = db.operator_brackets("dssp_statistics_histogram");
		
		Vector<Pair<String, int[]>> iterMe = new Vector<Pair<String, int[]>>();
		iterMe.add(new Pair<String, int[]>("residues_per_alpha_helix", stats.histogram.residues_per_alpha_helix));
		iterMe.add(new Pair<String, int[]>("parallel_bridges_per_ladder", stats.histogram.parallel_bridges_per_ladder));
		iterMe.add(new Pair<String, int[]>("antiparallel_bridges_per_ladder", stats.histogram.antiparallel_bridges_per_ladder));
		iterMe.add(new Pair<String, int[]>("ladders_per_sheet", stats.histogram.ladders_per_sheet));
		
		//for (final auto [type, values] : iterMe)
		for (final Pair<String, int[]> type_values : iterMe) {
			auto hi = dssp_statistics_histogram.emplace(
			{
				{ "entry_id", db.name() },
				{ "type", type },
				{ "1",  type_values.y[0] },
				{ "2",  type_values.y[1] },
				{ "3",  type_values.y[2] },
				{ "4",  type_values.y[3] },
				{ "5",  type_values.y[4] },
				{ "6",  type_values.y[5] },
				{ "7",  type_values.y[6] },
				{ "8",  type_values.y[7] },
				{ "9",  type_values.y[8] },
				{ "10", type_values.y[9] },
				{ "11", type_values.y[10] },
				{ "12", type_values.y[11] },
				{ "13", type_values.y[12] },
				{ "14", type_values.y[13] },
				{ "15", type_values.y[14] },
				{ "16", type_values.y[15] },
				{ "17", type_values.y[16] },
				{ "18", type_values.y[17] },
				{ "19", type_values.y[18] },
				{ "20", type_values.y[19] },
				{ "21", type_values.y[20] },
				{ "22", type_values.y[21] },
				{ "23", type_values.y[22] },
				{ "24", type_values.y[23] },
				{ "25", type_values.y[24] },
				{ "26", type_values.y[25] },
				{ "27", type_values.y[26] },
				{ "28", type_values.y[27] },
				{ "29", type_values.y[28] },
				{ "30", type_values.y[29] },
			});
		}
	}

	void writeSummary(DataBlock db, final DSSP dssp) {
		boolean writeAccessibility = dssp.get_statistics().accessible_surface > 0;

		// A approximation of the old format

		Category dssp_struct_summary = db.operator_brackets("dssp_struct_summary");

		// prime the category with the field labels we need, this is to ensure proper order in writing out the data.

		for (String label : new String[]{ "entry_id", "label_comp_id", "label_asym_id", "label_seq_id", "secondary_structure",
				"ss_bridge", "helix_3_10", "helix_alpha", "helix_pi", "helix_pp", "bend", "chirality", "sheet",
				"strand", "ladder_1", "ladder_2", "accessibility", "TCO", "kappa", "alpha", "phi", "psi",
				"x_ca", "y_ca", "z_ca"}) {
			dssp_struct_summary.add_column(label);
		}

		for (ResidueInfo res: dssp) {
			/* This is the header line for the residue lines in a DSSP file:
			    #  RESIDUE AA STRUCTURE BP1 BP2  ACC     N-H-->O    O-->H-N    N-H-->O    O-->H-N    TCO  KAPPA ALPHA  PHI   PSI    X-CA   Y-CA   Z-CA
			*/

			String ss_bridge = ".";
			if (res.ssBridgeNr() != 0) {
				ss_bridge = ""+res.ssBridgeNr();
			}

			String ss = res.type() == StructureType.Loop ? "." : ""+ (res.type());

			String[] helix = new String[]{ ".", ".", ".", "." };
			for (HelixType helixType : HelixType.values()) {
				switch (res.helix(helixType)) {
					// case dssp::helix_position_type::None: helix[static_cast<int>(helixType)] = ' '; break;
					case Start:
						helix[helixType.ordinal()] = ">";
						break;

					case End:
						helix[helixType.ordinal()] = "<";
						break;

					case StartAndEnd:
						helix[helixType.ordinal()] = "X";
						break;

					case Middle:
						if (helixType == HelixType.pp) {
							helix[helixType.ordinal()] = "P";
						} else {
							//helix[static_cast<int>(helixType)] = { static_cast<char>('3' + static_cast<int>(helixType)) };
							helix[helixType.ordinal()] = ""+ (char)('3' + helixType.ordinal());
						} 
						break;

					default:
						break;
				}
			}

			String bend = ".";
			if (res.bend()) {
				bend = "S";
			}

			String chirality = ".";
			if (res.alpha().isPresent()) {
				chirality = res.alpha().getAsDouble() < 0 ? "-" : "+";
			}

			String[] ladders = new String[]{ ".", "." };

			for (int i : new int[]{ 0, 1 }) {
				//final auto [p, ladder, parallel] = res.bridge_partner(i);
				Tuple3<ResidueInfo, Integer, Boolean> tuple = res.bridge_partner(i);
				if (tuple.a == null) {
					continue;
				}

				ladders[i] = cif.cif_id_for_number(tuple.b);
			}

			//auto final [cax, cay, caz] = res.ca_location();
			final Tuple3<Double, Double, Double> ca = res.ca_location();

			cif.row_initializer data = new cif.@row_initializer(
					{
						{ "entry_id", db.name() },
						{ "label_comp_id", res.compound_id() },
						{ "label_asym_id", res.asym_id() },
						{ "label_seq_id", res.seq_id() },
		
						{ "secondary_structure", ss },
		
						{ "ss_bridge", ss_bridge },
		
						{ "helix_3_10", helix[0] },
						{ "helix_alpha", helix[1] },
						{ "helix_pi", helix[2] },
						{ "helix_pp", helix[3] },
		
						{ "bend", bend },
						{ "chirality", chirality },
		
						{ "sheet", res.sheet() ? cif.cif_id_for_number(res.sheet() - 1) : "." },
						{ "strand", res.strand() ? cif.cif_id_for_number(res.strand() - 1) : "." },
						{ "ladder_1", ladders[0] },
						{ "ladder_2", ladders[1] },
		
						{ "x_ca", ca.x, 1 },
						{ "y_ca", ca.y, 1 },
						{ "z_ca", ca.z, 1 },
					}
			);

			if (writeAccessibility) {
				data.emplace_back("accessibility", res.accessibility(), 1);
			}

			if (res.tco().isPresent()) {
				data.emplace_back("TCO", res.tco(), 3);
			} else {
				data.emplace_back("TCO", ".");
			}

			if (res.kappa().isPresent()) {
				data.emplace_back("kappa", res.kappa(), 1);
			} else {
				data.emplace_back("kappa", ".");
			}

			if (res.alpha().isPresent()) {
				data.emplace_back("alpha", res.alpha(), 1);
			} else {
				data.emplace_back("alpha", ".");
			}

			if (res.phi().isPresent()) {
				data.emplace_back("phi", res.phi(), 1);
			} else {
				data.emplace_back("phi", ".");
			}

			if (res.psi().isPresent()) {
				data.emplace_back("psi", res.psi(), 1);
			} else {
				data.emplace_back("psi", ".");
			}
			
			dssp_struct_summary.emplace(data);
		}
	}
	
	/**
	 * 
	 * @param db
	 * @param dssp
	 * @param writeOther
	 * @param writeExperimental
	 * 
	 * using namespace std::literals;
	 */
	void annotateDSSP(DataBlock db, final DSSP dssp, boolean writeOther, boolean writeExperimental) {
		
		cif.validator validator = (cif.validator) (db.get_validator());
		if (validator.get_validator_for_category("dssp_struct_summary") == null) {
			auto dssp_extension = cif.load_resource("dssp-extension.dic");
			if (dssp_extension) {
				cif.extend_dictionary(validator, dssp_extension);
			}
		}

		if (dssp.empty()) {
			if (cif.VERBOSE > 0) {
				System.out.println("No secondary structure information found");
			}
		} else {
			if (writeExperimental) {
				writeBridgePairs(db, dssp);
				writeSheets(db, dssp);
				writeLadders(db, dssp);
				writeStatistics(db, dssp);
				writeSummary(db, dssp);
			}

			// replace all struct_conf and struct_conf_type records
			Category structConfType = db.operator_brackets("struct_conf_type");
			structConfType.clear();

			Category structConf = db.operator_brackets("struct_conf");
			structConf.clear();

			HashMap<String, Integer> foundTypes = new HashMap<String, Integer>();

			DSSP_Iterator st = dssp.begin(), lt = st;
			StructureType lastSS = st.type();

			for (DSSP_Iterator t = dssp.begin();; lt = t, ++t) {
				boolean stop = t == dssp.end();

				boolean flush = (stop || t.type() != lastSS);

				if (flush && (writeOther || lastSS != StructureType.Loop)) {
					DSSP_Iterator rb = st;
					DSSP_Iterator re = lt;

					String id;
					switch (lastSS) {
						case Helix_3:
							id = "HELX_RH_3T_P";
							break;

						case Alphahelix:
							id = "HELX_RH_AL_P";
							break;

						case Helix_5:
							id = "HELX_RH_PI_P";
							break;

						case Helix_PPII:
							id = "HELX_LH_PP_P";
							break;

						case Turn:
							id = "TURN_TY1_P";
							break;

						case Bend:
							id = "BEND";
							break;

						case Betabridge:
						case Strand:
							id = "STRN";
							break;

						case Loop:
							id = "OTHER";
							break;
					}

					if (foundTypes.count(id) == 0) {
						structConfType.emplace({ { "id", id },
							{ "criteria", "DSSP" } });
						foundTypes[id] = 1;
					}

					structConf.emplace({
						{ "conf_type_id", id },
						{ "id", id + std.to_string(foundTypes[id]++) },
						// { "pdbx_PDB_helix_id", vS(12, 14) },
						{ "beg_label_comp_id", rb.compound_id() },
						{ "beg_label_asym_id", rb.asym_id() },
						{ "beg_label_seq_id", rb.seq_id() },
						{ "pdbx_beg_PDB_ins_code", rb.pdb_ins_code() },
						{ "end_label_comp_id", re.compound_id() },
						{ "end_label_asym_id", re.asym_id() },
						{ "end_label_seq_id", re.seq_id() },
						{ "pdbx_end_PDB_ins_code", re.pdb_ins_code() },

						{ "beg_auth_comp_id", rb.compound_id() },
						{ "beg_auth_asym_id", rb.auth_asym_id() },
						{ "beg_auth_seq_id", rb.auth_seq_id() },
						{ "end_auth_comp_id", re.compound_id() },
						{ "end_auth_asym_id", re.auth_asym_id() },
						{ "end_auth_seq_id", re.auth_seq_id() }
					});

					st = t;
				}

				if (stop) {
					break;
				}

				if (lastSS != t.type()) {
					st = t;
					lastSS = t.type();
				}
			}
		}

		auto software = db["software"];
		software.emplace({
			{ "pdbx_ordinal", software.get_unique_id("") },
			{ "name", "dssp" },
			{ "version", klibdsspVersionNumber },
			{ "date", klibdsspRevisionDate },
			{ "classification", "model annotation" }
		});
	}
}
