package ru.aif.aifback.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.admin.TgAdminService;

/**
 * Admin controller.
 * @author emelnikov
 */
@RestController
@Slf4j
@RequestMapping(Constants.MAIN_URL + Constants.ADMIN_URL)
@RequiredArgsConstructor
@CrossOrigin(value = "*")
public class AdminController {

    private final TgAdminService tgAdminService;

    /**
     * Admin bot webhook.
     * @param webhookAdminRequest request
     * @return true/false
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> webhook(@RequestBody TgWebhookRequest webhookAdminRequest) {
        return ResponseEntity.ok(tgAdminService.process(webhookAdminRequest));
    }

    /**
     * Admin link bot.
     * @param id id
     * @param token token
     * @return true/false
     */
    @GetMapping(value = "/link-bot")
    public ResponseEntity<Boolean> linkBot(String id, String token) {
        return ResponseEntity.ok(tgAdminService.linkBot(id, token));
    }
}
