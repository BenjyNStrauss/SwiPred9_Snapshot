package dev.hssp;

/**
 * from structure.cpp
 * @translator Benjamin Strauss
 *
 */

public class Candidate {
	
	MPoint  location;
    double  radius;
    double  distance;
    
    public Candidate(MPoint location, double radius, double distance) {
    	this.location = location;
    	this.radius = radius;
    	this.distance = distance;
    }

    boolean operator_lt(Candidate rhs) { return distance < rhs.distance; }
}
