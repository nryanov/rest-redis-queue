package com.task.service.one.controller;

import com.task.common.model.SignedData;
import com.task.service.one.service.SignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SimpleController {
    private final static Logger logger = LoggerFactory.getLogger(SimpleController.class);
    private final SignatureService signatureService;

    public SimpleController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SignedData> get() {
        logger.info("Process next GET request");
        return signatureService
                .process()
                .doOnError(err -> logger.error(err.getLocalizedMessage()));
    }
}
