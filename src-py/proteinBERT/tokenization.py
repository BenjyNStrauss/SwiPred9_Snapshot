from collections.abc import Iterable

'''
Modified by Benjamin Strauss to handle the addition of secondary structure data
'''

ALL_AAS = 'A5,A9,A6,A2,A1,A3,A4,A7,A8,A0,!5,!9,!6,!2,!1,!3,!4,!7,!8,!0,C5,C9,C6,C2,C1,C3,C4,C7,C8,C0,D5,D9,D6,D2,D1,D3,D4,D7,D8,D0,E5,E9,E6,E2,E1,E3,E4,E7,E8,E0,F5,F9,F6,F2,F1,F3,F4,F7,F8,F0,G5,G9,G6,G2,G1,G3,G4,G7,G8,G0,H5,H9,H6,H2,H1,H3,H4,H7,H8,H0,I5,I9,I6,I2,I1,I3,I4,I7,I8,I0,Þ5,Þ9,Þ6,Þ2,Þ1,Þ3,Þ4,Þ7,Þ8,Þ0,K5,K9,K6,K2,K1,K3,K4,K7,K8,K0,L5,L9,L6,L2,L1,L3,L4,L7,L8,L0,M5,M9,M6,M2,M1,M3,M4,M7,M8,M0,N5,N9,N6,N2,N1,N3,N4,N7,N8,N0,O5,O9,O6,O2,O1,O3,O4,O7,O8,O0,P5,P9,P6,P2,P1,P3,P4,P7,P8,P0,Q5,Q9,Q6,Q2,Q1,Q3,Q4,Q7,Q8,Q0,R5,R9,R6,R2,R1,R3,R4,R7,R8,R0,S5,S9,S6,S2,S1,S3,S4,S7,S8,S0,T5,T9,T6,T2,T1,T3,T4,T7,T8,T0,U5,U9,U6,U2,U1,U3,U4,U7,U8,U0,V5,V9,V6,V2,V1,V3,V4,V7,V8,V0,W5,W9,W6,W2,W1,W3,W4,W7,W8,W0,X5,X9,X6,X2,X1,X3,X4,X7,X8,X0,Y5,Y9,Y6,Y2,Y1,Y3,Y4,Y7,Y8,Y0,µ5,µ9,µ6,µ2,µ1,µ3,µ4,µ7,µ8,µ0,A,!,C,D,E,F,G,H,I,Þ,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,µ'

ADDITIONAL_TOKENS = ['<OTHER>', '<START>', '<END>', '<PAD>']

# Each sequence is added <START> and <END> tokens
ADDED_TOKENS_PER_SEQ = 2

n_aas = len(ALL_AAS.split(","))

aa_to_token_index = {aa: i for i, aa in enumerate(ALL_AAS.split(","))}

additional_token_to_index = {token: i + n_aas for i, token in enumerate(ADDITIONAL_TOKENS)}
token_to_index = {**aa_to_token_index, **additional_token_to_index}
index_to_token = {index: token for token, index in token_to_index.items()}
n_tokens = len(token_to_index)

def tokenize_seq(seq):
    #print("tokenize_seq() called on "+ seq);
    other_token_index = additional_token_to_index['<OTHER>']
    seq = seq.replace("- ", ", ")
    pre_tokens = parse_seq(seq).split(", ")
    
    tokenized_seq = [aa_to_token_index.get(aa, other_token_index) for aa in pre_tokens]
    
    #validateTokens(tokenized_seq)
    
    return [additional_token_to_index['<START>']] + tokenized_seq + [additional_token_to_index['<END>']]
            
def parse_seq(seq):
    if isinstance(seq, str):
        return seq
    elif isinstance(seq, bytes):
        return seq.decode('utf8')
    else:
        raise TypeError('Unexpected sequence type: %s' % type(seq))
    
def validateTokens(tokens):
    for token in tokens:
        if token < 0:
            print("Bad token " +str(token))

def assertNoNegatives(data):
    if __hasNegValueInternal__(data):
        raise ValueError("Negative Value found in "+str(data))

def __hasNegValueInternal__(data):
    if isinstance(data, Iterable):
        for datapoint in data:
            if __hasNegValueInternal__(datapoint):
                return True
        return False
    elif data < 0:
        return True

def sp_debug_log(string):
    print(string)
    with open("debug-spbert.txt", "a") as file:
        file.write(string)
                
if __name__ == '__main__':
    #os.chdir("../..")
    import proteinBERT.tokenization_original as to
    
    print(to.n_tokens)
    print(n_tokens)
    
    print(to.additional_token_to_index)
    print(additional_token_to_index)
    
    print(to.token_to_index)
    print(token_to_index)
    
    print(to.index_to_token)
    print(index_to_token)
    
    print(to.aa_to_token_index)
    print(aa_to_token_index)
    
    