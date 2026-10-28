package ca.carleton.musicapp.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Full application context (controller + service + JPA + H2), called through MockMvc. */
@SpringBootTest
@AutoConfigureMockMvc
class PracticeSessionControllerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void uploadThenFetchSession() throws Exception {
        String body = sessionJson(UUID.randomUUID());

        String location = mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.results.length()").value(1))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.instrument").value("guitar"))
                .andExpect(jsonPath("$.results[0].outcome").value("CORRECT"));
    }

    @Test
    void sameSessionUploadedTwiceIsStoredOnce() throws Exception {
        String body = sessionJson(UUID.randomUUID());

        String firstId = mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location").replace("/api/sessions/", "");

        mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Long.parseLong(firstId)));
    }

    @Test
    void invalidSessionIsRejected() throws Exception {
        mvc.perform(post("/api/sessions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"instrument\":\"guitar\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownSessionReturns404() throws Exception {
        mvc.perform(get("/api/sessions/999999")).andExpect(status().isNotFound());
    }

    private static String sessionJson(UUID clientId) {
        return """
                {
                  "clientSessionId": "%s",
                  "instrument": "guitar",
                  "tuning": "standard",
                  "startedAt": "2026-10-28T15:00:00Z",
                  "endedAt": "2026-10-28T15:05:00Z",
                  "results": [
                    {"requestedNote": "A4", "detectedNote": "A4", "detectedFrequencyHz": 440.8,
                     "centsOffset": 3.1, "outcome": "CORRECT", "attempts": 1, "durationMs": 2100}
                  ]
                }
                """.formatted(clientId);
    }
}
