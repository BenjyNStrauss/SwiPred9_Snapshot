package dev.hssp;

/**
 * 
 * @author Benjamin Strauss
 *
 */

public interface HSSP_Constants {
	public static final int kHistogramSize = 30;
	public static final double kMinimalDistance = 0.5;
	public static final double kMinimalCADistance = 9.0;
	public static final double kMinHBondEnergy = -9.9;
	public static final double kMaxHBondEnergy = -0.5;
	public static final double kCouplingConstant = -27.888;  //  = -332 * 0.42 * 0.2
	public static final double kMaxPeptideBondLength = 2.5;
	
	public static final double kRadiusN = 1.65;
	public static final double kRadiusCA = 1.87;
	public static final double kRadiusC = 1.76;
	public static final double kRadiusO = 1.4;
	public static final double kRadiusSideAtom = 1.8;
	public static final double kRadiusWater = 1.4;
	
	public static final int P_WIN = 1;
	
	public static final MResidueInfo[] kResidueInfo = structure.kResidueInfo;
	
	public static final int kResidueTypeCount = MResidueType.kResidueTypeCount.ordinal();
	
}
