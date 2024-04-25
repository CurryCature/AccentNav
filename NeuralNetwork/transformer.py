import pathlib
import tensorflow as tf
import tensorflow_io as tfio

#import noise sounds
NOISES_PATH = 'noises'
directory1 = pathlib.Path(NOISES_PATH)

noise_ds = tf.keras.utils.audio_dataset_from_directory(
  directory=directory1,
  batch_size=1,
  validation_split=0,
  output_sequence_length=80000
)



def squeeze(audio, labels):
  audio = tf.squeeze(audio, axis=-1)
  return audio, labels

#squeeze because we have mono sound
noise_ds = noise_ds.map(squeeze, tf.data.AUTOTUNE)

def add_noise(sample):
  noise_ds.shuffle(buffer_size=noise_ds.cardinality())
  print
  s = 0.5
  for tensor,c in noise_ds.take(1):
    s = tensor
  s = tf.math.multiply(s,0.1)
  new = tf.math.add(sample, s)
  return new

def printel(ds):
  for e in ds.take(1):
    print(e)

def tds(ds):
  #squeeze because of mono sound
  ds = ds.map(squeeze, tf.data.AUTOTUNE)
  #add random noise to each sound (still 1D tensor)
  new_ds = ds.map(
    map_func=lambda audio,label: (add_noise(audio), label),
    num_parallel_calls=tf.data.AUTOTUNE)  
  #get spectrogram for every file
  new_ds = ds.map(
    map_func=lambda audio,label: (get_spectrogram(audio), label),
    num_parallel_calls=tf.data.AUTOTUNE)
  return new_ds

def get_spectrogram(waveform):
  # Convert the waveform to a spectrogram via a STFT.
  spectrogram = tf.signal.stft(
    waveform, frame_length=255, frame_step=128)
  # Obtain the magnitude of the STFT.
  spectrogram = tf.abs(spectrogram)
  #transform into melscale
  mel = tfio.audio.melscale(spectrogram, rate=16000,  mels=128, fmin=0, fmax=8000)
  return  mel




  