package dev.hssp;

/**
 * Copyright Maarten L. Hekkelman, Radboud University 2008-2011.
 * Copyright Coos Baakman, Jon Black, Wouter G. Touw & Gert Vriend, Radboud university medical center 2015.
 * Distributed under the Boost Software License, Version 1.0.
 * (See accompanying file LICENSE_1_0.txt or copy at http://www.boost.org/LICENSE_1_0.txt)
 * some data types and routines for working with 3d data
 * 
 * from primatives-3d.h
 * @translator Benjamin Strauss
 *
 */

public class primatives_3d {
	public static double DotProduct(MPoint a, MPoint b) {
		return a.mX * b.mX + a.mY * b.mY + a.mZ * b.mZ;
	}

	public static MPoint CrossProduct(MPoint a, MPoint b) {
		return new MPoint(a.mY * b.mZ - b.mY * a.mZ,
				a.mZ * b.mX - b.mZ * a.mX,
				a.mX * b.mY - b.mX * a.mY);
	}

	public static double DistanceSquared(MPoint a, MPoint b) {
	  return
	    (a.mX - b.mX) * (a.mX - b.mX) +
	    (a.mY - b.mY) * (a.mY - b.mY) +
	    (a.mZ - b.mZ) * (a.mZ - b.mZ);
	}

	public static double Distance(MPoint a, MPoint b) {
		return Math.sqrt( (a.mX - b.mX) * (a.mX - b.mX) + (a.mY - b.mY) * (a.mY - b.mY) + (a.mZ - b.mZ) * (a.mZ - b.mZ));
	}
	
	public static double CosinusAngle( MPoint p1, MPoint p2, MPoint p3, MPoint p4) {
		MPoint v12 = MPoint.operator_minus(p1, p2);
		MPoint v34 = MPoint.operator_minus(p3, p4);

		double result = 0;

		double x = DotProduct(v12, v12) * DotProduct(v34, v34);
		if (x > 0) {
			result = DotProduct(v12, v34) / Math.sqrt(x);
		}

		return result;
	}
}
