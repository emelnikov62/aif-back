package ru.aif.aifback.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.ProcessService;

/**
 * Client controller.
 * @author emelnikov
 */
@RestController
@Slf4j
@RequestMapping(Constants.MAIN_URL + Constants.CLIENT_URL)
@CrossOrigin(value = "*")
public class ClientController {

    private final ProcessService clientProcessService;

    public ClientController(@Qualifier("clientProcessService") ProcessService clientProcessService) {
        this.clientProcessService = clientProcessService;
    }

    /**
     * Client bot webhook.
     * @param webhookRequest request
     * @return true/false
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> webhook(@RequestBody WebhookRequest webhookRequest) {
        return ResponseEntity.ok(clientProcessService.process(webhookRequest));
    }
}
