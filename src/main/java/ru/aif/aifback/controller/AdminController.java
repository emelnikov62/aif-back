package ru.aif.aifback.controller;

import static ru.aif.aifback.constants.Constants.TG_LOG_ID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> test(@RequestBody WebhookAdminRequest webhookAdminRequest) {
        tgService.sendMessage(TG_LOG_ID, webhookAdminRequest.toString());
        tgService.sendMessage(Long.valueOf(webhookAdminRequest.getChatId()),
                              webhookAdminRequest.toString(),
                              new InlineKeyboardMarkup(
                                      new InlineKeyboardButton("url").url("www.google.com"),
                                      new InlineKeyboardButton("callback_data").callbackData("callback_data"),
                                      new InlineKeyboardButton("Switch!").switchInlineQuery("switch_inline_query")));
        return ResponseEntity.ok().build();
    }
}
