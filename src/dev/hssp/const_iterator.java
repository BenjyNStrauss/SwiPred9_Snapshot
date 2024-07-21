package dev.hssp;

import java.util.Iterator;

/**
 * Code from iocif.h
 * @translator Benjamin Strauss
 *
 */

//typedef std::iterator<std::forward_iterator_tag, row>  base_type;
//typedef base_type::reference reference;
//typedef base_type::pointer pointer;

//struct const_iterator : public std::iterator<std::forward_iterator_tag,const row>
public class const_iterator implements Iterator<forward_iterator_tag, row>{
	private m_record m_rec;
	private row m_row;

    const_iterator(m_record rec, row row) {
    	m_rec = (rec);
    	m_row = (row);
    }

    const_iterator(const_iterator iter) {
    	m_rec = (iter.m_rec);
    	m_row = (iter.m_row);
    }

    const_iterator operator_eq(const_iterator iter) {
    	m_row = iter.m_row;
    	return this;
    }

    reference operator() { return m_row; }
    pointer operator_ptr() { return m_row; }

    const_iterator operator_plus_plus() {
    	m_rec.advance(m_row);
    	return this;
    }

    const_iterator operator_plus_plus(int) {
    	const_iterator iter = new const_iterator(this);
    	operator++();
    	return iter;
    }

    boolean operator_eqeq( const_iterator iter) {
    	return m_row == iter.m_row;
    }

    boolean operator_neq(const_iterator iter) {
    	return ! operator_eqeq(iter);
    }
}
