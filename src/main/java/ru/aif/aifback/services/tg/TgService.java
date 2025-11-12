package ru.aif.aifback.services.tg;

import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * Send message.
     * @param id id
     * @param text text
     */
    public void sendMessage(Long id, String text) {
        log.info("{}", bot.execute(new SendMessage(id, text)));
    }

}
