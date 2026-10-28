package ca.carleton.musicapp.repository;

import ca.carleton.musicapp.model.AttemptOutcome;
import ca.carleton.musicapp.model.ExerciseResult;
import ca.carleton.musicapp.model.PracticeSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/** Checks the JPA mapping against a real (in-memory H2) database. */
@DataJpaTest
class PracticeSessionRepositoryTest {

    @Autowired
    private PracticeSessionRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savingASessionAlsoSavesItsResults() {
        UUID clientId = UUID.randomUUID();
        PracticeSession session = new PracticeSession(clientId, "piano", "A440",
                Instant.parse("2026-10-28T15:00:00Z"), null);
        session.addResult(new ExerciseResult("C4", "C4", 261.9, 1.2, AttemptOutcome.CORRECT, 1, 1800));

        repository.save(session);
        entityManager.flush();
        entityManager.clear(); // force a real read from the database below

        PracticeSession loaded = repository.findByClientSessionId(clientId).orElseThrow();
        assertThat(loaded.getResults()).singleElement()
                .satisfies(r -> assertThat(r.getRequestedNote()).isEqualTo("C4"));
    }
}
