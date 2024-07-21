package dev.dssp_redo;

import assist.numerical.DecimalCoordinate;
import assist.translation.cplusplus.Vector;

/**
 * operator() -> apply
 * @translator Benjamin Strauss
 *
 */

public class Accumulator {
	public Vector<Candidate> m_x = new Vector<Candidate>();
	
	public void apply(final DecimalCoordinate a, final DecimalCoordinate b, double d, double r) {
		double distance = DSSP_cpp.distance_sq(a, b);

		d += DSSP_cpp.kRadiusWater;
		r += DSSP_cpp.kRadiusWater;

		double test = d + r;
		test *= test;

		if (distance < test && distance > 0.0001) {
			Candidate c = new Candidate( DSSP_cpp.op_minus(b, a), r * r, distance );

			m_x.push_back(c);
			push_heap(m_x.begin(), m_x.end());
		}
	}

	public void sort() {
		sort_heap(m_x.begin(), m_x.end());
	}
	
}
