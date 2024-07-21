package dev.dssp_redo;

import java.util.Iterator;

/**
 * 
 * @translator Benjamin Strauss
 * 
 * operator++ = op_pp()
 * operator-- = op_mm()
 * operator== = boolean equals()
 * 	
 * using res_iterator = typename std::vector<residue>::iterator;
 * using iterator_category = std::bidirectional_iterator_tag;
 * using value_type = residue_info;
 * using difference_type = std::ptrdiff_t;
 * using pointer = value_type *;
 * using reference = value_type &;
 */

public class DSSP_Iterator implements Iterator<ResidueInfo> {
	//iterator(iterator i) = default;
	//iterator &operator=(const iterator &i) = default;
	//reference operator*() { return m_current; }
	//pointer operator->() { return &m_current; }
	
	private ResidueInfo m_current;
	
	public DSSP_Iterator(Residue res) {
		//Why does this work in the c++
		m_current = res;
	}
		
	public DSSP_Iterator op_pp() {
		++m_current.m_impl;
		return this;
	}
		
	public DSSP_Iterator op_pp(int value) {
		auto tmp(this);
		this.op_pp();
		return tmp;
	}

	public DSSP_Iterator op_mm() {
		--m_current.m_impl;
		return this;
	}
	
	public DSSP_Iterator op_mm(int value) {
		auto tmp(this);
		this.op_mm();
		return tmp;
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResidueInfo next() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean equals(Object rhs) {
		if(rhs instanceof DSSP_Iterator) {
			return m_current.m_impl == ((DSSP_Iterator) rhs).m_current.m_impl;
		} else {
			return false;
		}
	}
}
