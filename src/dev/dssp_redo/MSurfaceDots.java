package dev.dssp_redo;

import assist.numerical.DecimalCoordinate;
import assist.translation.cplusplus.Vector;

/**
 * we use a fibonacci sphere to calculate the even distribution of the dots
 * @translator Benjamin Strauss
 *
 */

public class MSurfaceDots {
	
	private double mWeight;
	private	Vector<DecimalCoordinate> mPoints;
	
	private MSurfaceDots(int N) {
		int P = 2 * N + 1;

		final double kGoldenRatio = (1 + Math.sqrt(5.0f)) / 2;

		mWeight = (4 * DSSP_cpp.kPI) / P;

		for (int i = -N; i <= N; ++i) {
			double lat = Math.asin((2.0f * i) / P);
			double lon = ((i % kGoldenRatio) * 2 * DSSP_cpp.kPI / kGoldenRatio);

			mPoints.emplace_back(new DecimalCoordinate( Math.sin(lon) * Math.cos(lat), Math.cos(lon) * Math.cos(lat), Math.sin(lat) ));
		}
	}
	
	public int size() { return mPoints.size(); }
	public DecimalCoordinate operator_brackets(int inIx)  { return mPoints.get(inIx); }
	public double weight() { return mWeight; }

	public static MSurfaceDots Instance() {
		final int kN = 200;

		MSurfaceDots sInstance = new MSurfaceDots(kN);
		return sInstance;
	}
}
