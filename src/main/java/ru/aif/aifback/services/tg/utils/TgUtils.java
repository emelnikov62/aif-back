package ru.aif.aifback.services.tg.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;

/**
 * TG API utils.
 * @author emelnikov
 */
@Slf4j
public final class TgUtils {

    public static final Map<Long, String> MONTHS = Map.ofEntries(
            Map.entry(1L, "Январь"),
            Map.entry(2L, "Февраль"),
            Map.entry(3L, "Март"),
            Map.entry(4L, "Апрель"),
            Map.entry(5L, "Май"),
            Map.entry(6L, "Июнь"),
            Map.entry(7L, "Июль"),
            Map.entry(8L, "Август"),
            Map.entry(9L, "Сентябрь"),
            Map.entry(10L, "Октябрь"),
            Map.entry(11L, "Ноябрь"),
            Map.entry(12L, "Декабрь")
    );

    /**
     * Send photo.
     * @param id id
     * @param file file
     * @param keyboard keyboard
     * @param bot bot
     */
    public static void sendPhoto(Long id, byte[] file, String caption, Keyboard keyboard, TelegramBot bot) {
        log.info("{}", bot.execute(new SendPhoto(id, file).parseMode(ParseMode.HTML).caption(caption).replyMarkup(keyboard)));
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

    /**
     * Get month by number.
     * @param number number
     * @return month
     */
    public static String getMonthByNumber(Long number) {
        return MONTHS.get(number);
    }

    /**
     * Get format times by user calendar and user item.
     * @param userCalendar user calendar
     * @param userItem user item
     * @param minTimeUserItem min time user item
     * @return times
     */
    public static List<String> formatTimeCalendar(UserCalendar userCalendar, UserItem userItem, Long minTimeUserItem) {
        return Collections.emptyList();
    }
}
