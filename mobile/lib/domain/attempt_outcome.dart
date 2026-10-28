/// Result of one attempt at an exercise. Matches the backend's AttemptOutcome.
enum AttemptOutcome {
  correct,
  incorrect,

  /// Silence or audio the recognizer could not classify. Never counts as correct.
  unrecognized,
}
