#! /usr/bin/env python3

import argparse

from proteinBERT.shared_utils.util import get_parser_file_type
from proteinBERT.uniref_dataset import create_h5_dataset

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description = 'Create an h5 dataset from a UniRef sqlite DB.')
    parser.add_argument('--protein-annotations-sqlite-db-file', dest = 'protein_annotations_sqlite_db_file', metavar = '/path/to/uniref.db', \
            type = get_parser_file_type(parser, must_exist = True), required = True, help = 'The UniRef sqlite DB file.')
    parser.add_argument('--protein-fasta-file', dest = 'protein_fasta_file', metavar = '/path/to/uniref.fasta', type = get_parser_file_type(parser, must_exist = True), \
            required = True, help = 'The FASTA file with the full sequences of the proteins.')
    parser.add_argument('--go-annotations-meta-csv-file', dest = 'go_annotations_meta_csv_file', metavar = '/path/to/go_annotations.csv', \
            type = get_parser_file_type(parser, must_exist = True), required = True, help = 'Path to a CSV file with the count of each GO annotation.')
    parser.add_argument('--output-h5-dataset-file', dest = 'output_h5_dataset_file', metavar = '/path/to/dataset.h5', type = get_parser_file_type(parser), required = True, \
            help = 'Path to the save the output h5 dataset file.')
    parser.add_argument('--min-records-to-keep-annotation', dest = 'min_records_to_keep_annotation', metavar = '100', type = int, default = 100, help = 'The minimal number of ' + \
            'records required to encode an annotaiton (default 100).')
    parser.add_argument('--log-progress-every', dest = 'log_progress_every', metavar = '10000', type = int, default = 10000, help = 'If running in verbose (non-silent) mode, ' + \
            'log the progress of the process in increments of this given number (10000 by default).')
    parser.add_argument('--records-limit', dest = 'records_limit', metavar = 'n', type = int, default = None, help = 'Limit the number of loaded records. By default will ' + \
            'load all records')
    parser.add_argument('--save-chunk-size', dest = 'save_chunk_size', metavar = '10000', type = int, default = 10000, help = 'The number of records to save per chunk.')
    parser.add_argument('--no-shuffle', dest = 'no_shuffle', action = 'store_true', help = 'By default, records are shuffled (to ensure a heterogenous dataset that trains ' + \
            'better). Provide this flag to disable that.')
    parser.add_argument('--silent', dest = 'silent', action = 'store_true', help = 'Run in silent mode.')
    args = parser.parse_args()
    
    create_h5_dataset(args.protein_annotations_sqlite_db_file, args.protein_fasta_file, args.go_annotations_meta_csv_file, args.output_h5_dataset_file, shuffle = \
            not args.no_shuffle, min_records_to_keep_annotation = args.min_records_to_keep_annotation, records_limit = args.records_limit, save_chunk_size = args.save_chunk_size, \
            verbose = not args.silent, log_progress_every = args.log_progress_every)