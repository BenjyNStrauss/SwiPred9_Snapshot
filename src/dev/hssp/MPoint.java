package dev.hssp;

import assist.numerical.Quaternion;

/**
 * from primitives-3d.h
 * @translator Benjamin Strauss
 *
 */

public class MPoint {
	double mX, mY, mZ;
	
    public MPoint() { 
    	mX = 0; mY = 0; mZ = 0;
    }
    
    public MPoint(double x, double y, double z) {
    	mX = x; mY = y; mZ = z;
    }
    
    public MPoint(MPoint rhs) {
    	mX = rhs.mX; mY = rhs.mY; mZ = rhs.mZ;
    }
    
    public static MPoint operator_plus( MPoint lhs, MPoint rhs) {
    	
    }
    
    public static MPoint operator_minus( MPoint lhs, MPoint rhs) {
    	
    }
    
    public static MPoint operator_minus( MPoint pt) {
    	
    }
    
    public static MPoint operator_times( MPoint pt, double f) {
    	
    }
    
    public static MPoint operator_div( MPoint pt, double f) {
    	
    }

    public MPoint operator_eq(MPoint rhs) {
    	mX = rhs.mX;
    	mY = rhs.mY;
    	mZ = rhs.mZ;
    	
    	return this;
    }

    public MPoint operator_plus_eq(MPoint rhs) {
    	mX += rhs.mX;
    	mY += rhs.mY;
    	mZ += rhs.mZ;

    	return this;
	}
    
    public MPoint operator_minus_eq(MPoint rhs) {
    	mX -= rhs.mX;
    	mY -= rhs.mY;
    	mZ -= rhs.mZ;

    	return this;
	}
	
    public MPoint operator_plus_eq(double f) {
    	mX += f;
    	mY += f;
    	mZ += f;

    	return this;
	}
    
    public MPoint operator_minus_eq(double f) {
    	mX -= f;
    	mY -= f;
    	mZ -= f;

    	return this;
    }

    public MPoint operator_times_eq(double f) {
    	mX *= f;
    	mY *= f;
    	mZ *= f;

    	return this;
    }
    
    public MPoint operator_div_eq(double f) {
    	mX /= f;
    	mY /= f;
    	mZ /= f;

    	return this;
    }

    public double Normalize() {
    	double length = mX * mX + mY * mY + mZ * mZ;
    	if (length > 0) {
    		length = Math.sqrt(length);
    		mX /= length;
    	    mY /= length;
    	    mZ /= length;
    	}
    	return length;
    }
    
    public void Rotate(Quaternion q) {
    	Quaternion p = new Quaternion(0, mX, mY, mZ);

    	p = Quaternion.mult(q, p);
    	p = Quaternion.mult(p, q.conjugate());

    	mX = p.i; //p.R_component_2();
    	mY = p.j; //p.R_component_3();
    	mZ = p.k; //p.R_component_4();
	}
}
