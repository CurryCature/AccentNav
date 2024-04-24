import os
import pathlib

import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import tensorflow as tf
import tensorflow_io as tfio

from tensorflow.keras import layers
from tensorflow.keras import models
from IPython import display

#SEED?????

#directory as Path object
DATASET_PATH = 'data/samples'
directory = pathlib.Path(DATASET_PATH)

#get dataset from directory, split into train and validation files (batch 64 - downsampled to 16kHz)
trainDS, valDS = tf.keras.utils.audio_dataset_from_directory(
    directory=directory,
    batch_size=64,
    validation_split=0.2,
    seed=0,
    output_sequence_length=16000,
    subset='both')

labelNames = np.array(trainDS.class_names)

#remove one axis of the tensor as we have mono recording
def squeeze(audio, labels):
  audio = tf.squeeze(audio, axis=-1)
  return audio, labels

trainDS = trainDS.map(squeeze, tf.data.AUTOTUNE) #remove axis
valDS = valDS.map(squeeze, tf.data.AUTOTUNE)

#get label names from folders


#PROCESSING STEP 1 - PRE Emphasis


#STEP 2 - STFT -> mel spectrum

def spec_to_mel(spectrogram):
    mel = tfio.audio.melscale(spectrogram, rate=16000,  mels=128, fmin=0, fmax=8000)
    return mel

def get_spectrogram(waveform):
  # Convert the waveform to a spectrogram via a STFT.
  spectrogram = tf.signal.stft(
      waveform, frame_length=255, frame_step=128)
  # Obtain the magnitude of the STFT.
  spectrogram = tf.abs(spectrogram)
  # Add a `channels` dimension, so that the spectrogram can be used
  # as image-like input data with convolution layers (which expect
  # shape (`batch_size`, `height`, `width`, `channels`).
  spectrogram = spectrogram[..., tf.newaxis]
  return spec_to_mel(spectrogram)




#Make new database
def make_spec_ds(ds):
  return ds.map(
      map_func=lambda audio,label: (get_spectrogram(audio), label),
      num_parallel_calls=tf.data.AUTOTUNE)

trainSpecDS = make_spec_ds(trainDS)
valSpecDS = make_spec_ds(valDS)

trainSpecDS.element_spec


#Create the model

#Template custom layer:
class  MyLayer(tf.keras.layers.Layer):
  def __init__(self, num_outputs):
    super(MyLayer, self).__init__()
    self.num_outputs = num_outputs

  def build(self, input_shape):
    self.kernel = self.add_weight("kernel",
        shape=[int(input_shape[-1]),
        self.num_outputs])

  def call(self, inputs):
    return tf.matmul(inputs, self.kernel)

    #Layer blocks can also be created in this fashion


model = models.sequential([
    layers.Input(shape = input_shape),
    layers.Resizing(32,32),
    norm_layer,
])