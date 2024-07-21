package dev.dssp_redo;

import java.util.Comparator;

import assist.numerical.DecimalCoordinate;
import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.Deque;
import assist.translation.cplusplus.Deque.CppDequeIterator;
import assist.translation.cplusplus.Deque.CppDequeReverseIterator;
//import assist.translation.cplusplus.OStringStream;
import assist.translation.cplusplus.Vector;
import assist.util.LabeledSet;
import assist.util.Pair;
import biology.molecule.MoleculeLookup;
import biology.molecule.types.AminoType;

/*-
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
 */


/* Calculate DSSP-like secondary structure information

#include "dssp.hpp"

#include "dssp-io.hpp"

#include <deque>
#include <iomanip>
#include <numeric>
#include <thread>

#ifdef near
#undef near
#endif

using residue = dssp::residue;
using statistics = dssp::statistics;
using structure_type = dssp::structure_type;
using helix_type = dssp::helix_type;
using helix_position_type = dssp::helix_position_type;
using chain_break_type = dssp::chain_break_type;

// -------------------------------------------------------------------- */


/*
 * struct point { float mX, mY, mZ; }; -> FloatCoord
 * 
 * 
 */

public class DSSP_cpp extends CppTranslator {
	
	public static final double kPI = 3.141592653589793238462643383279502884;
	
	public static final float kSSBridgeDistance = 3.0f;
	public static final float kMinimalDistance = 0.5f;
	public static final float kMinimalCADistance = 9.0f;
	public static final float kMinHBondEnergy = -9.9f;
	public static final float kMaxHBondEnergy = -0.5f;
	public static final float kCouplingConstant = -27.888f; //	= -332 * 0.42 * 0.2
	public static final float kMaxPeptideBondLength = 2.5f;

	public static final float kRadiusN = 1.65f;
	public static final float kRadiusCA = 1.87f;
	public static final float kRadiusC = 1.76f;
	public static final float kRadiusO = 1.4f;
	public static final float kRadiusSideAtom = 1.8f;
	public static final float kRadiusWater = 1.4f;
	
	// Truncate lines in pseudo PDB format to this length
	public static final int kTruncateAt = 127;
	
	static DecimalCoordinate op_minus(DecimalCoordinate lhs, DecimalCoordinate rhs) {
		return new DecimalCoordinate(lhs.x - rhs.x, lhs.y - rhs.y, lhs.z - rhs.z);
	}
	
	static DecimalCoordinate op_times(DecimalCoordinate lhs, DecimalCoordinate rhs) {
		return new DecimalCoordinate(lhs.x * rhs.x, lhs.y * rhs.y, lhs.z * rhs.z);
	}
	
	static double distance_sq(DecimalCoordinate a, DecimalCoordinate b) {
		return (a.x - b.x) * (a.x - b.x) +
		       (a.y - b.y) * (a.y - b.y) +
		       (a.z - b.z) * (a.z - b.z);
	}
	
	static double distance(DecimalCoordinate a, DecimalCoordinate b) {
		return Math.sqrt(distance_sq(a, b));
	}
	
	static double dot_product(DecimalCoordinate a, DecimalCoordinate b) {
		return a.x * b.x + a.y * b.y + a.z * b.z;
	}
	
	static DecimalCoordinate cross_product(DecimalCoordinate a, DecimalCoordinate b) {
		return new DecimalCoordinate(
			a.y * b.z - b.y * a.z,
			a.z * b.x - b.z * a.x,
			a.x * b.y - b.x * a.y
		);
	}
	
	static double dihedral_angle(DecimalCoordinate p1, DecimalCoordinate p2, DecimalCoordinate p3, DecimalCoordinate p4) {
		DecimalCoordinate v12 = op_minus(p1, p2); // vector from p2 to p1
		DecimalCoordinate v43 = op_minus(p4, p3); // vector from p3 to p4

		DecimalCoordinate z = op_minus(p2, p3); // vector from p3 to p2

		DecimalCoordinate p = cross_product(z, v12);
		DecimalCoordinate x = cross_product(z, v43);
		DecimalCoordinate y = cross_product(z, x);

		double u = dot_product(x, x);
		double v = dot_product(y, y);

		double result = 360;
		if (u > 0 && v > 0) {
			u = dot_product(p, x) / Math.sqrt(u);
			v = dot_product(p, y) / Math.sqrt(v);
			if (u != 0 || v != 0)
				result = Math.atan2(v, u) * 180.0 / (kPI);
		}

		return result;
	}
	
	static double cosinus_angle(DecimalCoordinate p1, DecimalCoordinate p2, DecimalCoordinate p3, DecimalCoordinate p4) {
		DecimalCoordinate v12 = op_minus(p1, p2);
		DecimalCoordinate v34 = op_minus(p3, p4);

		double result = 0;

		double x = dot_product(v12, v12) * dot_product(v34, v34);
		if (x > 0) {
			result = dot_product(v12, v34) / Math.sqrt(x);
		}

		return result;
	}

	static void CalculateAccessibilities(Vector<Residue> inResidues, Statistics stats) {
		stats.accessible_surface = 0;
		for (Residue residue : inResidues) {
			stats.accessible_surface += residue.CalculateSurface(inResidues);
		}
	}
	
	// --------------------------------------------------------------------
	
	//TODO: use the angle to improve bond energy calculation
	double CalculateHBondEnergy(Residue inDonor, Residue inAcceptor) {
		double result = 0;

		if (inDonor.mType != AminoType.Proline) {
			double distanceHO = distance(inDonor.mH, inAcceptor.mO);
			double distanceHC = distance(inDonor.mH, inAcceptor.mC);
			double distanceNC = distance(inDonor.mN, inAcceptor.mC);
			double distanceNO = distance(inDonor.mN, inAcceptor.mO);

			if (distanceHO < kMinimalDistance || distanceHC < kMinimalDistance || distanceNC < kMinimalDistance || distanceNO < kMinimalDistance) {
				result = kMinHBondEnergy;
			} else {
				result = kCouplingConstant / distanceHO - kCouplingConstant / distanceHC + kCouplingConstant / distanceNC - kCouplingConstant / distanceNO;
			}
				
			// DSSP compatibility mode:
			result = Math.round(result * 1000) / 1000;

			if (result < kMinHBondEnergy) {
				result = kMinHBondEnergy;
			}
		}

		// update donor
		if (result < inDonor.mHBondAcceptor[0].energy) {
			inDonor.mHBondAcceptor[1] = inDonor.mHBondAcceptor[0];
			inDonor.mHBondAcceptor[0].res = inAcceptor;
			inDonor.mHBondAcceptor[0].energy = result;
		} else if (result < inDonor.mHBondAcceptor[1].energy) {
			inDonor.mHBondAcceptor[1].res = inAcceptor;
			inDonor.mHBondAcceptor[1].energy = result;
		}

		// and acceptor
		if (result < inAcceptor.mHBondDonor[0].energy) {
			inAcceptor.mHBondDonor[1] = inAcceptor.mHBondDonor[0];
			inAcceptor.mHBondDonor[0].res = inDonor;
			inAcceptor.mHBondDonor[0].energy = result;
		} else if (result < inAcceptor.mHBondDonor[1].energy) {
			inAcceptor.mHBondDonor[1].res = inDonor;
			inAcceptor.mHBondDonor[1].energy = result;
		}

		return result;
	}
	
	void CalculateHBondEnergies(Vector<Residue> inResidues, Vector<Pair<Integer, Integer>> q) {
		std::unique_ptr<cif::progress_bar> progress;
		
		if (cif.VERBOSE == 0 || cif.VERBOSE == 1) {
			progress.reset(new cif.progress_bar(q.size(), "calculate hbond energies"));
		}

		for (final Pair<Integer, Integer> ij : q) {
			Residue ri = inResidues.get(ij.x);
			Residue rj = inResidues.get(ij.y);

			CalculateHBondEnergy(ri, rj);
			if (ij.y != ij.x + 1) {
				CalculateHBondEnergy(rj, ri);
			}
			
			if (progress) {
				progress.consumed(1);
			}
		}
	}

	// --------------------------------------------------------------------
	
	boolean NoChainBreak(final Residue a, final Residue b) {
		boolean result = a.mAsymID == b.mAsymID;
		for (Residue r = a; result && r != b; r = r.mNext) {
			Residue next = r.mNext;
			if (next == null)
				result = false;
			else
				result = next.mNumber == r.mNumber + 1;
		}
		return result;
	}

	/*boolean NoChainBreak(final Residue a, final Residue b) {
		return NoChainBreak(a, b);
	}*/

	// --------------------------------------------------------------------
	
	static boolean TestBond(final Residue a, final Residue b) {
		return (a.mHBondAcceptor[0].res == b && a.mHBondAcceptor[0].energy < kMaxHBondEnergy) ||
		       (a.mHBondAcceptor[1].res == b && a.mHBondAcceptor[1].energy < kMaxHBondEnergy);
	}

	boolean test_bond(final ResidueInfo a, final ResidueInfo b) {
		return a != null && b != null && TestBond(a.m_impl, b.m_impl);
	}

	// --------------------------------------------------------------------
	
	BridgeType TestBridge(final Residue r1, final Residue r2){  
	                                         	// I.	a	d	II.	a	d		parallel
		Residue a = r1.mPrev;                   //		  \			  /
		Residue b = r1;                         //		b	e		b	e
		Residue c = r1.mNext;                   // 		  /			  \                      ..
		Residue d = r2.mPrev;                   //		c	f		c	f
		Residue e = r2;                         //
		Residue f = r2.mNext;                   // III.	a <- f	IV. a	  f		antiparallel
		                                        //
		BridgeType result = BridgeType.None;	//		b	 e      b <-> e
		                                        //
												//		c -> d		c     d

		if (a != null && c != null && NoChainBreak(a, c) && d != null && f != null && NoChainBreak(d, f)) {
			if ((TestBond(c, e) && TestBond(e, a)) || (TestBond(f, b) && TestBond(b, d))) {
				result = BridgeType.Parallel;
			} else if ((TestBond(c, d) && TestBond(f, a)) || (TestBond(e, b) && TestBond(b, e))) {
				result = BridgeType.AntiParallel;
			}
		}

		return result;
	}
	
	// --------------------------------------------------------------------
	/**
	 * Completely re-written, but should do the same thing
	 * @return: true if any of the residues in bridge a is identical to any of the residues in bridge b
	 *  
	 */
	boolean Linked(final Bridge a, final Bridge b) {
		for(Integer val: a.i) {
			if(b.i.contains(val) || b.j.contains(val)) { return true; }
		}
		
		for(Integer val: a.j) {
			if(b.i.contains(val) || b.j.contains(val)) { return true; }
		}
		
		return false;
	}

	// --------------------------------------------------------------------
	
	void CalculateBetaSheets(Vector<Residue> inResidues, Statistics stats, Vector<Pair<Integer, Integer>> q) {
		// if (cif::VERBOSE)
		// 	std::cerr << "calculating beta sheets" << std::endl;

		unique_ptr<cif.progress_bar> progress;
		if (cif.VERBOSE == 0 || cif.VERBOSE == 1) {
			progress.reset(new cif.progress_bar(q.size(), "calculate beta sheets"));
		}

		// Calculate Bridges
		Vector<Bridge> bridges = new Vector<Bridge>();

		for (final Pair<Integer, Integer> ij : q) {
			if (progress) {
				progress.consumed(1);
			}

			Residue ri = inResidues.get(ij.x);
			Residue rj = inResidues.get(ij.y);

			BridgeType type = TestBridge(ri, rj);
			if (type == BridgeType.None) {
				continue;
			}

			boolean found = false;
			for (Bridge bridge : bridges) {
				if (type != bridge.type || ij.x != bridge.i.back() + 1) {
					continue;
				}

				if (type == BridgeType.Parallel && bridge.j.back() + 1 == ij.y) {
					bridge.i.push_back(ij.x);
					bridge.j.push_back(ij.y);
					found = true;
					break;
				}

				if (type == BridgeType.AntiParallel && bridge.j.front() - 1 == ij.y) {
					bridge.i.push_back(ij.x);
					bridge.j.push_front(ij.y);
					found = true;
					break;
				}
			}

			if (! found) {
				Bridge bridge = new Bridge();

				bridge.type = type;
				bridge.i.push_back(ij.x);
				bridge.chainI = ri.mAsymID;
				bridge.j.push_back(ij.y);
				bridge.chainJ = rj.mAsymID;

				bridges.push_back(bridge);
			}
		}

		// extend ladders
		//std.sort(bridges.begin(), bridges.end());
		bridges.sort(new Comparator<Bridge>() {
			@Override
			public int compare(Bridge o1, Bridge o2) {
				return (o1.operator_lt(o2)) ? -1 : (o2.operator_lt(o1) ? 1 : 0);
			}
		});

		for (int i = 0; i < bridges.size(); ++i) {
			for (int j = i + 1; j < bridges.size(); ++j) {
				int ibi = bridges.get(i).i.front();
				int iei = bridges.get(i).i.back();
				int jbi = bridges.get(i).j.front();
				int jei = bridges.get(i).j.back();
				int ibj = bridges.get(j).i.front();
				int iej = bridges.get(j).i.back();
				int jbj = bridges.get(j).j.front();
				int jej = bridges.get(j).j.back();

				if (bridges.get(i).type != bridges.get(j).type ||
					NoChainBreak(inResidues.get(min(ibi, ibj)), inResidues.get(max(iei, iej))) == false ||
					NoChainBreak(inResidues.get(min(jbi, jbj)), inResidues.get(max(jei, jej))) == false ||
					ibj - iei >= 6 ||
					(iei >= ibj && ibi <= iej)) {
					continue;
				}

				boolean bulge;
				if (bridges.get(i).type == BridgeType.Parallel) {
					bulge = ((jbj - jei < 6 && ibj - iei < 3) || (jbj - jei < 3));
				} else {
					bulge = ((jbi - jej < 6 && ibj - iei < 3) || (jbi - jej < 3));
				}

				if (bulge) {
					bridges.get(i).i.insert(bridges.get(i).i.end(), bridges.get(j).i.begin(), bridges.get(j).i.end());
					if (bridges.get(i).type == BridgeType.Parallel) {
						bridges.get(i).j.insert(bridges.get(i).j.end(), bridges.get(j).j.begin(), bridges.get(j).j.end());
					} else {
						bridges.get(i).j.insert(bridges.get(i).j.begin(), bridges.get(j).j.begin(), bridges.get(j).j.end());
					}
					bridges.erase(bridges.begin() + j);
					--j;
				}
			}
		}

		// Sheet
		LabeledSet<Bridge> ladderset = new LabeledSet<Bridge>();
		for (Bridge bridge : bridges) {
			ladderset.insert(bridge);

			int n = bridge.i.size();
			if (n > DSSP.kHistogramSize) {
				n = DSSP.kHistogramSize;
			}

			if (bridge.type == BridgeType.Parallel) {
				stats.histogram.parallel_bridges_per_ladder[n - 1] += 1;
			} else {
				stats.histogram.antiparallel_bridges_per_ladder[n - 1] += 1;
			}
		}

		int sheet = 1, ladder = 0;
		while (! ladderset.isEmpty()) {
			LabeledSet<Bridge> sheetset = new LabeledSet<Bridge>();
			sheetset.insert(ladderset.begin());
			ladderset.clear();

			boolean done = false;
			while (! done) {
				done = true;
				for (Bridge a : sheetset) {
					for (Bridge b : ladderset) {
						if (Linked(a, b)) {
							sheetset.insert(b);
							ladderset.erase(b);
							done = false;
							break;
						}
					}
					if (!done) {
						break;
					}
				}
			}

			for (Bridge bridge : sheetset) {
				bridge.ladder = ladder;
				bridge.sheet = sheet;
				bridge.link = sheetset;

				++ladder;
			}

			int nrOfLaddersPerSheet = sheetset.size();
			if (nrOfLaddersPerSheet > DSSP.kHistogramSize) {
				nrOfLaddersPerSheet = DSSP.kHistogramSize;
			}
			if (nrOfLaddersPerSheet == 1 && (sheetset.begin()).i.size() > 1) {
				stats.histogram.ladders_per_sheet[0] += 1;
			} else if (nrOfLaddersPerSheet > 1) {
				stats.histogram.ladders_per_sheet[nrOfLaddersPerSheet - 1] += 1;
			}

			++sheet;
		}

		for (Bridge bridge : bridges) {
			// find out if any of the i and j set members already have
			// a bridge assigned, if so, we're assigning bridge 2

			int betai = 0, betaj = 0;

			for (int l : bridge.i){
				if (inResidues.get(l).GetBetaPartner(0).m_residue != null) {
					betai = 1;
					break;
				}
			}

			for (int l : bridge.j) {
				if (inResidues.get(l).GetBetaPartner(0).m_residue != null) {
					betaj = 1;
					break;
				}
			}

			StructureType ss = StructureType.Betabridge;
			if (bridge.i.size() > 1) {
				ss = StructureType.Strand;
			}

			if (bridge.type == BridgeType.Parallel) {
				stats.count.H_bonds_in_parallel_bridges += bridge.i.back() - bridge.i.front() + 2;

				CppDequeIterator<Integer> j = bridge.j.begin();
				for (int i : bridge.i) {
					inResidues.get(i).SetBetaPartner(betai, inResidues.get(j.next()), bridge.ladder, true);
				}
				j = bridge.i.begin();
				for (int i : bridge.j) {
					inResidues.get(i).SetBetaPartner(betaj, inResidues.get(j.next()), bridge.ladder, true);
				}
			} else {
				stats.count.H_bonds_in_antiparallel_bridges += bridge.i.back() - bridge.i.front() + 2;

				CppDequeReverseIterator<Integer> j = bridge.j.rbegin();
				for (int i : bridge.i) {
					inResidues.get(i).SetBetaPartner(betai, inResidues.get(j.next()), bridge.ladder, false);
				}
				
				j = bridge.i.rbegin();
				for (int i : bridge.j) {
					inResidues.get(i).SetBetaPartner(betaj, inResidues.get(j.next()), bridge.ladder, false);
				}
			}

			for (int i = bridge.i.front(); i <= bridge.i.back(); ++i) {
				if (inResidues.get(i).GetSecondaryStructure() != StructureType.Strand) {
					inResidues.get(i).SetSecondaryStructure(ss);
				}
				inResidues.get(i).SetSheet(bridge.sheet);
			}

			for (int i = bridge.j.front(); i <= bridge.j.back(); ++i) {
				if (inResidues.get(i).GetSecondaryStructure() != StructureType.Strand) {
					inResidues.get(i).SetSecondaryStructure(ss);
				}
				inResidues.get(i).SetSheet(bridge.sheet);
			}
		}

		// Create 'strands'. A strand is a range of residues without a gap in between
		// that belong to the same sheet.

		int strand = 0;
		for (int iSheet = 1; iSheet < sheet; ++iSheet) {
			int lastNr = -1;
			for (Residue res : inResidues) {
				if (res.mSheet != iSheet) {
					continue;
				}
				
				if (lastNr + 1 < res.mNumber) {
					++strand;
				}
				
				res.mStrand = strand;
				lastNr = res.mNumber;
			}
		}
	}
	
	// --------------------------------------------------------------------
	
	void CalculateAlphaHelices(Vector<Residue> inResidues, Statistics stats) {
		CalculateAlphaHelices(inResidues, stats, true);
	}
	
	void CalculateAlphaHelices(Vector<Residue> inResidues, Statistics stats, boolean inPreferPiHelices) {
		if (cif.VERBOSE) {
			System.err.println("calculating alpha helices");
		}

		// Helix and Turn
		for (HelixType helixType : HelixType.values_main()) {
			int stride = (helixType).ordinal() + 3;

			for (int i = 0; i + stride < inResidues.size(); ++i) {
				if (NoChainBreak(inResidues.get(i), inResidues.get(i + stride)) && TestBond(inResidues.get(i + stride), inResidues.get(i))) {
					inResidues.get(i + stride).SetHelixFlag(helixType, HelixPositionType.End);
					for (int j = i + 1; j < i + stride; ++j) {
						if (inResidues.get(j).GetHelixFlag(helixType) == HelixPositionType.None) {
							inResidues.get(j).SetHelixFlag(helixType, HelixPositionType.Middle);
						}
					}

					if (inResidues.get(i).GetHelixFlag(helixType) == HelixPositionType.End) {
						inResidues.get(i).SetHelixFlag(helixType, HelixPositionType.StartAndEnd);
					} else {
						inResidues.get(i).SetHelixFlag(helixType, HelixPositionType.Start);
					}
				}
			}
		}

		for (Residue r : inResidues) {
			if (r.mKappa.isPresent()) {
				r.SetBend(r.mKappa.getAsDouble() > 70);
			}
		}

		for (int i = 1; i + 4 < inResidues.size(); ++i) {
			if (inResidues.get(i).IsHelixStart(HelixType.alpha) && inResidues.get(i - 1).IsHelixStart(HelixType.alpha)) {
				for (int j = i; j <= i + 3; ++j) {
					inResidues.get(j).SetSecondaryStructure(StructureType.Alphahelix);
				}
			}
		}

		for (int i = 1; i + 3 < inResidues.size(); ++i) {
			if (inResidues.get(i).IsHelixStart(HelixType._3_10) && inResidues.get(i - 1).IsHelixStart(HelixType._3_10)) {
				boolean empty = true;
				for (int j = i; empty && j <= i + 2; ++j) {
					empty = inResidues.get(j).GetSecondaryStructure() == StructureType.Loop || inResidues.get(j).GetSecondaryStructure() == StructureType.Helix_3;
				}
				if (empty) {
					for (int j = i; j <= i + 2; ++j) {
						inResidues.get(j).SetSecondaryStructure(StructureType.Helix_3);
					}
				}
			}
		}

		for (int i = 1; i + 5 < inResidues.size(); ++i) {
			if (inResidues.get(i).IsHelixStart(HelixType.pi) && inResidues.get(i - 1).IsHelixStart(HelixType.pi)) {
				boolean empty = true;
				for (int j = i; empty && j <= i + 4; ++j) {
					empty = inResidues.get(j).GetSecondaryStructure() == StructureType.Loop || inResidues.get(j).GetSecondaryStructure() == StructureType.Helix_5 ||
					        (inPreferPiHelices && inResidues.get(j).GetSecondaryStructure() == StructureType.Alphahelix);
				}
				if (empty) {
					for (int j = i; j <= i + 4; ++j) {
						inResidues.get(j).SetSecondaryStructure(StructureType.Helix_5);
					}
				}
			}
		}

		for (int i = 1; i + 1 < inResidues.size(); ++i) {
			if (inResidues.get(i).GetSecondaryStructure() == StructureType.Loop) {
				boolean isTurn = false;
				for (HelixType helixType : HelixType.values_main()) {
					int stride = 3 + helixType.ordinal();
					for (int k = 1; k < stride && ! isTurn; ++k)
						isTurn = (i >= k) && inResidues.get(i - k).IsHelixStart(helixType);
				}

				if (isTurn) {
					inResidues.get(i).SetSecondaryStructure(StructureType.Turn);
				} else if (inResidues.get(i).IsBend()) {
					inResidues.get(i).SetSecondaryStructure(StructureType.Bend);
				}
			}
		}

		String asym;
		int helixLength = 0;
		for (Residue r : inResidues) {
			if (r.mAsymID != asym) {
				helixLength = 0;
				asym = r.mAsymID;
			}

			if (r.GetSecondaryStructure() == StructureType.Alphahelix) {
				++helixLength;
			} else if (helixLength > 0) {
				if (helixLength > DSSP.kHistogramSize)
					helixLength = DSSP.kHistogramSize;

				stats.histogram.residues_per_alpha_helix[helixLength - 1] += 1;
				helixLength = 0;
			}
		}
	}

	// --------------------------------------------------------------------
	
	void CalculatePPHelices(Vector<Residue> inResidues, Statistics stats, int stretch_length) {
		if (cif.VERBOSE) {
			System.err.println("calculating pp helices");
		}

		int N = inResidues.size();

		final float epsilon = 29;
		final float phi_min = -75 - epsilon;
		final float phi_max = -75 + epsilon;
		final float psi_min = 145 - epsilon;
		final float psi_max = 145 + epsilon;

		Vector<Double> phi = new Vector<Double>(N);
		Vector<Double> psi = new Vector<Double>(N);

		for (int i = 1; i + 1 < inResidues.size(); ++i) {
			phi.set(i, inResidues.get(i).mPhi.orElse(360));
			psi.set(i, inResidues.get(i).mPsi.orElse(360));
		}

		for (int i = 1; i + 3 < inResidues.size(); ++i) {
			switch (stretch_length) {
				case 2: {
					if (phi_min > phi.get(i + 0) || phi.get(i + 0) > phi_max ||
						phi_min > phi.get(i + 1) || phi.get(i + 1) > phi_max) {
						continue;
					}

					if (psi_min > psi.get(i + 0) || psi.get(i + 0) > psi_max ||
						psi_min > psi.get(i + 1) || psi.get(i + 1) > psi_max) {
						continue;
					}

					// auto phi_avg = (phi[i + 0] + phi[i + 1]) / 2;
					// auto phi_sq = (phi[i + 0] - phi_avg) * (phi[i + 0] - phi_avg) +
					// 			  (phi[i + 1] - phi_avg) * (phi[i + 1] - phi_avg);

					// if (phi_sq >= 200)
					// 	continue;

					// auto psi_avg = (psi[i + 0] + psi[i + 1]) / 2;
					// auto psi_sq = (psi[i + 0] - psi_avg) * (psi[i + 0] - psi_avg) +
					// 			  (psi[i + 1] - psi_avg) * (psi[i + 1] - psi_avg);

					// if (psi_sq >= 200)
					// 	continue;

					switch (inResidues.get(i).GetHelixFlag(HelixType.pp)) {
						case None:
							inResidues.get(i).SetHelixFlag(HelixType.pp, HelixPositionType.Start);
							break;

						case End:
							inResidues.get(i).SetHelixFlag(HelixType.pp, HelixPositionType.Middle);
							break;

						default:
							break;
					}

					inResidues.get(i).SetHelixFlag(HelixType.pp, HelixPositionType.End);

					if (inResidues.get(i).GetSecondaryStructure() == StructureType.Loop) {
						inResidues.get(i).SetSecondaryStructure(StructureType.Helix_PPII);
					}
					if (inResidues.get(i + 1).GetSecondaryStructure() == StructureType.Loop) {
						inResidues.get(i + 1).SetSecondaryStructure(StructureType.Helix_PPII);
					}
				}
				break;

				case 3: {
					if (phi_min > phi.get(i + 0) || phi.get(i + 0) > phi_max ||
						phi_min > phi.get(i + 1) || phi.get(i + 1) > phi_max ||
						phi_min > phi.get(i + 2) || phi.get(i + 2) > phi_max) {
						continue;
					}

					if (psi_min > psi.get(i + 0) || psi.get(i + 0) > psi_max ||
						psi_min > psi.get(i + 1) || psi.get(i + 1) > psi_max ||
						psi_min > psi.get(i + 2) || psi.get(i + 2) > psi_max) {
						continue;
					}

					// auto phi_avg = (phi[i + 0] + phi[i + 1] + phi[i + 2]) / 3;
					// auto phi_sq = (phi[i + 0] - phi_avg) * (phi[i + 0] - phi_avg) +
					// 			  (phi[i + 1] - phi_avg) * (phi[i + 1] - phi_avg) +
					// 			  (phi[i + 2] - phi_avg) * (phi[i + 2] - phi_avg);

					// if (phi_sq >= 300)
					// 	continue;

					// auto psi_avg = (psi[i + 0] + psi[i + 1] + psi[i + 2]) / 3;
					// auto psi_sq = (psi[i + 0] - psi_avg) * (psi[i + 0] - psi_avg) +
					// 			  (psi[i + 1] - psi_avg) * (psi[i + 1] - psi_avg) +
					// 			  (psi[i + 2] - psi_avg) * (psi[i + 2] - psi_avg);

					// if (psi_sq >= 300)
					// 	continue;

					switch (inResidues.get(i).GetHelixFlag(HelixType.pp)) {
						case None:
							inResidues.get(i).SetHelixFlag(HelixType.pp, HelixPositionType.Start);
							break;

						case End:
							inResidues.get(i).SetHelixFlag(HelixType.pp, HelixPositionType.StartAndEnd);
							break;

						default:
							break;
					}

					inResidues.get(i + 1).SetHelixFlag(HelixType.pp, HelixPositionType.Middle);
					inResidues.get(i + 2).SetHelixFlag(HelixType.pp, HelixPositionType.End);

					if (inResidues.get(i + 0).GetSecondaryStructure() == StructureType.Loop) {
						inResidues.get(i + 0).SetSecondaryStructure(StructureType.Helix_PPII);
					}

					if (inResidues.get(i + 1).GetSecondaryStructure() == StructureType.Loop) {
						inResidues.get(i + 1).SetSecondaryStructure(StructureType.Helix_PPII);
					}

					if (inResidues.get(i + 2).GetSecondaryStructure() == StructureType.Loop) {
						inResidues.get(i + 2).SetSecondaryStructure(StructureType.Helix_PPII);
					}

					break;
				}

				default:
					throw new RuntimeException("Unsupported stretch length");
			}
		}
	}
	
	// --------------------------------------------------------------------
	
	String FixStringLength(String s) {
		return FixStringLength(s, kTruncateAt);
	}
	
	String FixStringLength(String s, int l) {
		if (s.length() > l) {
			s = s.substring(0, l - 4) + "... ";
		} else if (s.length() < l) {
			
			
			s.append(l - s.length(), ' ');
		}

		return s;
	}
	
	String cif2pdbDate(String d) {
		
		//final std::regex rx(R"((\d{4})-(\d{2})(?:-(\d{2}))?)");
		final String regex = new String("((\\d{4})-(\\d{2})(?:-(\\d{2}))?)");
		final String[] kMonths = {
			"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
		};

		smatch m;
		StringBuilder os = new StringBuilder();

		if (std::regex_match(d, m, rx)) {
			int year = stoi(m[1].str());
			int month = stoi(m[2].str());

			if (m[3].matched) {
				os.append(String.format("%02d", stoi(m[3].str())) + '-');
			}
			os.append(kMonths[month - 1] + '-' + String.format("%02d", (year % 100)));
		}

		return os.toString();
	}

	String cif2pdbAuth(String name) {
		//final std::regex rx(R"(([^,]+), (\S+))");
		final String regex = new String("(([^,]+), (\\S+))");

		smatch m;
		if (std::regex_match(name, m, rx)) {
			name = m[2].str() + m[1].str();
		}

		return name;
	}
}
