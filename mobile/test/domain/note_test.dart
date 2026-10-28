import 'package:flutter_test/flutter_test.dart';
import 'package:music_app/domain/note.dart';

void main() {
  group('Note', () {
    test('A4 is 440 Hz', () {
      expect(Note.parse('A4').frequencyHz, closeTo(440, 0.001));
    });

    test('middle C is about 261.63 Hz', () {
      expect(Note.parse('C4').frequencyHz, closeTo(261.63, 0.01));
    });

    test('parse and toString round-trip', () {
      for (final text in ['E2', 'C#3', 'A4', 'B5']) {
        expect(Note.parse(text).toString(), text);
      }
    });

    test('rejects text that is not a note', () {
      expect(() => Note.parse('H2'), throwsFormatException);
      expect(() => Note.parse('A'), throwsFormatException);
    });

    test('nearest note to a slightly sharp A4 is A4', () {
      expect(Note.nearest(443), Note.parse('A4'));
    });

    test('nearest note to 82 Hz is low E', () {
      expect(Note.nearest(82), Note.parse('E2'));
    });

    test('cents: one semitone up is +100', () {
      final a4 = Note.parse('A4');
      expect(a4.centsFrom(Note.parse('A#4').frequencyHz), closeTo(100, 0.001));
    });
  });
}
