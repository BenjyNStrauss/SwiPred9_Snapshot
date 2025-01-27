#! /usr/bin/env python3

import argparse

import numpy as np
import pandas as pd
import h5py

from .shared_utils.util import log, get_parser_file_type

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description = 'Designate which records in the h5 dataset are considered test-set.')
    parser.add_argument('--h5-dataset-file', dest = 'h5_dataset_file', metavar = '/path/to/dataset.h5', type = get_parser_file_type(parser, must_exist = True), required = True, \
            help = 'Path to the h5 dataset file.')
    parser.add_argument('--uniprot-ids-file', dest = 'uniprot_ids_file', metavar = '/path/to/uniprot_ids.txt', type = get_parser_file_type(parser, must_exist = True), \
            help = 'Path to a file listing UniProt IDs (one per line) to be considered test-set.')
    parser.add_argument('--set-with-prob', dest = 'set_with_prob', metavar = '0.01', type = float, default = 0, help = 'Set records to be part of the test-set randomly, ' + \
            'with a given probability.')
    parser.add_argument('--silent', dest = 'silent', action = 'store_true', help = 'Run in silent mode.')
    args = parser.parse_args()
    
    if args.uniprot_ids_file is None and args.set_with_prob == 0 and not args.silent:
        log('Warning: Haven\'t set --uniprot-ids-file or --set-with-prob, meaning there won\'t be any test-set records.')
    
    if args.uniprot_ids_file is not None:
        
        test_set_uniprot_ids = set(pd.read_csv(args.uniprot_ids_file, names = ['uniprot_id'], squeeze = True))
        
        if not args.silent:
            log('Loaded %d UniProt IDs to be considered test-set.' % len(test_set_uniprot_ids))
    else:
        test_set_uniprot_ids = set()
        
    np.random.seed(0)

    with h5py.File(args.h5_dataset_file, 'a') as h5f:
        uniprot_ids = [uniprot_id.decode('utf-8') for uniprot_id in h5f['uniprot_ids']]
        test_set_mask = np.array([uniprot_id in test_set_uniprot_ids for uniprot_id in uniprot_ids]) | np.random.choice([True, False], len(uniprot_ids), \
                p = [args.set_with_prob, 1 - args.set_with_prob])
        h5f.create_dataset('test_set_mask', data = test_set_mask, dtype = bool)
        
    if not args.silent:
        log('Done. %d of %d records are test-set.' % (test_set_mask.sum(), len(test_set_mask)))