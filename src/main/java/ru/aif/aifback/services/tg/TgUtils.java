package ru.aif.aifback.services.tg;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * TG API utils.
 * @author emelnikov
 */
@Slf4j
public final class TgUtils {

    /**
     * Send message.
     * @param id id
     * @param text text
     * @param bot bot
     */
    public static void sendMessage(Long id, String text, TelegramBot bot) {
        log.info("{}", bot.execute(new SendMessage(id, text)));
    }

    /**
     * Send message with keyboard.
     * @param id id
     * @param text text
     * @param keyboard keyboard
     * @param bot bot
     */
    public static void sendMessage(Long id, String text, Keyboard keyboard, TelegramBot bot) {
        log.info("{}", bot.execute(new SendMessage(id, text).replyMarkup(keyboard)));
    }
}
