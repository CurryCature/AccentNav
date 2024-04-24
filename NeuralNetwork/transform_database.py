def squeeze(audio, labels):
  audio = tf.squeeze(audio, axis=-1)
  return audio, labels

trainDS = trainDS.map(squeeze, tf.data.AUTOTUNE) #remove axis
valDS = valDS.map(squeeze, tf.data.AUTOTUNE)
noise_ds = noise_ds.map(squeeze, tf.data.AUTOTUNE)

def get_spectrogram(waveform):
 
  spectrogram = tf.signal.stft(
      waveform, frame_length=255, frame_step=128)
  spectrogram = tf.abs(spectrogram)
  mel = spec_to_mel(spectrogram)
  return  mel

  def spec_to_mel(spectrogram):
    mel = tfio.audio.melscale(spectrogram, rate=16000,  mels=128, fmin=0, fmax=8000)
    return mel

def add_noise(sample):
  noise_ds.shuffle(buffer_size=noise_ds.cardinality())
  print
  s = 0.5
  for tensor,c in noise_ds.take(1):
    s = tensor
  s = tf.math.multiply(s,0.1)
  new = tf.math.add(sample, s)
  return new

def transform_dataset(ds):
    new_ds = ds.map(squeeze, tf.data.AUTOTUNE)
    new_ds = new_ds.map(
        map_func=lambda audio,label: (add_noise(audio), label),
        num_parallel_calls=tf.data.AUTOTUNE)
    new_ds = ds.map(
        map_func=lambda audio,label: (get_spectrogram(audio), label),
        num_parallel_calls=tf.data.AUTOTUNE)
  return new_ds