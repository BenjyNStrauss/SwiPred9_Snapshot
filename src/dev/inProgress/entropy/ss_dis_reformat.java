package dev.inProgress.entropy;

import java.util.List;

import assist.translation.python.PythonTranslator;

//import itertools
//import sys

public class ss_dis_reformat extends PythonTranslator {

	public String input_ss_dis;
	public String output_ss_dis = "ss_dis.json";
	public String _tmp = "tmp.txt";
	
	//# sourced: https://alexwlchan.net/2018/12/iterating-in-fixed-size-chunks/
	public static void chunked_iterable(iterable, size) {
	    it = iter(iterable);
	    while (true) {
	        chunk = tuple(itertools.islice(it, size));
	        if(! chunk) {
	            break;
	        } yield chunk;
	    }
	}

	static String secstr_disorder_merge(String secstr, Object disorder) {
	    //replace all spaces with dashes
	    //import pdb;
		//import assist.translation.python.PythonTranslator;
		pdb.set_trace();
	    List<Character> replacement = list(secstr); // removed replace, this is already addressed in create_dict by making temp file
	    //if there should be an X, replace the dash with an X
	    for(int i = 0; i < replacement.size(); ++i) {
	        if(disorder[i] == "X") {
	            replacement.set(i, 'X');
	        }
	    }
	    String replacement_str = "".join(replacement);
	    return replacement_str;
	}

	void create_dict() {
	    /* ## moving data to near block where it is populated
	    data = {}
	    record_list = []
	    */
	    int record_count = 0;
	
	    print("Starting to read {input_ss_dis} and write temporary file "+_tmp+"...");
	    //replace all the spaces in the file and replaces it with dashes
	    //so that no information goes missing when parsing through the file
	    File replace = open(_tmp, "w"); // w+ -> w : w+ allows read and write, we only need to write the tmp file
	    with open(input_ss_dis,"r") as in_file: // r+ -> r : r+ is allows reading and writing access, we only need reading
	        for(String line : in_file) {
	            if(line.contains(">")) {
	                record_count += 1;
	            }
	            fixed_line = line.replace(" ", "L");
	            replace.write(fixed_line);
	        }
	    replace.close();
	    print("Success");
	
	    print("Now to parse through the whole thing....");
	    data = dict();
	    triplet_records = chunked_iterable(iterable=SeqIO.parse(_tmp,"fasta"), size=3);
	    for(i, (primary, secstruct, disorder) : enumerate(triplet_records)):
	        print(f"Coverting record {i+1} of {int(record_count/3)} records {' '*8}",end="\r");
	        merge = secstr_disorder_merge(secstr=str(secstruct.seq), disorder=str(disorder.seq));
	        new_id = primary.id.split(":")[0] + primary.id.split(":")[1];
	        data[new_id] = str(primary.seq), merge;
	
	    print("Success");
	            
        print("Finally to create the JSON File...");
        with open(output_ss_dis, "w") as file { // w+ -> w
                json.dump(data,file, indent=4);
        }
        
        print("Finished!");
    }
	
    public static void main(String[] args) {
    	String input_ss_dis = args[1];
    	create_dict();
    }
}
