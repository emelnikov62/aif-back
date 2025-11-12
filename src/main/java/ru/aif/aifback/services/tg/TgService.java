package ru.aif.aifback.services.tg;

import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.TgButtons.MENU_TITLE;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.WebhookAdminRequest;

/**
 * TG API service.
 * @author emelnikov
 */
@Slf4j
@Service
public class TgService {

    private TelegramBot bot;

    @PostConstruct
    void init() {
        bot = new TelegramBot(TG_TOKEN_ADMIN);
    }

    public Boolean process(WebhookAdminRequest webhookAdminRequest) {
        if (webhookAdminRequest.isCallback()) {
            processCallback();
        } else {
            processNoCallback(webhookAdminRequest.getChatId());
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     */
    public void processCallback() {
    }

    /**
     * No callback process.
     * @param id id
     */
    public void processNoCallback(String id) {
        sendMessage(Long.valueOf(id), MENU_TITLE, TgButtons.createMainMenuKeyboard());
    }

    /**
     * Send message.
     * @param id id
     * @param text text
     */
    public void sendMessage(Long id, String text) {
        log.info("{}", bot.execute(new SendMessage(id, text)));
    }

    /**
     * Send message with keyboard.
     * @param id id
     * @param text text
     * @param keyboard keyboard
     */
    public void sendMessage(Long id, String text, Keyboard keyboard) {
        log.info("{}", bot.execute(new SendMessage(id, text).replyMarkup(keyboard)));
    }

}
