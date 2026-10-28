package ca.carleton.musicapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** JSON body the phone sends when it syncs a finished practice session. */
public record PracticeSessionRequest(
        @NotNull UUID clientSessionId,
        @NotBlank String instrument,
        @NotBlank String tuning,
        @NotNull Instant startedAt,
        Instant endedAt,
        @NotNull List<@Valid @NotNull ExerciseResultRequest> results) {
}
