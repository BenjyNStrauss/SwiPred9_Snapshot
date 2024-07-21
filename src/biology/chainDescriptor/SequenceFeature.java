package biology.chainDescriptor;

/**
 * 
 * @author Benjamin Strauss
 * 
 * No mode,
 * 
 */

public enum SequenceFeature {
	ARITHMETIC_MEAN,
	GEOMETRIC_MEAN,
	HARMONIC_MEAN,
	
	QUADRATIC_MEAN,
	CUBIC_MEAN,
	
	STANDARD_DEVIATION, //average distance from central tendency
	VARIANCE, //sqrt of std dev
	MEDIAN,
	MINIMUM,
	MAXIMUM,
	RANGE,
	MIDRANGE,
	
	SKEW, //are more points less or greater than the mean
	KURTOSIS,
	
	//Quartiles top 1/4 of data
	//Skew
	//Kurtosis - how clustered around mean
}
