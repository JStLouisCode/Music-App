package ca.carleton.musicapp.service;

public class SessionNotFoundException extends RuntimeException {

    public SessionNotFoundException(Long id) {
        super("Practice session " + id + " not found");
    }
}
