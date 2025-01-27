package dev.inProgress.entropy;

//from collections import Counter
//from Bio import AlignIO
//import pandas as pd

public class assess_isSwitch {

	private Object i_primary;
	private Object i_ssdis;
	private Object o_full_csv;
	private Object val_cluster_repID;

	private static final String[] LETTERS = {
	    "X",// = residues that exist in the originally studied molecule
	        // (the SEQRES records of the PDB file)
	        // but not in the observed structure (the coordinate records of the PDB file)
	    "H",// = alpha helix
	    "B",// = residue in isolated beta-bridge
	    "E",// = extended strand, participates in beta ladder
	    "G",// = 3-helix (3/10 helix)
	    "I",// = 5 helix (pi helix)
	    "T",// = hydrogen bonded turn
	    "S",// = bend
	    "L", // CUSTOM = stands for a loop or other irregular structure
	    "-", // indel from alignment
	    "?" // missing in SS_DIS
	};
	
	public assess_isSwitch(Object arg1, Object arg2, Object arg3, Object arg4) {
		pd.options.mode.chained_assignment = null;  // default='warn'
		i_primary = arg1;
		i_ssdis = arg2;
		o_full_csv = arg3;
		val_cluster_repID = arg4;
	}
	
	private static final String[] DEFINED_SECONDARY_STRUCTURE = {"H","B","E","G","I","T","S","L"};

	private Object _create_ssdis_analysis(Object ssdis) {
	    /* appends analysis columns based on input dataframe
	
	    returns:
	        dataframe with secondary structure data based analysis
		*/
	
	    // remove all alignments with ? indicating SS_DIS did not contain them
	    // removes any column (e.g. an aligned sequence) where the ss_dis data was missing
	    // print((ssdis == '?').any());
	    ssdis = ssdis.loc[:, ~(ssdis == '?').any()];
	
	    for(i, row : enumerate(ssdis.iterrows())) {
	        counts = Counter(row[1]);
	        cluster_size = len(row[1]);
	        // if cluster size drops below at least 2 after removing missing ss_dis columns
	        // raise ValueError
	        // if cluster_size < 2:
	        //    raise ValueError("One or Zero sequences remaining after removing missing ss_dis columns")
	
	        // generate counts and proportions for each secondary structure letter
	        for(String letter : LETTERS) {
	            ssdis.at[i, letter+"_count"] = counts[letter];
	            // size of the characters MINUS indel characters at the position
	            // this will give proportion of residues present, ignoring alignment indels
	            // set proportios to zero in first case
	            // (where all positions are indels, this occurs when an alignment like '-,-,?' occurs
	            // because ? columns are removed)
	            if(cluster_size - counts["-"] == 0) {
	                ssdis.at[i, letter+"_proportion_of_present"] = 0;
	                ssdis.at[i, "-_proportion_of_present"] = 1;
	            } else {
	                ssdis.at[i, f"{letter}_proportion_of_present"] = (counts[letter]/(cluster_size-counts["-"]));
	            }
	        }
	    }
	    ssdis["ClusterSize"] = cluster_size;
	
	    /*########################################
	    # START SWITCH ASSIGNMENT
	    # switch categories categorized here
	    # WARN: THE ORDER IS VERY IMPORTANT HERE AS LATER CATERGORIES SUPERSEDE EARLY ONES
	
	    # default value set*/
	    ssdis[f"isSwitch"] = "SwitchObserved";
	
	    //# set to Missing Observations
	    ssdis.loc[ssdis["X_count"] > 1, "isSwitch"] = "NoSwitchObserved_WithUnobservedPositions";
	
	
	    //# 100% SINGLE DEFINED_SECONDARY_STRUCTURE
	    for(String letter : DEFINED_SECONDARY_STRUCTURE) {
	        ssdis.loc[ssdis[letter"_proportion_of_present"] == 1, "isSwitch"] = "NoSwitchObserved";
	    }
	
	    //# END SWITCH ASSIGNMENT
	    //########################################
	
	    return ssdis;
	}
	
	private Object isSwitchPlus(Object primary_aln_path, Object ssdis_csv_path) {
	    /* Combines primary sequence data and ssdis aligned data
	
	    Outputs:
	        isSwitch
	        percentage breakdown in secondary structures
	        length of alignment (e.g. cluster size)
	
	    */
	    // import ssdis data and generate analysis columns
	    ssdis = _create_ssdis_analysis(pd.read_csv(ssdis_csv_path));
	
	    // import primary sequence alignment and append to dataframe
	    aln = AlignIO.read(primary_aln_path, format="fasta");
	    for(seq : aln) {
	        try {
	            ssdis[seq.id+"_primary"] = list(seq.seq);
	        } catch (ValueError e) {
	            //#print(seq.seq)
	            //display(ssdis)
	            raise e;
	        }
	    }
	
	    ssdis_datasets = {};
	    for(String seq : aln) {
	        seq_primary = seq.id+"_primary";
	        save_these_columns = [seq_primary, "ClusterSize", "isSwitch"];
	        save_these_columns.extend([f"{letter}_count" for letter in LETTERS]);
	        print(save_these_columns);
	        dataset_df = ssdis.filter(axis="columns", items=save_these_columns);
	        // make column label generic for primary sequence
	        dataset_df = dataset_df.rename(axis="columns", mapper={seq_primary:"Residue"});
	        ssdis_datasets[seq.id] = dataset_df;
	    }
	
	
	    return ssdis_datasets, ssdis;
	}
	
	public void go() {
		dataset_dfs, full_df = isSwitchPlus(i_primary, i_ssdis);
		
		// save results to both dataset compatible csv and full cluster csv format
		for(seqID, df : dataset_dfs.items()) {
		    if(seqID == val_cluster_repID) {
		        o_dataset_path = seqID+".csv";
		        df.index = pd.Series(range(1,len(df)+1), name="query_index");
		        df.to_csv(o_dataset_path);
		    }
		}
		full_df.to_csv(o_full_csv);
	}
}
