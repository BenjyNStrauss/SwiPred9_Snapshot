'''
Created on Mar 7, 2024

@author: Benjamin Strauss
see: https://github.com/nadavbra/protein_bert/blob/master/ProteinBERT%20demo.ipynb
'''

import os
import gc

import pandas as pd
#from IPython.display import display

import keras

from sklearn.model_selection import train_test_split

from proteinBERT import OutputType, OutputSpec, FinetuningModelGenerator, load_pretrained_model, finetune, evaluate_by_len, log
from proteinBERT.conv_and_global_attention_model import get_model_with_hidden_layers_as_outputs
from proteinBERT.shared_utils.util import start_log
from proteinBERT.visualizer import main
from proteinBERT.pretrain_proteinbert_sp import SAVE_PATH

MODEL_EPOCHS = 196
#FINE_TUNING = 4

os.chdir("../..")
#swipredbert_model_dir = "files/proteinbert-models/"+REDUNDANCY

keys = [ "p", "ps8", "ps3", "s8", "s3", "pbert" ]

BENCHMARKS = [
    # name, output_type
    ('species',                      OutputType(False, 'categorical')),
    ('family',                       OutputType(False, 'categorical')),
    ('superfamily',                  OutputType(False, 'categorical')),
]

settings = {
    'max_dataset_size': None,
    'max_epochs_per_stage': 20,  #original 40
    'seq_len': 2048, # #original 512
    'batch_size': 80, #original 32
    'final_epoch_seq_len': 2048, # #original 1024
    'initial_lr_with_frozen_pretrained_layers': 1e-03, #original 1e-02
    'initial_lr_with_all_layers': 1e-04,
    'final_epoch_lr': 1e-06, # original1e-05
    'dropout_rate': 0.25, #original 0.5
    'training_callbacks': [
        keras.callbacks.ReduceLROnPlateau(patience = 1, factor = 0.25, min_lr = 1e-05, verbose = 1),
        keras.callbacks.EarlyStopping(patience = 2, restore_best_weights = True),
    ],
}

def benchmarksDir(redundancy, database):
    return 'input/swipredbert/benchmarks/' + redundancy + "-" + database + "/"

####### Uncomment for debug mode
# settings['max_dataset_size'] = 500
# settings['max_epochs_per_stage'] = 1

def run_benchmark(benchmark_name, benchmarks_dir, pretraining_model_generator, input_encoder, pretraining_model_manipulation_function = None, tokenType: str = ""):
    
    log(f'========== {tokenType} ========== {benchmark_name} ==========')  
    
    output_type = get_benchmark_output_type(benchmark_name)
    log('Output type: %s' % output_type)
    
    train_set, valid_set, test_set = load_benchmark_dataset(benchmark_name, benchmarks_dir, tokenType)        
    log(f'{len(train_set)} training set records, {len(valid_set)} validation set records, {len(test_set)} test set records.')
    
    if settings['max_dataset_size'] is not None:
        log('Limiting the training, validation and test sets to %d records each.' % settings['max_dataset_size'])
        train_set = train_set.sample(min(settings['max_dataset_size'], len(train_set)), random_state = 0)
        valid_set = valid_set.sample(min(settings['max_dataset_size'], len(valid_set)), random_state = 0)
        test_set  = test_set.sample(min(settings['max_dataset_size'], len(test_set)), random_state = 0)
    
    if output_type.is_seq or output_type.is_categorical:
        train_set['label'] = train_set['label'].astype(str)
        valid_set['label'] = valid_set['label'].astype(str)
        test_set['label']  =  test_set['label'].astype(str)
    else:
        train_set['label'] = train_set['label'].astype(float)
        valid_set['label'] = valid_set['label'].astype(float)
        test_set['label']  =  test_set['label'].astype(float)
        
    if output_type.is_categorical:
        
        if output_type.is_seq:
            unique_labels = sorted(set.union(*train_set['label'].apply(set)) | set.union(*valid_set['label'].apply(set)) | \
                    set.union(*test_set['label'].apply(set)))
        else:
            unique_labels = sorted(set(train_set['label'].unique()) | set(valid_set['label'].unique()) | set(test_set['label'].unique()))
            
        log('%d unique labels.' % len(unique_labels))
    elif output_type.is_binary:
        unique_labels = [0, 1]
    else:
        unique_labels = None
        
    output_spec = OutputSpec(output_type, unique_labels)
    model_generator = FinetuningModelGenerator(pretraining_model_generator, output_spec, pretraining_model_manipulation_function = \
            pretraining_model_manipulation_function, dropout_rate = settings['dropout_rate'])
    
    log("Fine-Tuning...")
    finetune(model_generator, input_encoder, output_spec, train_set['seq'], train_set['label'], valid_set['seq'], valid_set['label'], \
            seq_len = settings['seq_len'], batch_size = settings['batch_size'], max_epochs_per_stage = settings['max_epochs_per_stage'], \
            lr = settings['initial_lr_with_all_layers'], begin_with_frozen_pretrained_layers = True, lr_with_frozen_pretrained_layers = \
            settings['initial_lr_with_frozen_pretrained_layers'], n_final_epochs = 1, final_seq_len = settings['final_epoch_seq_len'], \
            final_lr = settings['final_epoch_lr'], callbacks = settings['training_callbacks'])
    
    log("Fine-Tuning Completed!")
    
    for dataset_name, dataset in [('Training-set', train_set), ('Validation-set', valid_set), ('Test-set', test_set)]:
        
        log('*** %s performance: ***' % dataset_name)
        results, confusion_matrix = evaluate_by_len(model_generator, input_encoder, output_spec, dataset['seq'], dataset['label'], \
                start_seq_len = settings['seq_len'], start_batch_size = settings['batch_size'])
        
        with pd.option_context('display.max_rows', None, 'display.max_columns', None):
            log(results)
            if 'AUC' in results.keys():
                log("results['AUC']: "+results['AUC'])
            if 'Spearman\'s rank correlation' in results.keys():
                log("results['Spearman']: "+results['Spearman\'s rank correlation'])
        
        #Confusion Matrix is not useful...
        #if confusion_matrix is not None:
            #with pd.option_context('display.max_rows', 2000, 'display.max_columns', 2000):
                #log('Confusion matrix:')
                #log(confusion_matrix)
                #display(confusion_matrix)
                
    return model_generator

def load_benchmark_dataset(benchmark_name, benchmarks_dir, tokenType = ""):
    
    train_set_file_path = os.path.join(benchmarks_dir, tokenType+'%s.train.csv' % benchmark_name)
    valid_set_file_path = os.path.join(benchmarks_dir, tokenType+'%s.valid.csv' % benchmark_name)
    test__set_file_path = os.path.join(benchmarks_dir, tokenType+'%s.test.csv'  % benchmark_name)
    
    train_set_raw = pd.read_csv(train_set_file_path)
    test_set_raw = pd.read_csv(test__set_file_path)
    
    train_set = train_set_raw.dropna().drop_duplicates()
    test_set = test_set_raw.dropna().drop_duplicates()
          
    if os.path.exists(valid_set_file_path):
        valid_set = pd.read_csv(valid_set_file_path).dropna().drop_duplicates()
    else:
        log(f'Validation set {valid_set_file_path} missing. Splitting training set instead.')
        train_set, valid_set = train_test_split(train_set, stratify = train_set['label'], test_size = 0.1, random_state = 0)
    
    return train_set, valid_set, test_set

def get_benchmark_output_type(benchmark_name):
    for name, output_type in BENCHMARKS:
        if name == benchmark_name:
            return output_type

if __name__ == '__main__':
    for database in ["pdb", "unp"]:
        for redundancy in ["nr", "wr"]:
            start_msg = "Starting: "+database+"-"+redundancy
            log(start_msg)
            print(start_msg)
            
            start_log(os.getcwd(), "pbert-log-"+redundancy+"-"+database)
            
            log("****Hyperparameters:****")
            
            log("Redundancy:           "+redundancy)
            log("Epochs:               "+str(MODEL_EPOCHS))
            log("max_epochs_per_stage: "+str(settings["max_epochs_per_stage"]))
            log("seq_len:              "+str(settings["seq_len"]))
            log("batch_size:           "+str(settings["batch_size"]))
            log("dropout_rate:         "+str(settings["dropout_rate"]))
            log("final_epoch_seq_len:  "+str(settings["final_epoch_seq_len"]))
            
            swipredbert_model_dir = SAVE_PATH+redundancy
            
            for key in keys:
                log("**** Using Key: "+key+" ****")
                
                model_dump_file_name = key+"/epoch_"+str(MODEL_EPOCHS)
                
                if database == "pdb":
                    model_dump_file_name += "_dssp.pkl"
                else:
                    model_dump_file_name += ".pkl"
                
                #load pretrained model
                pretrained_model_generator, input_encoder = load_pretrained_model(
                    local_model_dump_dir = swipredbert_model_dir,
                    local_model_dump_file_name = model_dump_file_name)
                
                #run the benchmark
                for benchmark_name, _ in BENCHMARKS:
                    model_gen = run_benchmark(benchmark_name, benchmarksDir(redundancy, database), pretrained_model_generator,
                            input_encoder, pretraining_model_manipulation_function = \
                            get_model_with_hidden_layers_as_outputs, tokenType=key+"token-")
                    
                    test_set_path = benchmarksDir(redundancy, database) + key + "token-" + benchmark_name + ".test.csv"
                    main(pretrained_model_generator, test_set_path, model_gen, swipredbert_model_dir, model_dump_file_name)
                    
                    gc.collect()
                    
                log("**** Finished with key: "+key+" ****")
                
            log('Completed.')