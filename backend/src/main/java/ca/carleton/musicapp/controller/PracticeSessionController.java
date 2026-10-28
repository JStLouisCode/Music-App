package ca.carleton.musicapp.controller;

import ca.carleton.musicapp.dto.PracticeSessionRequest;
import ca.carleton.musicapp.dto.PracticeSessionResponse;
import ca.carleton.musicapp.service.PracticeSessionService;
import ca.carleton.musicapp.service.PracticeSessionService.SyncResult;
import ca.carleton.musicapp.service.SessionNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * REST endpoints the phone calls to sync practice history. Only handles HTTP concerns;
 * the logic lives in {@link PracticeSessionService}.
 */
@RestController
@RequestMapping("/api/sessions")
public class PracticeSessionController {

    private final PracticeSessionService service;

    public PracticeSessionController(PracticeSessionService service) {
        this.service = service;
    }

    /** 201 Created for a new session, 200 OK if this session was already uploaded. */
    @PostMapping
    public ResponseEntity<PracticeSessionResponse> sync(@Valid @RequestBody PracticeSessionRequest request) {
        SyncResult result = service.sync(request);
        if (result.created()) {
            URI location = URI.create("/api/sessions/" + result.session().id());
            return ResponseEntity.created(location).body(result.session());
        }
        return ResponseEntity.ok(result.session());
    }

    @GetMapping("/{id}")
    public PracticeSessionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    // TODO: once sign-in (Entra External ID) is added, only return the caller's own sessions.
    @GetMapping
    public List<PracticeSessionResponse> list() {
        return service.list();
    }

    @ExceptionHandler(SessionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleNotFound() {
        // empty 404 body
    }
}
