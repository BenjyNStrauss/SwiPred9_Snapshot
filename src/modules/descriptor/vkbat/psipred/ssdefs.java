package modules.descriptor.vkbat.psipred;

import utilities.LocalToolBase;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public interface ssdefs {
	
	static final int MAXSEQLEN = 10000;

	static int SQR(int x) { return x*x; }
	static float SQR(float x) { return x*x; }
	static double SQR(double x) { return x*x; }
	
	static int MAX(int x, int y) { return LocalToolBase.max(x, y); }
	//static float MAX(float x, float y) { return ToolBase.max(x, y); }	
	static double MAX(double x, double y) { return LocalToolBase.max(x, y); }
	static int MIN(int x, int y) { return LocalToolBase.min(x, y); }
	//static float MIN(float x, float y) { return ToolBase.min(x, y); }
	static double MIN(double x, double y) { return LocalToolBase.min(x, y); }


	/* logistic 'squashing' function (output range +/- 1.0) */
	static double logistic(double x) { return (1.0F / (1.0F + Math.pow(Math.E, -(x)))); }
	static float logistic(float x) { return (float) (1.0F / (1.0F + Math.pow(Math.E, -(x)))); }

	/* Rectifier function */
	static double rectifier(double x) { return ((x) < 0.0F ? 0.0F : (x)); }
	static float rectifier(float x) { return ((x) < 0.0F ? 0.0F : (x)); }
	
}
