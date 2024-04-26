import pathlib
import tensorflow as tf
import tensorflow_io as tfio
import librosa

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
  print(sample,"waveform")
  noise_ds.shuffle(buffer_size=noise_ds.cardinality())
  print
  s = 0.5
  for tensor,c in noise_ds.take(1):
    s = tensor
  s = tf.math.multiply(s,0.1)
  new = tf.math.add(sample, s)
  return new

##########################################################################################
#change pitch variation for mapping (doesn't work cause mapping makes the tensors symbolic)

def change_pitch_low(waveform):
  steps = 2
  print(waveform, "waveform")
  vec = waveform.make_ndarray()
  shifted = librosa.effects.pitch_shift(vec,sr = 16000,n_steps=steps,bins_per_octave=12)
  shifted = tf.convert_to_tensor(shifted, dtype=tf.float32)
  return shifted

def change_pitch_high(waveform):
  steps = -2
  print(waveform, "waveform")
  vec = waveform.make_ndarray()
  shifted = librosa.effects.pitch_shift(vec,sr = 16000,n_steps=steps,bins_per_octave=12)
  shifted = tf.convert_to_tensor(shifted, dtype=tf.float32)
  return shifted

def pitchvar_mapping(ds):
  hds = ds.map(
      map_func=lambda audio,label: (change_pitch_high(audio), label),
      num_parallel_calls=tf.data.AUTOTUNE) 
  lds = ds.map(
      map_func=lambda audio,label: (change_pitch_low(audio), label),
      num_parallel_calls=tf.data.AUTOTUNE) 
  print(hds.cardinality())
  print(lds.cardinality())
  nds = ds.concatenate(hds)
  return nds.concatenate(lds)

########################## END OF PITCHVAR MAP ##########################################

def pitchvar(ds):
  #working dataset
  wds = ds
  steps = 3
  for s,c in wds:
    #empty lists to collect samples in one batch
    tensorlisth = []
    tensorlistl = []
    for i in range(int(tf.size(s)/80000)): 
      vec = s[i].numpy()
      shifted_high = librosa.effects.pitch_shift(vec,sr = 16000,n_steps=steps,bins_per_octave=12)
      shifted_low =  librosa.effects.pitch_shift(vec,sr = 16000,n_steps=-steps, bins_per_octave=12)
      shifted_high = tf.convert_to_tensor([shifted_high], dtype=tf.float32)
      shifted_low = tf.convert_to_tensor([shifted_low], dtype=tf.float32)
      tensorlisth.append(shifted_high)
      tensorlistl.append(shifted_low)
    #make tensors from batch list
    new_tensor_h =  tf.concat(tensorlisth, axis=0)
    new_tensor_l =  tf.concat(tensorlistl, axis=0)
    #make dataset out of batch list to concatanate
    hds = tf.data.Dataset.from_tensors((new_tensor_h,c))
    lds = tf.data.Dataset.from_tensors((new_tensor_l,c))
    nds = hds.concatenate(lds)
    wds = wds.concatenate(nds)
  return wds                      


def printel(ds):
  for e in ds.take(1):
    print(e)

def tds(ds,pitchvariation,noise):
  #squeeze because of mono sound
  new_ds = ds.map(squeeze, tf.data.AUTOTUNE)
  #add random noise to each sound (still 1D tensor)
  printel(new_ds)
  print("test shape")
  if pitchvariation:
    new_ds = pitchvar(new_ds)
  #printel(ds)
  if noise:
    new_ds = new_ds.map(
      map_func=lambda audio,label: (add_noise(audio), label),
      num_parallel_calls=tf.data.AUTOTUNE)  
  #get spectrogram for every file (commented out for testing - necessary for training)
  '''new_ds = new_ds.map(
    map_func=lambda audio,label: (get_spectrogram(audio), label),
    num_parallel_calls=tf.data.AUTOTUNE)'''
  return new_ds

def get_spectrogram(waveform):
  print(type(waveform),"WAVEFORM TYPE")
  # Convert the waveform to a spectrogram via a STFT.
  spectrogram = tf.signal.stft(
    waveform, frame_length=255, frame_step=128)
  # Obtain the magnitude of the STFT.
  spectrogram = tf.abs(spectrogram)
  #transform into melscale
  mel = tfio.audio.melscale(spectrogram, rate=16000,  mels=128, fmin=0, fmax=8000)
  return  mel




  