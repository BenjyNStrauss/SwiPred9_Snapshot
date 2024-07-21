from math import log, sqrt
from tqdm import tqdm

'''
Created on May 22, 2023

@translator: bns
'''

# Dictionary of the 20 natural amino acids
acids = {'Ala':'A',
         'Arg':'R',
         'Asn':'N',
         'Asp':'D',
         'Cys':'C',
         'Glu':'E',
         'Gln':'Q',
         'Gly':'G',
         'His':'H',
         'Ile':'I',
         'Leu':'L',
         'Lys':'K',
         'Met':'M',
         'Phe':'F',
         'Pro':'P',
         'Ser':'S',
         'Thr':'T',
         'Trp':'W',
         'Tyr':'Y',
         'Val':'V'}

class GOR3(object):
    '''
    Class GOR3 is made to predict the protein secondary structure
    with GOR III algorithm
    '''
    def __init__(self, dataset, PDB_code):
        self.dataset = dataset
        self.PDB_code = PDB_code
        global acids
        print(PDB_code)
        self.acids = list(acids.values())
    
    
    def _calc_fs(self):
        '''
        Made to calculate the total number of acids appearence for each structure.
        Returns dictionary {strusture: number_of_appearence}
        '''
        fs = {}
        helix_fs = 0
        sheet_fs = 0
        coil_fs = 0
        
        for acid in self.dataset:
            if acid[0] != self.PDB_code:
                if acid[4] == 'Helix':
                    helix_fs += 1
                if acid[4] == 'Sheet':
                    sheet_fs += 1
                if acid[4] == 'Coil':
                    coil_fs += 1
        
        fs['Helix'] = helix_fs
        fs['Sheet'] = sheet_fs
        fs['Coil'] = coil_fs
        
        return fs
    
    
    def _calc_self_info(self, seq: str = None):
        '''
        Made to calculate acid self information.
        Returns dictionary {acid: (helix_info, sheet_info, coil_info)}
        '''
        self_info = {}
        acids_list = self.acids[:]
        
        fs = self._calc_fs()
        helix_fs = fs['Helix']
        sheet_fs = fs['Sheet']
        coil_fs = fs['Coil']
        
        #print("acids_list",acids_list)
        
        if seq is not None:
            print("using custom sequence")
            for acid in seq:
#            for acid in self.dataset:
#                if acid[0] == self.PDB_code:
                current = acid
                
                if acid in acids_list:
                    
                    helix_fsr = 0
                    sheet_fsr = 0
                    coil_fsr = 0
                    
                    for other in self.dataset:
                        if other[0] != self.PDB_code:
                            if other[3] == current:
                                if other[4] == 'Helix':
                                    helix_fsr += 1
                                if other[4] == 'Sheet':
                                    sheet_fsr += 1
                                if other[4] == 'Coil':
                                    coil_fsr += 1
                                    
                    #print("*"+str(helix_fs) +":"+ str(sheet_fs) +":"+ str(coil_fs));
                    #print("*"+str(helix_fsr) +":"+ str(sheet_fsr) +":"+ str(coil_fsr));
                   
                    helix_i = log(helix_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr)) + log((sheet_fs + coil_fs) / helix_fs)
                    sheet_i = log(sheet_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr)) + log((helix_fs + coil_fs) / sheet_fs)
                    coil_i = log(coil_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr)) + log((helix_fs + sheet_fs) / coil_fs)
                    
                    #print(">"+str(helix_i) +":"+ str(sheet_i) +":"+ str(coil_i));
                    #exit(3)
                    
                    self_info[current] = (helix_i, sheet_i, coil_i)
                    # print((helix_i, sheet_i, coil_i))
                    
                    acids_list.remove(current)
        else:
            for acid in self.dataset:
                if acid[0] == self.PDB_code:
                    current = acid[3]
                    if acid[3] in acids_list:
                        
                        helix_fsr = 0
                        sheet_fsr = 0
                        coil_fsr = 0
                        
                        for other in self.dataset:
                            if other[0] != self.PDB_code:
                                if other[3] == current:
                                    if other[4] == 'Helix':
                                        helix_fsr += 1
                                    if other[4] == 'Sheet':
                                        sheet_fsr += 1
                                    if other[4] == 'Coil':
                                        coil_fsr += 1
                        
                        helix_i = log(helix_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr)) + log((sheet_fs + coil_fs) / helix_fs)
                        sheet_i = log(sheet_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr)) + log((helix_fs + coil_fs) / sheet_fs)
                        coil_i = log(coil_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr)) + log((helix_fs + sheet_fs) / coil_fs)
                        
                        self_info[current] = (helix_i, sheet_i, coil_i)
                        # print((helix_i, sheet_i, coil_i))
                        
                        acids_list.remove(current)
        
        return self_info
    
    
    def _calc_pair_info(self):
        '''
        Made to calculate acid pair information.
        Returns dictionary {acid:{position:{acid:(helix_info, sheet_info, coil_info)}}}
        '''
        acids_list = self.acids[:]
        pair_info = {}
        
        fs = self._calc_fs()
        helix_fs = fs['Helix']
        sheet_fs = fs['Sheet']
        coil_fs = fs['Coil']
        
        #print("acids_list", acids_list)
        for current_acid in tqdm(acids_list):
            
            '''loop over 20 natural amino acids'''
            acid_dict = {}
            #print("current_acid",current_acid)
            
            #exit(3)
            
            for m in [-8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8]:
                '''loop over 16 neighbors'''
                position_dict = {}
                
                
                for pair_acid in acids_list:
                    '''loop over 20 natural amino acids'''
                        
                    helix_fsr = 0
                    sheet_fsr = 0
                    coil_fsr = 0
                    
                    
                    
                    for i in range(len(self.dataset) - 8):
                        '''loop over all rest recidues'''
                        
                        '''print("--------------"+str(i+m))
                        print(pair_acid+":"+self.dataset[i+m][3])
                        print(self.dataset[i+m][0]+":"+self.dataset[i][0])
                        print(self.dataset[i][0]+":"+self.PDB_code)
                        print(self.dataset[i][3]+":"+current_acid)'''
                        
                        if pair_acid == self.dataset[i+m][3] and self.dataset[i+m][0] == self.dataset[i][0] and self.dataset[i][0] != self.PDB_code and self.dataset[i][3] == current_acid:
                            
                            #print("self.dataset[i+m]",self.dataset[i+m])
                            if self.dataset[i][4] == 'Helix':
                                helix_fsr += 1
                            if self.dataset[i][4] == 'Sheet':
                                sheet_fsr += 1
                            if self.dataset[i][4] == 'Coil':
                                coil_fsr += 1
                    
                            #exit(3)
                    
                    if ((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr) != 0:
                        helix_log_arg = helix_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - helix_fsr)
                    else:
                        helix_log_arg = 1

                    if ((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr) != 0:
                        sheet_log_arg = sheet_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - sheet_fsr)
                    else:
                        sheet_log_arg = 1

                    if ((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr) != 0:
                        coil_log_arg = coil_fsr / ((helix_fsr + sheet_fsr + coil_fsr) - coil_fsr)
                    else:
                        coil_log_arg = 1

                    
                    helix_i = log(helix_log_arg if helix_log_arg > 0 else 1) + log((sheet_fs + coil_fs) / helix_fs)
                    sheet_i = log(sheet_log_arg if sheet_log_arg > 0 else 1) + log((helix_fs + coil_fs) / sheet_fs)
                    coil_i = log(coil_log_arg if coil_log_arg > 0 else 1) + log((helix_fs + sheet_fs) / coil_fs)
                    
                    position_dict[pair_acid] = (helix_i, sheet_i, coil_i)
                
                acid_dict[m] = position_dict
            pair_info[current_acid] = acid_dict
        
        return pair_info
    
    
    def sec_struc_prediction(self, protein = None):
        '''
        Returns a secondary stucture prediction of the protein as a list:
        [PDB_code, the secondary structure prediction, Q3]
        '''
        self_info = self._calc_self_info(seq=protein)
        #print("len(self_info)",len(self_info))
        
        pair_info = self._calc_pair_info()
        #print("len(pair_info)",len(pair_info))
        
        
        protein_list = [entry for entry in self.dataset if entry[0] == self.PDB_code]
        print("protein_list:",protein_list)
        
        
        structure_real = ''
        prediction = [self.PDB_code]
        print("prediction:",prediction)
        structure_prediction = ''
        
        '''get the real protein secondary structure as a string'''
        if protein is None:
            protein = ''
            for acid in protein_list:
                protein += acid[3]
            
            #if acid[4] == 'Helix':
            #    structure_real += 'H'
            #elif acid[4] == 'Sheet':
            #    structure_real += 'E'
            #elif acid[4] == 'Coil':
            #    structure_real += 'C'
        
        print("protein",protein)
        print("structure_real:",structure_real)
        # print(protein, structure_real)
        
        '''get the protein secondary structure prediction as a string'''
        for i in range(len(protein)):
            helix = [self_info[protein[i]][0], 'H'] #
            sheet = [self_info[protein[i]][1], 'E']
            coil = [self_info[protein[i]][2], 'C']
            
            for m in [-8, -7, -6, -5, -4, -3, -2, -1, 1, 2, 3, 4, 5, 6, 7, 8]:
                if i+m > 0 and i+m < len(protein):
                    helix[0] += pair_info[protein[i]][m][protein[i+m]][0]
                    sheet[0] += pair_info[protein[i]][m][protein[i+m]][1]
                    coil[0] += pair_info[protein[i]][m][protein[i+m]][2]
                    
            print(f'**helix={helix}, sheet={sheet}, coil={coil}')
            
            if max(helix[0], sheet[0], coil[0]) == helix[0]:
                structure_prediction += helix[1]
            elif max(helix[0], sheet[0], coil[0]) == sheet[0]:
                structure_prediction += sheet[1]
            elif max(helix[0], sheet[0], coil[0]) == coil[0]:
                structure_prediction += coil[1]
        
        prediction.append(structure_prediction)
        
        '''Q3 calculation'''
        recidues_predicted = 0
        for i in range(len(structure_real)):
            if structure_real[i] == structure_prediction[i]:
                recidues_predicted += 1
        
        q3 = recidues_predicted / len(protein)
        prediction.append(q3)
        
        return prediction
    
def get_protein_list(dataset):
    '''
    Made to get a list of proteins from the dataset
    '''
    protein_raw_list = []
    for acid in dataset:
        protein_raw_list.append(acid[0])
    protein_list = list(set(protein_raw_list))
    return protein_list

def get_dataset(file):
    '''
    get_dataset() is made to get suitable (list) data set
    from starting data sets
    
    dataset[0] — PDB_code
    dataset[1] — PDB_chain_code
    dataset[2] — PDB_seq_code
    dataset[3] — residue_name
    dataset[4] — secondary_structure
    '''
    data_file = open(file, 'r')
    data_set = []
    global acids
    
    for line in data_file:
        new_entry = line.split()
        if new_entry[4] == "Other":
            new_entry[4] = "Coil"
        if new_entry[4] == "Beta":
            new_entry[4] = "Sheet"
            
        if new_entry[3].capitalize() in list(acids.keys()):
            new_entry[3] = acids.get(new_entry[3].capitalize())
            data_set.append(new_entry)
            # print(new_entry)
            
    return data_set

dssp_info = get_dataset("dssp_info.txt")
#print("dssp_info", dssp_info)
dssp_proteins = get_protein_list(dssp_info)
dssp_predictions = []

#gor = GOR3(dssp_info, "5btr")
#print(gor.sec_struc_prediction())
_2i51_seq = "SLAPWRGAIAHALHRNRSLVYARYLQLATVQPNGRPANRTLVFRGFLEDTNQLRFITDTRSAKADQIQQQPWAEICWYFPNTREQFRAGDLTLISSDDSHQDLQPARIAWQELSDAARLQFGWPYPGKPRIKESGAFEPSPPDPIEPVPNFCLLLLDPVQVDHLELRGEPQNRWLYHRNDQQEWSSEAINP"
_5btr_seq = "GSRDNLLFGDEIITNGFHSCESDEEDRASHASSSDWTPRPRIGPYTFVQQHLMIGTDPRTILKDLLPETIPPPELDDMTLWQIVINILSEPPKRKKRKDINTIEDAVKLLQESKKIIVLTGAGVSVSSGIPDFRSRDGIYARLAVDFPDLPDPQAMFDIEYFRKDPRPFFKFAKEIYPGQFQPSLCHKFIALSDKEGKLLRNYTQNIDTLEQVAGIQRIIQCHGSFATASCLICKYKVDCEAVRGDIFNQVVPRCPRCPADEPLAIMKPEIVFFGENLPEQFHRAMKYDKDEVDLLIVIGSSLKVRPVALIPSSIPHEVPQILINREPLPHLHFDVELLGDCDVIINELCHRLGGEYAKLSSNPVKLSEITEQYLFLPPNRYIFHGAEVYSDSEDDV"

#for prot in dssp_proteins[:20]:
    
gor = GOR3(dssp_info, '2i51')
print(gor.sec_struc_prediction(protein = _2i51_seq))


#gor = GOR3(dssp_info, '5btr')
#print(gor.sec_struc_prediction(protein = _5btr_seq))

