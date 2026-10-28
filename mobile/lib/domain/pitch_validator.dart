import 'package:music_app/domain/attempt_outcome.dart';
import 'package:music_app/domain/note.dart';

/// Decides whether a detected pitch matches the requested note.
class PitchValidator {
  const PitchValidator({this.toleranceCents = defaultToleranceCents});

  /// Starting tolerance from the proposal; to be tuned through testing.
  static const double defaultToleranceCents = 25;

  final double toleranceCents;

  /// [detectedHz] is null when the recognizer found no clear pitch.
  AttemptOutcome evaluate(Note requested, double? detectedHz) {
    if (detectedHz == null || detectedHz <= 0) {
      return AttemptOutcome.unrecognized;
    }
    final offset = requested.centsFrom(detectedHz).abs();
    return offset <= toleranceCents
        ? AttemptOutcome.correct
        : AttemptOutcome.incorrect;
  }
}
