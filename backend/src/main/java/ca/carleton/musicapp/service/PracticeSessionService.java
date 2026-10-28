package ca.carleton.musicapp.service;

import ca.carleton.musicapp.dto.PracticeSessionRequest;
import ca.carleton.musicapp.dto.PracticeSessionResponse;
import ca.carleton.musicapp.model.ExerciseResult;
import ca.carleton.musicapp.model.PracticeSession;
import ca.carleton.musicapp.repository.PracticeSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for storing practice history. Controllers stay thin and call this;
 * the repository is injected through the constructor (dependency injection), so unit
 * tests can pass in a mock instead of a real database.
 */
@Service
public class PracticeSessionService {

    private final PracticeSessionRepository repository;

    public PracticeSessionService(PracticeSessionRepository repository) {
        this.repository = repository;
    }

    /** What happened when the phone uploaded a session. */
    public record SyncResult(PracticeSessionResponse session, boolean created) {
    }

    /**
     * Saves an uploaded session. If the phone already uploaded this session (same
     * clientSessionId, e.g. a retry after a dropped connection), the stored copy is
     * returned and nothing new is written.
     */
    @Transactional
    public SyncResult sync(PracticeSessionRequest request) {
        return repository.findByClientSessionId(request.clientSessionId())
                .map(existing -> new SyncResult(PracticeSessionResponse.from(existing), false))
                .orElseGet(() -> new SyncResult(PracticeSessionResponse.from(repository.save(toEntity(request))), true));
    }

    @Transactional(readOnly = true)
    public PracticeSessionResponse get(Long id) {
        return repository.findById(id)
                .map(PracticeSessionResponse::from)
                .orElseThrow(() -> new SessionNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<PracticeSessionResponse> list() {
        return repository.findAll().stream().map(PracticeSessionResponse::from).toList();
    }

    private static PracticeSession toEntity(PracticeSessionRequest request) {
        PracticeSession session = new PracticeSession(request.clientSessionId(), request.instrument(),
                request.tuning(), request.startedAt(), request.endedAt());
        request.results().forEach(r -> session.addResult(new ExerciseResult(r.requestedNote(),
                r.detectedNote(), r.detectedFrequencyHz(), r.centsOffset(), r.outcome(),
                r.attempts(), r.durationMs())));
        return session;
    }
}
