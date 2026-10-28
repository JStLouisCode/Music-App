# infrastructure/

Code that talks to the outside world: microphone capture, `PitchDetector`
implementations, local storage (offline history and the pending-sync queue) and the
HTTP client for the Spring Boot backend. Each piece implements an interface the
inner layers depend on, so it can be replaced with a fake in tests.
