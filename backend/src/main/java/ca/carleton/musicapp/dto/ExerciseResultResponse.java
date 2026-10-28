package ca.carleton.musicapp.dto;

import ca.carleton.musicapp.model.AttemptOutcome;
import ca.carleton.musicapp.model.ExerciseResult;

public record ExerciseResultResponse(
        Long id,
        String requestedNote,
        String detectedNote,
        Double detectedFrequencyHz,
        Double centsOffset,
        AttemptOutcome outcome,
        int attempts,
        long durationMs) {

    public static ExerciseResultResponse from(ExerciseResult r) {
        return new ExerciseResultResponse(r.getId(), r.getRequestedNote(), r.getDetectedNote(),
                r.getDetectedFrequencyHz(), r.getCentsOffset(), r.getOutcome(),
                r.getAttempts(), r.getDurationMs());
    }
}
