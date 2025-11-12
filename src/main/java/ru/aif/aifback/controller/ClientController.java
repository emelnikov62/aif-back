package ru.aif.aifback.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.WebhookRequest;
import ru.aif.aifback.services.tg.TgAdminService;

/**
 * Client controller.
 * @author emelnikov
 */
@RestController
@Slf4j
@RequestMapping(Constants.MAIN_URL + Constants.CLIENT_URL)
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class ClientController {

    private final TgAdminService tgService;

    /**
     * Client bot webhook.
     * @param webhookRequest request
     * @return true/false
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> test(@RequestBody WebhookRequest webhookRequest) {
        return ResponseEntity.ok(tgService.process(webhookRequest));
    }
}
