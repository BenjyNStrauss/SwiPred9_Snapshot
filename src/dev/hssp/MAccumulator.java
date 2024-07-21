package dev.hssp;

import assist.exceptions.NotYetImplementedError;
import assist.translation.cplusplus.Vector;

/**
 * from structure.cpp
 * @translator Benjamin Strauss
 *
 */

public class MAccumulator implements HSSP_Constants {
	
	public Vector<Candidate> m_x;
	
	//void operator()(const MPoint& a, const MPoint& b, double d, double r)
	public void operator_parens(MPoint a, MPoint b, double d, double r) {
	    double distance = primatives_3d.DistanceSquared(a, b);

	    d += kRadiusWater;
	    r += kRadiusWater;

	    double test = d + r;
	    test *= test;

	    if (distance < test && distance > 0.0001) {
	    	Candidate c = new Candidate( MPoint.operator_minus(b, a), r * r, distance );

	    	m_x.push_back(c);
	    	push_heap(m_x.begin(), m_x.end());
	    }
	}

	public void operator_parens(MAtom a, MAtom b, double d, double r) {
		//TODO find implementation!
		throw new NotYetImplementedError();
	}
	
	public void sort() {
		sort_heap(m_x.begin(), m_x.end());
	}
	
}
