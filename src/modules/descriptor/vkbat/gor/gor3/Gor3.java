package modules.descriptor.vkbat.gor.gor3;

import java.util.Collections;

import assist.util.LabeledHash;
import assist.util.LabeledList;
import assist.util.Pair;
import biology.amino.AminoAcid;
import biology.descriptor.VKPred;
import biology.molecule.types.AminoType;
import biology.protein.ChainID;
import biology.protein.ProteinChain;
import install.FileManager;
import modules.descriptor.vkbat.Vkbat;
import tools.download.fasta.PDB_Downloader;
import tools.reader.fasta.pdb.PDB_HashReader;
import utilities.LocalToolBase;

/**
 * Class GOR3 is made to predict the protein secondary structure
 * with GOR III algorithm
 * Java translation of https://github.com/krtk0/gor3/blob/master/gor3-notebook.ipynb
 * 
 * @translator Benjamin Strauss
 *
 */

public class Gor3 extends LocalToolBase {
	private static final int[] NEIGHBOR_INDECES = {-8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8}; 
	
	private static final String[][] DATASET = Gor3Util.getDSSPInfo(FileManager.GOR3_DSSP);
	//private static final LabeledList<String> PDB_CODE = Gor3Util.getPDBCode(DATASET);
	
	private final String PDB_code;
	private final LabeledList<Character> acids;
	private boolean nullIssue = false;
	
	public Gor3() { this(null); }
	
    public Gor3(String PDB_code) {
    	this.PDB_code = PDB_code;
        acids = new LabeledList<Character>();
        for(char ch: Gor3Util.ACIDS.values()) {
        	acids.add(ch);
        }
    }
    
    /**
     * Made to calculate the total number of acids appearence for each structure.
     * @return dictionary {structure: number_of_appearence}
     */
    private LabeledHash<String, Integer> _calc_fs() {
        LabeledHash<String, Integer> fs = new LabeledHash<String, Integer>();
        int helix_fs = 0;
        int sheet_fs = 0;
        int coil_fs = 0;
        
        for(String[] acid : DATASET) {
            if(!acid[0].equals(PDB_code)) {
                if(acid[4].equals("Helix")) {
                    helix_fs += 1;
                }
                if(acid[4].equals("Sheet")) {
                    sheet_fs += 1;
                }
                if(acid[4].equals("Coil")) {
                    coil_fs += 1;
                }
            }
        }
        
        fs.put("Helix", helix_fs);
        fs.put("Sheet", sheet_fs);
        fs.put("Coil", coil_fs);
        
        return fs;
    }
    
    /**
     * Made to calculate acid self information.
     * @return dictionary {acid: (helix_info, sheet_info, coil_info)}
     * 
     * OK - Tested and working
     */
    private LabeledHash<Character, Double[]> _calc_self_info(char[] sequence) {
    	LabeledHash<Character, Double[]> self_info = new LabeledHash<Character, Double[]>();
    	LabeledList<Character> acids_list = new LabeledList<Character>();
        acids_list.addAll(acids);
        
        LabeledHash<String, Integer> fs = _calc_fs();
        double helix_fs = fs.get("Helix");
        double sheet_fs = fs.get("Sheet");
        double  coil_fs = fs.get("Coil");
        
        for(char acid : sequence) {
            char current = acid;
            if(acids_list.contains(acid)) {
                
            	double helix_fsr = 0;
            	double sheet_fsr = 0;
            	double coil_fsr = 0;
                
                for(String[] other : DATASET) {
                    if(!other[0].equals(PDB_code)) {
                        if(other[3].trim().charAt(0) == current) {
                            if(other[4].equals("Helix")) {
                                helix_fsr += 1;
                            }
                            if(other[4].equals("Sheet")) {
                                sheet_fsr += 1;
                            }
                            if(other[4].equals("Coil")) {
                                coil_fsr += 1;
                            }
                        }
                    }
                }
                
                //qp("*1*"+helix_fs +":"+ sheet_fs +":"+ coil_fs);
                //qp("*2*"+helix_fsr +":"+ sheet_fsr +":"+ coil_fsr);
                
                double helix_i = Math.log(helix_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr)) + Math.log((sheet_fs + coil_fs) / helix_fs);
                double sheet_i = Math.log(sheet_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr)) + Math.log((helix_fs + coil_fs) / sheet_fs);
                double coil_i = Math.log(coil_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr)) + Math.log((helix_fs + sheet_fs) / coil_fs);
                
                //qp(">"+helix_i +":"+ sheet_i +":"+ coil_i);
                
                self_info.put(current, new Double[]{helix_i, sheet_i, coil_i});
                // print((helix_i, sheet_i, coil_i))
                Character tmp = current;
                
                acids_list.remove(tmp);
            }
        }
        
        return self_info;
    }
    
    /**
     * Made to calculate acid pair information.
     * @return dictionary {acid:{position:{acid:(helix_info, sheet_info, coil_info)}}}
     * 
     * TODO something wrong here
     */
    private LabeledHash<Character, LabeledHash<Integer, LabeledHash<Character, Double[]>>> _calc_pair_info() {
        LabeledList<Character> acids_list = new LabeledList<Character>();
        acids_list.addAll(acids);
        Collections.sort(acids_list);
        LabeledHash<Character, LabeledHash<Integer, LabeledHash<Character, Double[]>>> pair_info = new LabeledHash<Character, LabeledHash<Integer, LabeledHash<Character, Double[]>>>();
        
        LabeledHash<String, Integer> fs = _calc_fs();
        double helix_fs = fs.get("Helix");
        double sheet_fs = fs.get("Sheet");
        double  coil_fs = fs.get("Coil");
        
        /*loop over 20 natural amino acids*/
        for(Character current_acid : acids_list) {
        	LabeledHash<Integer, LabeledHash<Character, Double[]>> acid_dict = new LabeledHash<Integer, LabeledHash<Character, Double[]>>();
            
        	/*loop over 16 neighbors*/
            for(int m : NEIGHBOR_INDECES) {
                LabeledHash<Character, Double[]> position_dict = new LabeledHash<Character, Double[]>();
                
                /*loop over 20 natural amino acids*/
                for(Character pair_acid : acids_list) {
                    //qp("@pair_acid: "+pair_acid);
                	double helix_fsr = 0;
                	double sheet_fsr = 0;
                	double coil_fsr = 0;
                    
                    //loop over all rest residues
                    //i in range(len(self.dataset) - 8)
                    for(int i = 0; i < DATASET.length - 8; ++i) {
                    	int val = i+m;
                    	if(val < 0) { val += DATASET.length; }
                    	
                        if(pair_acid == DATASET[val][3].charAt(0) &&
                        		DATASET[val][0].equals(DATASET[i][0]) &&
                        		!DATASET[i][0].equals(PDB_code) &&
                        		DATASET[i][3].charAt(0) == current_acid) {
                        	
                        	//qp(DATASET[val]);
                        	
                            if(DATASET[i][4].equals("Helix")) {
                                helix_fsr += 1;
                            }
                            if(DATASET[i][4].equals("Sheet")) {
                                sheet_fsr += 1;
                            }
                            if(DATASET[i][4].equals("Coil")) {
                                coil_fsr += 1;
                            }
                            //System.exit(3);
                        }
                    }
                    
                    
                    double helix_log_arg = 1;
                    double sheet_log_arg = 1;
                    double coil_log_arg = 1;
                    
                    if(((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr) != 0) {
                        helix_log_arg = helix_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr);
                    }

                    if(((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr) != 0) {
                        sheet_log_arg = sheet_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr);
                    }

                    if(((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr) != 0) {
                        coil_log_arg = coil_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr);
                    }

                    double helix_i = Math.log((helix_log_arg > 0) ? helix_log_arg : 1) + Math.log((sheet_fs +  coil_fs) / helix_fs);
                    double sheet_i = Math.log((sheet_log_arg > 0) ? sheet_log_arg : 1) + Math.log((helix_fs +  coil_fs) / sheet_fs);
                    double coil_i  = Math.log((coil_log_arg > 0)  ? coil_log_arg  : 1) + Math.log((helix_fs + sheet_fs) /  coil_fs);
                    
                    position_dict.put(pair_acid, new Double[] {helix_i, sheet_i, coil_i});
                }
                acid_dict.put(m, position_dict);
            }
            pair_info.put(current_acid, acid_dict);
        }
        return pair_info;
    }
    
    /**
     * Returns a secondary stucture prediction of the protein as a list:
     * @return [PDB_code, the secondary structure prediction, Q3]
     */
    public String sec_struc_prediction(String protein) {
    	LabeledHash<Character, Double[]> self_info = _calc_self_info(protein.toCharArray());
    	LabeledHash<Character, LabeledHash<Integer, LabeledHash<Character, Double[]>>> pair_info = _calc_pair_info();
        //protein_list = [entry for entry in self.dataset if entry[0] == PDB_CODE];
        
        StringBuilder structure_prediction = new StringBuilder();
        
        //get the real protein secondary structure as a string
        
        // print(protein, structure_real)
        
        //get the protein secondary stucture prediction as a string
        //i : range(len(protein))
        //int count = 0;
        for(int i = 0; i < protein.length(); ++i) {
        	char ch = protein.charAt(i);
        	Double[] values = self_info.get(ch);
        	
        	Pair<Double, Character> helix;
        	Pair<Double, Character> sheet;
        	Pair<Double, Character> coil;
        	
        	if(values == null) { //new code by Benjy to prevent a crash
        		helix = new Pair<Double, Character>(0.0, 'H');
                sheet = new Pair<Double, Character>(0.0, 'E');
                coil  = new Pair<Double, Character>(0.0, 'C');
                nullIssue = true;
        	} else {
        		helix = new Pair<Double, Character>(self_info.get(ch)[0], 'H');
                sheet = new Pair<Double, Character>(self_info.get(ch)[1], 'E');
                coil  = new Pair<Double, Character>(self_info.get(ch)[2], 'C');
        	}
        	
            //boolean flag = protein.charAt(i+1) == 'X';
            
            for(int m : NEIGHBOR_INDECES) {
                if(i+m >= 0 && i+m < protein.length()){
                	LabeledHash<Integer, LabeledHash<Character, Double[]>> tmp0 = pair_info.get(ch);
                	
                	if(tmp0 == null) { 
                		nullIssue = true;
                		continue;
                	}
                	LabeledHash<Character, Double[]> tmp1 = tmp0.get(m);
                	
                	/*if(flag) {
                		qp("\t**"+protein.charAt(i+m)+"**"+count);
                		++count;
                	}*/
                	
                	Double[] tmp2 = tmp1.get(protein.charAt(i+m));
                	if(tmp2 != null) {
	                    helix.x += tmp2[0];
	                    sheet.x += tmp2[1];
	                    coil.x  += tmp2[2];
                	} else {
                		nullIssue = true;
                	}
                }
            }
            
            //qp(helix.x + ": " + sheet.x + ": " + coil.x);
            
            if(max(helix.x, sheet.x, coil.x) == helix.x) {
                structure_prediction.append(helix.y);
            } else if(max(helix.x, sheet.x, coil.x) == sheet.x) {
                structure_prediction.append(sheet.y);
            } else if(max(helix.x, sheet.x, coil.x) == coil.x) {
                structure_prediction.append(coil.y);
            }
        }
        
        
        return structure_prediction.toString();
    }
    
    public boolean nullPointerIssue() { return nullIssue; }
    
    /**
     * Ligand causes the whole thing to not get assigned
     * 
     * 
     * @param args
     */
    public static void main(String[] args) {    	
    	ChainID id = new ChainID();
		id.setProtein("1HQS");
		id.setChain("A");
		
		PDB_Downloader.quickDownload(id);
		PDB_HashReader reader = new PDB_HashReader(id);
		reader.readPDB();
		reader.applyDSSP();
		ProteinChain chain1 = reader.toChain();
		ProteinChain chain2 = chain1.clone();
		
		chain2.add(22, new AminoAcid(AminoType.Ornithine));
		Vkbat.assign(chain2, VKPred.gor3, true);
		
		AminoAcid aa = (AminoAcid) chain2.get(0);
		qp(aa.getVKPrediction(VKPred.gor3));
		
		
		//chain1.add(22, new AminoAcid(AminoType.Ornithine));
		//Vkbat.assign(chain1, VKPred.gor3, true);
		//aa = (AminoAcid) chain1.get(0);
		//qp(aa.getVKPrediction(VKPred.gor3));
    }
}
