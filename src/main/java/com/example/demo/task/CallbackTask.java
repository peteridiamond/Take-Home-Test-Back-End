package com.example.demo.task;

import com.example.demo.model.Callback;
import com.example.demo.model.ServiceTime;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URISyntaxException;
import java.time.Instant;
import java.util.TimerTask;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Log4j2
@Builder
public class CallbackTask extends TimerTask {

    private Callback callback;
    private Instant received;

    @Override
    public void run() {
        log.info("TimerTask - [" + received.toString() + "] - Starting for Callback [" + callback.getUrl() + "]");
        try {
            WebClient.create()
                    .post()
                    .uri(callback.getUrl().toURI())
                    .contentType(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .body(Mono.just(new ServiceTime()), ServiceTime.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (URISyntaxException | RestClientException e) {
            //TODO: Error Handler - Async
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
