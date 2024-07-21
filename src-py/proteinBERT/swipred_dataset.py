import numpy as np
import h5py

'''
Created on Feb 1, 2024

@author: Benjamin Strauss

Notes: "test_set_mask" to be an all-false boolean vector if no test set, has to be the size of the data set

'''

REDUNDANCY       = "wr"
DEFAULT_DIR      = "../../input/swipredbert/";
DEFAULT_INFILE   = DEFAULT_DIR + REDUNDANCY + "/";
DEFAULT_OUTFILE  = DEFAULT_DIR + REDUNDANCY + "/";
DEFAULT_MASKFILE = DEFAULT_DIR + REDUNDANCY + "/testmask.txt";

data_sets = { "ps3token":6, "ps8token":6, "ptoken":4, "s8token":4, "s3token":4, "pberttoken":4 }

class SwipredSequence:
    def __init__(self, line: str, lineEnd = 6):
        fields = line.split(":");
        self.protein = fields[0]
        self.chain   = fields[1]
        self.uniprot = fields[2]
        self.go_id   = fields[3]
        #get rid of Start and End tokens
        self.tokens  = fields[4][lineEnd:-lineEnd]
    
    def seqLen(self):
        token_array = self.tokens.split(", ");
        return len(token_array);

def markArrayWithSet(bool_array, mark_annotation, annotation_set):
    index = 0;
    for annotation in annotation_set:
        if annotation == mark_annotation:
            bool_array[index] = True;
            break;
        else:
            ++index;

def primarySeq(line):
    tokens = line.split(", ")
    return "".join(token[0] for token in tokens)

def to10ClassSS(line):
    tokens = line.split(", ")
    newLine = "".join(token[1] for token in tokens)
    newLine = newLine.replace("5", "B")
    newLine = newLine.replace("9", "C")
    newLine = newLine.replace("6", "E")
    newLine = newLine.replace("2", "G")
    newLine = newLine.replace("1", "H")
    newLine = newLine.replace("3", "I")
    newLine = newLine.replace("4", "P")
    newLine = newLine.replace("7", "S")
    newLine = newLine.replace("8", "T")
    newLine = newLine.replace("0", "Z")
    return newLine

def to4ClassSS(line):
    tokens = line.split(", ")
    newLine = "".join(token[1] for token in tokens)
    newLine = newLine.replace("5", "E")
    newLine = newLine.replace("9", "C")
    newLine = newLine.replace("6", "E")
    newLine = newLine.replace("2", "H")
    newLine = newLine.replace("1", "H")
    newLine = newLine.replace("3", "H")
    newLine = newLine.replace("4", "H")
    newLine = newLine.replace("7", "C")
    newLine = newLine.replace("8", "C")
    newLine = newLine.replace("0", "D")
    return newLine

def simplifySecondary(line):
    '''
    0 stays as Disordered/missing
    1 -> Helix, 2 -> Sheet, 3 -> Other
    '''
    line = line.replace("2", "1")
    line = line.replace("3", "1")
    line = line.replace("4", "1")
    line = line.replace("5", "2")
    line = line.replace("6", "2")
    line = line.replace("7", "3")
    line = line.replace("8", "3")
    line = line.replace("9", "3")
    return line

def create_h5_dataset(infile_path: str, outfile: str, lineEnd = 6):
    
    with open(infile_path) as infile:
        with h5py.File(outfile, 'w') as h5f:
            
            #read in the entire infile
            lines = [line.rstrip('\n') for line in infile]
            #lines = infile.readLines();
            
            print("Finished Reading!")
            
            sequence_list = [];
            annotations = set();
            n_seqs = len(lines);
            
            #Creates the list of PDB-ids and the set of GO-annotations
            for index in range(0, len(lines)):
                seq_obj = SwipredSequence(lines[index], lineEnd);
                sequence_list.append(seq_obj);
                annotations.add(seq_obj.go_id);
            
            h5f.create_dataset('included_annotations', data = [annotation.encode('ascii') for annotation in annotations], dtype = h5py.string_dtype())
            uniprot_ids = h5f.create_dataset('pdb_ids', shape = (n_seqs,), dtype = h5py.string_dtype())
            seqs = h5f.create_dataset('seqs', shape = (n_seqs,), dtype = h5py.string_dtype())
            seq_lengths = h5f.create_dataset('seq_lengths', shape = (n_seqs,), dtype = np.int32)
            annotation_masks = h5f.create_dataset('annotation_masks', shape = (n_seqs, len(annotations)), dtype = bool)
            
            print("Starting writing loop:")
            
            #Writes everything to the h5
            for index in range(0, len(sequence_list)):
                uniprot_ids[index] = sequence_list[index].protein+":"+sequence_list[index].chain;
                seqs[index] = sequence_list[index].tokens;
                seq_lengths[index] = sequence_list[index].seqLen();
                
                go_matrix = np.zeros(len(annotations), dtype = bool);
                
                markArrayWithSet(go_matrix, sequence_list[index].go_id, annotations);
                    
                annotation_masks[index] = go_matrix;
                
                if index % 1000 == 0:
                    print("Written: "+str(index)+"/"+str(n_seqs))

def create_h5_testset(infile_path: str, outfile: str):
    with open(infile_path) as infile:
        
        maskLine: str = infile.readline().strip();
        
        with h5py.File(outfile, 'r') as h5f:
            uniprot_ids = [uniprot_id.decode('utf-8') for uniprot_id in h5f['pdb_ids']]
        
        with h5py.File(outfile, 'a') as h5f:
            #print(h5f.keys())
            
            test_set_mask = [];
            if len(maskLine) != len(uniprot_ids):
                raise ValueError("Mask does not fit data set! ("+str(len(maskLine))+" vs "+str(len(uniprot_ids))+")");
            
            for index in range(0, len(maskLine)):
                if maskLine[index] == '1':
                    test_set_mask.append(True);
                else:
                    test_set_mask.append(False);
            
            test_set_mask = np.array(test_set_mask)
            h5f.create_dataset('test_set_mask', data = test_set_mask, dtype = bool)
            print(h5f.keys())

#length = 565145, why 556192?
if __name__ == '__main__':
    for key in data_sets:
        create_h5_dataset(DEFAULT_INFILE+key+".txt", DEFAULT_OUTFILE+key+"-unp.h5", data_sets[key]);
        create_h5_testset(DEFAULT_MASKFILE, DEFAULT_OUTFILE+key+"-unp.h5");

