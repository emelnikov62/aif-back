package ru.aif.aifback.services.tg;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.extern.slf4j.Slf4j;

/**
 * TG API utils.
 * @author emelnikov
 */
@Slf4j
public final class TgUtils {

    /**
     * Send photo.
     * @param id id
     * @param file file
     * @param keyboard keyboard
     * @param bot bot
     */
    public static void sendPhoto(Long id, byte[] file, String caption, Keyboard keyboard, TelegramBot bot) {
        log.info("{}", bot.execute(new SendPhoto(id, file).caption(caption).replyMarkup(keyboard).parseMode(ParseMode.HTML)));
    }

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
        log.info("{}", bot.execute(new SendMessage(id, text).parseMode(ParseMode.HTML).replyMarkup(keyboard)));
    }
}
