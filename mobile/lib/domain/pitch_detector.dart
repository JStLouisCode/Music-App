/// Turns a chunk of audio into a pitch.
///
/// This is the "common interface" for recognition: the FFT, autocorrelation or a
/// future ML model each implement it in `infrastructure/`, and the rest of the app
/// only depends on this type, so detectors can be swapped without other changes.
abstract interface class PitchDetector {
  /// Returns the detected frequency in Hz, or null if there is no clear pitch.
  double? detect(List<double> samples, int sampleRate);
}
