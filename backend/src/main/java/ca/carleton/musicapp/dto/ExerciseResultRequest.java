package ca.carleton.musicapp.dto;

import ca.carleton.musicapp.model.AttemptOutcome;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ExerciseResultRequest(
        @NotBlank String requestedNote,
        String detectedNote,
        Double detectedFrequencyHz,
        Double centsOffset,
        @NotNull AttemptOutcome outcome,
        @Min(1) int attempts,
        @PositiveOrZero long durationMs) {
}
