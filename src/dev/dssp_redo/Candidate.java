package dev.dssp_redo;

import assist.numerical.DecimalCoordinate;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Candidate {
	DecimalCoordinate location;
	double radius;
	double distance;
	
	public Candidate() { }
	
	public Candidate(DecimalCoordinate location, double radius, double distance) {
		this.location = location;
		this.radius = radius;
		this.distance = distance;
	}
	
	boolean operator_lt(final Candidate rhs) {
		return distance < rhs.distance;
	}
}
