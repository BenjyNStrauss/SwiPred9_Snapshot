package view;

import modules.cluster.ClusterLoader;
import utilities.LocalToolBase;

/**
 * 
 * @author Benjy Strauss
 *
 */

public class ErrorView extends LocalToolBase {

	public static void displayErrors(ClusterLoader clusterResult) {
		if(clusterResult == null) {
			qerr("Error displaying clustering errors, result is null");
			return;
		}
		
		qp("--------------------------");
		qp("Errors from clustering:");
		for(String str: clusterResult.allErrors()) {
			qp("Error: " + str);
		}
	}
}
