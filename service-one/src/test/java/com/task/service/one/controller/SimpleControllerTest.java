package com.task.service.one.controller;

import com.task.common.model.SignedData;
import com.task.service.one.service.SignatureService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = SimpleController.class)
public class SimpleControllerTest {
    @Autowired
    private WebTestClient client;
    @MockBean
    private SignatureService signatureService;

    @Test
    public void testSuccessGet() {
        SignedData success = SignedData.create(new byte[] {1}, new byte[] {1});
        success.setValid(true);

        Mockito.when(signatureService.process()).thenReturn(Mono.just(success));

        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SignedData.class).isEqualTo(success);
    }

    @Test
    public void testFailedGet() {
        Mockito.when(signatureService.process()).thenReturn(Mono.error(new Exception("error")));

        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(String.class).isEqualTo("error");
    }
}
