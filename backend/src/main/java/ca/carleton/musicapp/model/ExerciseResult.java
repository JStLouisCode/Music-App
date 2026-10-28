package ca.carleton.musicapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * One exercise inside a practice session. Only the result is stored: raw microphone
 * audio never leaves the phone.
 */
@Entity
public class ExerciseResult {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    private PracticeSession session;

    /** Note the user was asked to play, e.g. "A4". */
    @Column(nullable = false)
    private String requestedNote;

    /** Note the phone detected, or null when nothing was recognized. */
    private String detectedNote;

    private Double detectedFrequencyHz;

    /** Distance from the requested pitch in cents (100 cents = 1 semitone). */
    private Double centsOffset;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttemptOutcome outcome;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private long durationMs;

    protected ExerciseResult() {
        // for JPA
    }

    public ExerciseResult(String requestedNote, String detectedNote, Double detectedFrequencyHz,
                          Double centsOffset, AttemptOutcome outcome, int attempts, long durationMs) {
        this.requestedNote = requestedNote;
        this.detectedNote = detectedNote;
        this.detectedFrequencyHz = detectedFrequencyHz;
        this.centsOffset = centsOffset;
        this.outcome = outcome;
        this.attempts = attempts;
        this.durationMs = durationMs;
    }

    void setSession(PracticeSession session) { this.session = session; }

    public Long getId() { return id; }
    public PracticeSession getSession() { return session; }
    public String getRequestedNote() { return requestedNote; }
    public String getDetectedNote() { return detectedNote; }
    public Double getDetectedFrequencyHz() { return detectedFrequencyHz; }
    public Double getCentsOffset() { return centsOffset; }
    public AttemptOutcome getOutcome() { return outcome; }
    public int getAttempts() { return attempts; }
    public long getDurationMs() { return durationMs; }
}
