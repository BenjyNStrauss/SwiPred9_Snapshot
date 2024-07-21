package dev.hssp;

import assist.translation.cplusplus.Vector;

/**
 * from structure.cpp
 * @translator Benjamin Strauss
 *
 */

public class MSurfaceDots {
	private Vector<MPoint> mPoints;
	private double mWeight;
	
	public static MSurfaceDots Instance() {
		int kN = 200;
		MSurfaceDots sInstance = new MSurfaceDots(kN);
		return sInstance;
	}

	public int size() { return mPoints.size(); }
	public MPoint operator_brackets(int inIx) { return mPoints.get(inIx); }
	public double weight() { return mWeight; }

	private MSurfaceDots(int N) {
		int P = 2 * N + 1;

		double kGoldenRatio = (1 + Math.sqrt(5.0)) / 2;

		mWeight = (4 * kPI) / P;

		for(int i = -N; i <= N; ++i) {
		    double lat = Math.asin((2.0 * i) / P);
		    double lon = fmod(i, kGoldenRatio) * 2 * kPI / kGoldenRatio;

		    MPoint p;
		    p.mX = Math.sin(lon) * Math.cos(lat);
		    p.mY = Math.cos(lon) * Math.cos(lat);
		    p.mZ = Math.sin(lat);

		    mPoints.push_back(p);
	  	}
	}
}
