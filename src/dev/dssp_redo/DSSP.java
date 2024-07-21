package dev.dssp_redo;

import java.util.Iterator;

import assist.Deconstructable;
import assist.translation.cplusplus.CppTranslator;
import assist.translation.cplusplus.OStream;
import assist.util.Pair;
import dev.cif.DataBlock;

/**
 * 
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

/**
 * 
 * operator[] -> get()
 * 
 * @translator Benjamin Strauss
 * 
 * // To access residue info by key, i.e. LabelAsymID and LabelSeqID
 * using key_type = std::tuple<std::string, int>;
 * using value_type = residue_info;
 *
 */

public class DSSP extends CppTranslator implements Deconstructable, Iterable<ResidueInfo> {
	//public struct residue;

	private DSSP_impl m_impl;
	
	//constexpr
	static final int kHistogramSize = 30;

	DSSP(final DataBlock db, int model_nr, int min_poly_proline_stretch, boolean calculateSurfaceAccessibility) {
		m_impl = new DSSP_impl(db, model_nr, min_poly_proline_stretch);
		
		if (calculateSurfaceAccessibility) {
			std.thread t(std.bind(DSSP_impl.calculateSurface, m_impl));
			m_impl.calculateSecondaryStructure();
			t.join();
		} else {
			m_impl.calculateSecondaryStructure();
		}
	}
	
	DSSP(final cif.mm.structure s, int min_poly_proline_stretch_length, boolean calculateSurfaceAccessibility) {
		this(s.get_datablock(), (int) (s.get_model_nr()), min_poly_proline_stretch_length, calculateSurfaceAccessibility);
	}

	/*DSSP(final DSSP dssp) {
		//= delete
	}*/
	
	//DSSP operator=(const dssp &) = delete;

	Statistics get_statistics() { return m_impl.mStats; }
	
	DSSP_Iterator begin() {
		Residue[] array = new Residue[m_impl.mResidues.size()];
		m_impl.mResidues.toArray(array);
		
		return new DSSP_Iterator(m_impl.mResidues.empty() ? null : array);
	}
	
	/**
	 * dssp::iterator dssp::end() const
	 * @return
	 */
	DSSP_Iterator end() {
		// careful now, MSVC is picky when it comes to dereferencing iterators that are at the end.
		Residue[] res = null;
		if (! m_impl.mResidues.empty()) {
			//res = m_impl->mResidues.data();
			//res += m_impl.mResidues.size();
			res = new Residue[m_impl.mResidues.size()];
			m_impl.mResidues.toArray(res);
			//DSSP_impl
		}

		return new DSSP_Iterator(res);
	}

	/**
	 * ResidueInfo operator[](final key_type key);
	 */
	ResidueInfo get(final Pair<String, Integer> key) {
		ResidueInfo i = std.find_if(begin(), end(), [key](final ResidueInfo res) { 
				return res.asym_id() == std.get<0>(key) && res.seq_id() == std.get<1>(key); 
		});
			
		if (i == end()) {
			throw new IndexOutOfBoundsException("Could not find residue with supplied key");
		}
			
		return i;
	}

	boolean empty() { return begin().equals(end()); }

	// --------------------------------------------------------------------
	// Writing out the data, either in legacy format...

	void write_legacy_output(OStream os) {
		DSSP_IO.writeDSSP(this, os);
	}

	// ... or as annotation in the cif::datablock
	void annotate(DataBlock db, boolean writeOther, boolean writeDSSPCategories) {
		annotateDSSP(db, this, writeOther, writeDSSPCategories);
	}

	// convenience method, when creating old style DSSP files

	String get_pdb_header_line(PDB_RecordType pdb_record) {
		switch (pdb_record) {
			case HEADER:
				return m_impl.GetPDBHEADERLine();
			case COMPND:
				return m_impl.GetPDBCOMPNDLine();
			case SOURCE:
				return m_impl.GetPDBSOURCELine();
			case AUTHOR:
				return m_impl.GetPDBAUTHORLine();
			default:
				return "";
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void deconstruct() {
		try {
			m_impl.deconstruct();
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public Iterator<ResidueInfo> iterator() {
		//TODO unfixable!
		return m_impl.mResidues.iterator();
	};
}