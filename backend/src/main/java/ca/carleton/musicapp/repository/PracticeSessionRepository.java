package ca.carleton.musicapp.repository;

import ca.carleton.musicapp.model.PracticeSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PracticeSessionRepository extends JpaRepository<PracticeSession, Long> {

    Optional<PracticeSession> findByClientSessionId(UUID clientSessionId);
}
