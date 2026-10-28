package ca.carleton.musicapp.dto;

import ca.carleton.musicapp.model.PracticeSession;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PracticeSessionResponse(
        Long id,
        UUID clientSessionId,
        String instrument,
        String tuning,
        Instant startedAt,
        Instant endedAt,
        List<ExerciseResultResponse> results) {

    public static PracticeSessionResponse from(PracticeSession s) {
        return new PracticeSessionResponse(s.getId(), s.getClientSessionId(), s.getInstrument(),
                s.getTuning(), s.getStartedAt(), s.getEndedAt(),
                s.getResults().stream().map(ExerciseResultResponse::from).toList());
    }
}
