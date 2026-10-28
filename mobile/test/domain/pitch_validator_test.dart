import 'package:flutter_test/flutter_test.dart';
import 'package:music_app/domain/attempt_outcome.dart';
import 'package:music_app/domain/note.dart';
import 'package:music_app/domain/pitch_validator.dart';

void main() {
  const validator = PitchValidator();
  final a4 = Note.parse('A4');

  test('exact pitch is correct', () {
    expect(validator.evaluate(a4, 440), AttemptOutcome.correct);
  });

  test('about 20 cents sharp is still correct', () {
    expect(validator.evaluate(a4, 445), AttemptOutcome.correct);
  });

  test('about 47 cents sharp is incorrect', () {
    expect(validator.evaluate(a4, 452), AttemptOutcome.incorrect);
  });

  test('right note, wrong octave is incorrect', () {
    expect(validator.evaluate(a4, 880), AttemptOutcome.incorrect);
  });

  test('no pitch detected is unrecognized, never correct', () {
    expect(validator.evaluate(a4, null), AttemptOutcome.unrecognized);
    expect(validator.evaluate(a4, 0), AttemptOutcome.unrecognized);
  });

  test('tolerance is configurable', () {
    const strict = PitchValidator(toleranceCents: 10);
    expect(strict.evaluate(a4, 445), AttemptOutcome.incorrect);
  });
}
