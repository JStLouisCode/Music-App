package ca.carleton.musicapp.service;

import ca.carleton.musicapp.dto.ExerciseResultRequest;
import ca.carleton.musicapp.dto.PracticeSessionRequest;
import ca.carleton.musicapp.model.AttemptOutcome;
import ca.carleton.musicapp.model.PracticeSession;
import ca.carleton.musicapp.repository.PracticeSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Plain unit test: no Spring context and no database, just a mocked repository. */
class PracticeSessionServiceTest {

    private PracticeSessionRepository repository;
    private PracticeSessionService service;

    @BeforeEach
    void setUp() {
        repository = mock(PracticeSessionRepository.class);
        service = new PracticeSessionService(repository);
    }

    @Test
    void newSessionIsSavedWithItsResults() {
        PracticeSessionRequest request = request(UUID.randomUUID());
        when(repository.findByClientSessionId(request.clientSessionId())).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PracticeSessionService.SyncResult result = service.sync(request);

        assertThat(result.created()).isTrue();
        ArgumentCaptor<PracticeSession> saved = ArgumentCaptor.forClass(PracticeSession.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getResults()).hasSize(2);
        assertThat(saved.getValue().getResults().get(0).getSession()).isSameAs(saved.getValue());
    }

    @Test
    void retriedUploadDoesNotCreateADuplicate() {
        PracticeSessionRequest request = request(UUID.randomUUID());
        PracticeSession existing = new PracticeSession(request.clientSessionId(), "guitar", "standard",
                request.startedAt(), request.endedAt());
        when(repository.findByClientSessionId(request.clientSessionId())).thenReturn(Optional.of(existing));

        PracticeSessionService.SyncResult result = service.sync(request);

        assertThat(result.created()).isFalse();
        verify(repository, never()).save(any());
    }

    static PracticeSessionRequest request(UUID clientId) {
        Instant start = Instant.parse("2026-10-28T15:00:00Z");
        return new PracticeSessionRequest(clientId, "guitar", "standard", start, start.plusSeconds(300),
                List.of(new ExerciseResultRequest("A4", "A4", 440.8, 3.1, AttemptOutcome.CORRECT, 1, 2100),
                        new ExerciseResultRequest("E2", null, null, null, AttemptOutcome.UNRECOGNIZED, 2, 6400)));
    }
}
