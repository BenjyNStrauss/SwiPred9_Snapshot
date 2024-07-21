package biology.descriptor;

import assist.util.LabeledList;
import utilities.SwiPredObject;

/**
 * An AbstractMetric is something that can be converted to a metric
 * 
 * @author Benjy Strauss
 *
 */

public interface AbstractMetric extends SwiPredObject {
	LabeledList<AbstractMetric> ALL_METRICS = new LabeledList<AbstractMetric>() {
		private static final long serialVersionUID = 1L;
		{
			for(Identifier value: Identifier.values()) { add(value); }
			for(DescriptorType value: DescriptorType.values()) { add(value); }
			for(VKPred value: VKPred.values()) { add(value); }
		}
	};

	public Metric toMetric();
}
