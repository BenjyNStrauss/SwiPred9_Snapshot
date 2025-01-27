'''
Created on Mar 15, 2024

@author: nadavbra, Benjamin Strauss
'''

import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

#import keras

from proteinBERT import load_pretrained_model
#from proteinbert.conv_and_global_attention_model import get_model_with_hidden_layers_as_outputs


BENCHMARK_DISPLAY_NAME = 'Signal peptide'

IDEAL_LEN = 80

def calculate_attentions(model, input_encoder, seq, seq_len = None):
    
    from keras import backend as K
    from proteinBERT.tokenization import index_to_token
    
    if seq_len is None:
        seq_len = len(seq) + 2
    
    X = input_encoder.encode_X([seq], seq_len)
    (X_seq,), _ = X
    seq_tokens = list(map(index_to_token.get, X_seq))

    model_inputs = [layer.input for layer in model.layers if 'InputLayer' in str(type(layer))][::-1]
    model_attentions = [layer.calculate_attention(layer.input) for layer in model.layers if 'GlobalAttention' in str(type(layer))]
    invoke_model_attentions = K.function(model_inputs, model_attentions)
    attention_values = invoke_model_attentions(X)
    
    attention_labels = []
    merged_attention_values = []

    for attention_layer_index, attention_layer_values in enumerate(attention_values):
        for head_index, head_values in enumerate(attention_layer_values):
            attention_labels.append('Attention %d - head %d' % (attention_layer_index + 1, head_index + 1))
            merged_attention_values.append(head_values)

    attention_values = np.array(merged_attention_values)
    
    return attention_values, seq_tokens, attention_labels

def plot_attention(attention_values, seq_tokens, attention_labels, ax, cmap = 'Reds', vmin = 0, vmax = None, text_value_threshold = 0.1):

    heatmap = ax.pcolor(attention_values.transpose(), cmap = cmap, vmin = vmin, vmax = vmax)

    ax.set_xticks(np.arange(len(attention_labels)) + 0.5)
    ax.set_xticklabels(attention_labels, rotation = 45, ha = 'right', fontsize = 12)
    ax.set_yticks(np.arange(len(seq_tokens)) + 0.5)
    ax.set_yticklabels(seq_tokens, fontsize = 12)

    for i, row in enumerate(attention_values):
        for j, value in enumerate(row):
            if abs(value) >= text_value_threshold:
                add_plus_sign = attention_values.min() < 0 and value > 0
                plus_sign = '+' if add_plus_sign else ''
                ax.text(i + 0.5, j + 0.5, plus_sign + '%d%%' % (100 * value), color = 'white', ha = 'center', va = 'center', \
                        fontsize = 9, fontweight = 'bold', fontstretch = 'condensed')
                
def main(model_generator, test_set_path, pretrained_model_generator, model_dir, model_name):
    test_set = pd.read_csv(test_set_path)
    chosen_index = ((test_set['seq'].str.len() - IDEAL_LEN).abs()).sort_values().index[0]
    seq = test_set.loc[chosen_index, 'seq']
    label = test_set.loc[chosen_index, 'label']
                    
    seq_len = len(seq) + 2
    #was "pretrained_model_generator, input_encoder = load_pretrained_model(model_dir, model_name)"
    _, input_encoder = load_pretrained_model(model_dir, model_name)
    model = pretrained_model_generator.create_model(seq_len)
    pretrained_attention_values, pretrained_seq_tokens, pretrained_attention_labels = calculate_attentions(model, input_encoder, seq, \
            seq_len = seq_len)
    
    model = model_generator.create_model(seq_len)
    finetuned_attention_values, finetuned_seq_tokens, finetuned_attention_labels = calculate_attentions(model, input_encoder, seq, \
            seq_len = seq_len)
    assert finetuned_seq_tokens == pretrained_seq_tokens
    assert finetuned_attention_labels == pretrained_attention_labels[:len(finetuned_attention_labels)]
    
    fig, axes = plt.subplots(ncols = 4, figsize = (20, 0.2 * seq_len), gridspec_kw = dict(width_ratios = [1, 5, 1, 5]))
    fig.subplots_adjust(wspace = 0.3)
    
    axes[0].barh(np.arange(seq_len), 100 * pretrained_attention_values.sum(axis = 0), color = '#EC7063')
    axes[0].set_ylim((-0.5, seq_len - 0.5))
    axes[0].set_yticks([])
    axes[0].invert_xaxis()
    axes[0].set_xlabel('Total atten. %', fontsize = 14)
    
    vmax = pretrained_attention_values.max()
    plot_attention(pretrained_attention_values, pretrained_seq_tokens, pretrained_attention_labels, axes[1], cmap = 'Reds', vmax = vmax, \
            text_value_threshold = 0.05)
    axes[1].set_title('Only pre-training', fontsize = 16)
    
    axes[2].barh(np.arange(seq_len), 100 * (finetuned_attention_values - pretrained_attention_values).sum(axis = 0), color = '#28B463')
    axes[2].set_ylim((-0.5, seq_len - 0.5))
    axes[2].set_yticks([])
    axes[2].invert_xaxis()
    axes[2].set_xlabel('Total atten. % diff', fontsize = 14)
    
    attention_diff = finetuned_attention_values - pretrained_attention_values[:len(finetuned_attention_labels), :]
    vmax = np.abs(attention_diff).max()
    plot_attention(attention_diff, finetuned_seq_tokens, finetuned_attention_labels, axes[3], cmap = 'PiYG', vmin = -vmax, vmax = vmax, \
            text_value_threshold = 0.03)
    axes[3].set_title('%s fine-tuning' % BENCHMARK_DISPLAY_NAME, fontsize = 16)
    
    print(seq, label)
    
if __name__ == '__main__':
    main()