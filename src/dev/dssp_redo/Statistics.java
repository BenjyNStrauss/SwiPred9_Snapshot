package dev.dssp_redo;

/**
 * 
 * @translator Benjamin Strauss
 *
 */

public class Statistics {
		
	public class Count {
		int residues, chains, SS_bridges, intra_chain_SS_bridges, H_bonds;
		int H_bonds_in_antiparallel_bridges, H_bonds_in_parallel_bridges;
		int[] H_Bonds_per_distance = new int[11];
	}
	
	public class Histogram {
		int[] residues_per_alpha_helix = new int[DSSP.kHistogramSize];
		int[] parallel_bridges_per_ladder = new int[DSSP.kHistogramSize];
		int[] antiparallel_bridges_per_ladder = new int[DSSP.kHistogramSize];
		int[] ladders_per_sheet = new int[DSSP.kHistogramSize];
	}
	
	double accessible_surface;
	Count count;
	Histogram histogram;
	
	public Statistics() {
		count = new Count();
		histogram = new Histogram();
	}
};
