import 'dart:math' as math;

/// A musical note in scientific pitch notation, e.g. A4 (440 Hz) or C#3.
///
/// Plain Dart on purpose: nothing in `domain/` may import Flutter or touch the
/// microphone, so all of it can be unit tested without a phone.
class Note {
  const Note(this.midi) : assert(midi >= 0 && midi <= 127);

  /// Parses text like "A4", "C#3" or "E2".
  factory Note.parse(String text) {
    final match = RegExp(r'^([A-G]#?)(\d)$').firstMatch(text.trim());
    if (match == null) {
      throw FormatException('Not a note', text);
    }
    final index = names.indexOf(match.group(1)!);
    if (index < 0) {
      throw FormatException('Not a note', text);
    }
    final octave = int.parse(match.group(2)!);
    return Note((octave + 1) * 12 + index);
  }

  /// The note whose exact pitch is closest to [hz].
  factory Note.nearest(double hz) {
    if (hz <= 0) {
      throw ArgumentError.value(hz, 'hz', 'must be greater than 0');
    }
    final semitonesFromA4 = 12 * _log2(hz / a4Hz);
    return Note(_a4Midi + semitonesFromA4.round());
  }

  static const List<String> names = [
    'C',
    'C#',
    'D',
    'D#',
    'E',
    'F',
    'F#',
    'G',
    'G#',
    'A',
    'A#',
    'B',
  ];

  /// Reference tuning: A4 = 440 Hz.
  static const double a4Hz = 440;
  static const int _a4Midi = 69;

  /// MIDI note number: 60 = C4, 69 = A4.
  final int midi;

  String get name => names[midi % 12];

  int get octave => midi ~/ 12 - 1;

  /// Exact frequency of this note in Hz.
  double get frequencyHz => a4Hz * math.pow(2, (midi - _a4Midi) / 12);

  /// How far [hz] is from this note, in cents (100 cents = 1 semitone).
  /// Positive means sharp, negative means flat.
  double centsFrom(double hz) => 1200 * _log2(hz / frequencyHz);

  @override
  bool operator ==(Object other) => other is Note && other.midi == midi;

  @override
  int get hashCode => midi.hashCode;

  @override
  String toString() => '$name$octave';
}

double _log2(double x) => math.log(x) / math.ln2;
