package ca.carleton.musicapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * One practice session uploaded from a phone. A session owns its exercise results.
 */
@Entity
public class PracticeSession {

    @Id
    @GeneratedValue
    private Long id;

    /**
     * ID created on the phone when the session starts. The phone re-sends it on every
     * sync retry, so the server can recognize a duplicate upload instead of storing it twice.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private UUID clientSessionId;

    @Column(nullable = false)
    private String instrument;

    @Column(nullable = false)
    private String tuning;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant endedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ExerciseResult> results = new ArrayList<>();

    protected PracticeSession() {
        // for JPA
    }

    public PracticeSession(UUID clientSessionId, String instrument, String tuning,
                           Instant startedAt, Instant endedAt) {
        this.clientSessionId = clientSessionId;
        this.instrument = instrument;
        this.tuning = tuning;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }

    /** Adds a result and keeps both sides of the relationship in sync. */
    public void addResult(ExerciseResult result) {
        results.add(result);
        result.setSession(this);
    }

    public Long getId() { return id; }
    public UUID getClientSessionId() { return clientSessionId; }
    public String getInstrument() { return instrument; }
    public String getTuning() { return tuning; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getEndedAt() { return endedAt; }
    public List<ExerciseResult> getResults() { return List.copyOf(results); }
}
