package ca.carleton.musicapp.model;

/** Result of one exercise, as decided on the phone. */
public enum AttemptOutcome {
    CORRECT,
    INCORRECT,
    /** Silence or audio the recognizer could not classify. Never counts as correct. */
    UNRECOGNIZED
}
