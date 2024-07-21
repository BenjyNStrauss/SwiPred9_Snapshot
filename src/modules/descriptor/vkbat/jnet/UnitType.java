package modules.descriptor.vkbat.jnet;

/**
 * 
 * @translator Benjy Strauss
 *
 */

public class UnitType {
	int idNum = -1;
	/* Activation       */
	float act;
	/* Bias of the Unit */
	float Bias;
	/* Number of predecessor units */
	int	NoOfSources;
	/* predecessor units */
	UnitType sources[];
	/* weights from predecessor units */
	float weights[];
	
	public UnitType(double act, double Bias, int NoOfSources, UnitType[] sources, float[] weights) {
		this.act = (float) act;
		this.Bias = (float) Bias;
		this.NoOfSources = NoOfSources;
		this.sources = sources;
		this.weights = weights;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(" + idNum + ")");
		builder.append("act="+act+",\t");
		builder.append("Bias="+Bias+",\t");
		builder.append("NoOfSources="+NoOfSources+",\t");
		return builder.toString();
	}
}
