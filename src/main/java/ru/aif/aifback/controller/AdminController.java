package ru.aif.aifback.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.ProcessService;

/**
 * Admin controller.
 * @author emelnikov
 */
@RestController
@Slf4j
@RequestMapping(Constants.MAIN_URL + Constants.ADMIN_URL)
@CrossOrigin(value = "*")
public class AdminController {

    private final ProcessService adminProcessService;
    private final ProcessService adminLinkProcessService;

    public AdminController(@Qualifier("adminProcessService") ProcessService adminProcessService,
                           @Qualifier("adminLinkProcessService") ProcessService adminLinkProcessService) {
        this.adminProcessService = adminProcessService;
        this.adminLinkProcessService = adminLinkProcessService;
    }

    /**
     * Admin bot webhook.
     * @param webhookAdminRequest request
     * @return true/false
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> webhook(@RequestBody WebhookRequest webhookAdminRequest) {
        return ResponseEntity.ok(adminProcessService.process(webhookAdminRequest));
    }

    /**
     * Admin link bot.
     * @param id id
     * @param token token
     * @return true/false
     */
    @GetMapping(value = "/link-bot")
    public ResponseEntity<Boolean> linkBot(String id, String token) {
        return ResponseEntity.ok(adminLinkProcessService.process(WebhookRequest.builder().token(token).id(id).build()));
    }
}
