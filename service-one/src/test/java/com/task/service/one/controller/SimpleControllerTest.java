package com.task.service.one.controller;

import com.task.service.one.model.Failure;
import com.task.service.one.model.Success;
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
        byte[] data = new byte[] {1};
        byte[] sign = new byte[] {2};

        Mockito.when(signatureService.process()).thenReturn(Mono.just(Success.create(data, sign)));

        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Success.class).isEqualTo(Success.create(data, sign));
    }

    // FIXME
    @Test
    public void testFailedGet() {
        Mockito.when(signatureService.process()).thenReturn(Mono.error(new Exception("error")));

        client
                .get()
                .uri("/")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(Failure.class).isEqualTo(Failure.create("error"));
    }
}
