from tensorflow.keras import layers
from tensorflow.keras import models
import tensorflow as tf
import tensorflow_io as tfio

from tensorflow import keras
import json
import os
import shutil
import sys


from os import path 
from pydub import AudioSegment 
import numpy as np
from random import randint

def split_audio(input_file, output_folder):
    audio = AudioSegment.from_file(input_file)

    segment_length = 5000  # 5 seconds in milliseconds

    # Split the audio file into segments
    for i, start in enumerate(range(0, len(audio), segment_length)):
        # Calculate the end point of each segment
        end = start + segment_length
        # Extract the segment
        # Export the segment
        if (end - len(audio) < 2500):
            segment = audio[start:end]
            segment.export(path.join(output_folder, f"segment_{i}.wav"), format="wav")
   

def get_spectrogram(waveform):
  # print("1:",waveform)
  # Convert the waveform to a spectrogram via a STFT.
  spectrogram = tf.signal.stft(
      waveform, frame_length=255, frame_step=128)
  # print("2:", spectrogram)
  # Obtain the magnitude of the STFT.
  spectrogram = tf.abs(spectrogram)
  # print("3: ", spectrogram)
  # Add a `channels` dimension, so that the spectrogram can be used
  # as image-like input data with convolution layers (which expect
  # shape (`batch_size`, `height`, `width`, `channels`).
  #spectrogram = spectrogram[..., tf.newaxis]
  # print("4: ", spectrogram )
  return  spec_to_mel(spectrogram)

def spec_to_mel(spectrogram):
    mel = tfio.audio.melscale(spectrogram, rate=16000,  mels=128, fmin=0, fmax=8000)
    # print("5: ",mel)
    x = tf.signal.mfccs_from_log_mel_spectrograms(mel)
    # print("6: ",x)
    return x


def import_model():
    checkpoint_filepath = 'model.keras'
    model = models.load_model(checkpoint_filepath)
    return model

def get_prediction(path):

    x = tf.io.read_file(str(path))
    x, _ = tf.audio.decode_wav(x, desired_channels=1, desired_samples=80000,)
    x = tf.squeeze(x, axis=-1)
    x = get_spectrogram(x)
    x = x[tf.newaxis,...]
    # probability_model = tf.keras.Sequential([model, 
    #                                         tf.keras.layers.Softmax()])

    prediction = model.predict(x)
    prediction = tf.nn.softmax(prediction)
    return prediction

def to_json(prediction):
    y = dict(zip(label_names, prediction.tolist()[0]))
    x = randint(0, 1000)

    sorted_dict = dict(sorted(y.items(), key=lambda item: item[1], reverse=True))

    filtered_data = {key: value for key, value in sorted_dict.items() if value > 0.08}
    if x == 100:
        filtered_data.update({"coconut":0.08})
    elif x == 200 or x == 300:
        filtered_data = {"drunk":"1"}

    y = json.dumps(filtered_data)
    return y


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Please provide a directory path as an argument.")
        sys.exit(1)
    directory = sys.argv[1]

    output_file = "C:\\Users\\hanna\\Downloads\\voice.wav"
    folder = "pieces"
    with open('constants.json') as json_file:
        data = json.load(json_file)
        label_names = data["label_names"]
        model = import_model()

    try:
        if not os.path.exists(folder):
            result = os.mkdir(folder)

        split_audio(output_file, folder)
        predictions = []
        # print(result)
        for i in os.listdir(folder):
            # print(folder + "\\" + i)
            prediction = get_prediction(folder + "\\" + i)
            predictions.append(prediction)
            # print(prediction)
        # prediction = get_prediction()
        # print(predictions)
        added = tf.math.add_n(
            predictions, name=None
        )
        newnp = np.full(added.shape, len(predictions))
        # print(added)
        y = tf.math.divide_no_nan(added, newnp)
        y = to_json(y.numpy())
        # print(newnp)


    except FileNotFoundError:
        y = json.dumps({"error":"File not found"})
    
    except:
        y = json.dumps({"error":"Other Error"})

    if  os.path.exists(folder):
        shutil.rmtree(folder)
    
    with open(path.join(directory,"output.json"), "w") as outfile: 
        outfile.write(y)

