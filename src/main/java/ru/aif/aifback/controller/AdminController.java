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
import ru.aif.aifback.model.WebhookAdminRequest;
import ru.aif.aifback.services.tg.TgService;

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

    private final TgService tgService;

    /**
     * Admin bot webhook.
     * @param webhookAdminRequest request
     * @return true/false
     */
    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> test(@RequestBody WebhookAdminRequest webhookAdminRequest) {
        return ResponseEntity.ok(tgService.process(webhookAdminRequest));
    }

    /**
     * Admin link form.
     * @param id id
     * @return html
     */
    @GetMapping(value = "/link-bot-form")
    public ResponseEntity<String> linkForm(String id) {
        return ResponseEntity.ok(String.format("<form method=\"get\" action=\"https://aif-back-emelnikov62.amvera.io/aif/admin/link-bot\">" +
                                               "    <input type=\"text\" name=\"token\"/>" +
                                               "    <input type=\"hidden\" name=\"id\" value=\"%s\"/>" +
                                               "    <input type=\"submit\" value=\"Привязать\"/>" +
                                               "</form>", id));
    }

    /**
     * Admin link bot.
     * @param id id
     * @param token token
     * @return true/false
     */
    @GetMapping(value = "/link-bot")
    public ResponseEntity<Boolean> linkBot(String id, String token) {
        return ResponseEntity.ok(tgService.linkBot(id, token));
    }
}
