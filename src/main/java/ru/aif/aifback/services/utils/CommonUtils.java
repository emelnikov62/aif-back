package ru.aif.aifback.services.utils;

import static ru.aif.aifback.constants.Constants.DAY_OF_WEEK;
import static ru.aif.aifback.constants.Constants.EMPTY_FILE_PATH;
import static ru.aif.aifback.constants.Constants.MONTHS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;

/**
 * Common API utils.
 * @author emelnikov
 */
@Slf4j
public final class CommonUtils {

    /**
     * Send photo.
     * @param chatId chat id
     * @param messageId message id
     * @param file file
     * @param keyboard keyboard
     * @param bot bot
     */
    public static void sendPhoto(String chatId, int messageId, byte[] file, String caption, Keyboard keyboard, TelegramBot bot) {
        bot.execute(new SendPhoto(chatId, file).parseMode(ParseMode.HTML).caption(caption).replyMarkup(keyboard));
        deleteMessage(chatId, messageId, bot);
    }

    /**
     * Delete message.
     * @param chatId chat id
     * @param messageId message id
     * @param bot bot
     */
    public static void deleteMessage(String chatId, int messageId, TelegramBot bot) {
        try {
            bot.execute(new DeleteMessage(chatId, messageId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Send message.
     * @param chatId chat id
     * @param messageId message id
     * @param text text
     * @param bot bot
     * @param update update
     * @return new message id
     */
    public static Integer sendMessage(String chatId, int messageId, String text, TelegramBot bot, Boolean update) {
        Integer id = bot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML)).message().messageId();

        if (update) {
            deleteMessage(chatId, messageId, bot);
        }

        return id;
    }

    /**
     * Send message with keyboard.
     * @param chatId chat id
     * @param messageId message id
     * @param text text
     * @param keyboard keyboard
     * @param bot bot
     * @param update update
     */
    public static void sendMessage(String chatId, int messageId, String text, InlineKeyboardMarkup keyboard, TelegramBot bot, Boolean update) {
        bot.execute(new SendMessage(chatId, text).parseMode(ParseMode.HTML).replyMarkup(keyboard));

        if (update) {
            deleteMessage(chatId, messageId, bot);
        }
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
     * @param minUserItem min user item
     * @param records client records
     * @return times
     */
    public static List<ClientRecordTime> formatTimeCalendar(UserCalendar userCalendar,
                                                            UserItem userItem,
                                                            UserItem minUserItem,
                                                            List<ClientRecord> records) {
        List<ClientRecordTime> times = new ArrayList<>();
        long userItemTime = userItem.getHours() * 60 + userItem.getMins();

        if (records.isEmpty()) {
            long allTime = (((userCalendar.getHoursEnd() - userItem.getHours()) - userCalendar.getHoursStart()) * 60) - userItem.getMins();
            int repeated = (int) Math.ceil((double) (allTime / userItemTime)) + 1;
            fillTimes(repeated, minUserItem.getHours(), minUserItem.getMins(), userItem.getId(), userCalendar, null, null)
                    .ifPresent(times::addAll);
        } else {
            LocalDateTime startDate = LocalDateTime.of(Math.toIntExact(userCalendar.getYear()),
                                                       Math.toIntExact(userCalendar.getMonth()),
                                                       Math.toIntExact(userCalendar.getDay()),
                                                       Math.toIntExact(userCalendar.getHoursStart()),
                                                       Math.toIntExact(userCalendar.getMinsStart()));

            for (ClientRecord record : records) {
                LocalDateTime nextDate = LocalDateTime.of(Math.toIntExact(userCalendar.getYear()),
                                                          Math.toIntExact(userCalendar.getMonth()),
                                                          Math.toIntExact(userCalendar.getDay()),
                                                          Math.toIntExact(record.getHours()),
                                                          Math.toIntExact(record.getMins()));

                long diff = ChronoUnit.MINUTES.between(startDate, nextDate);
                if (diff >= userItemTime) {
                    int repeated = (int) (diff / userItemTime);
                    fillTimes(repeated, minUserItem.getHours(), minUserItem.getMins(), userItem.getId(), userCalendar, userCalendar.getHoursStart(),
                              userCalendar.getMinsStart()).ifPresent(times::addAll);
                }

                startDate = nextDate;
                startDate = startDate.plusHours(record.getUserItem().getHours());
                startDate = startDate.plusMinutes(record.getUserItem().getMins());
            }

            LocalDateTime endDate = LocalDateTime.of(Math.toIntExact(userCalendar.getYear()),
                                                     Math.toIntExact(userCalendar.getMonth()),
                                                     Math.toIntExact(userCalendar.getDay()),
                                                     Math.toIntExact(userCalendar.getHoursEnd()),
                                                     Math.toIntExact(userCalendar.getMinsEnd()));
            long diff = ChronoUnit.MINUTES.between(startDate, endDate);
            if (diff >= userItemTime) {
                int repeated = (int) (diff / userItemTime);
                fillTimes(repeated, minUserItem.getHours(), minUserItem.getMins(), userItem.getId(), userCalendar, (long) startDate.getHour(),
                          (long) startDate.getMinute()).ifPresent(times::addAll);
            }
        }

        return times;
    }

    /**
     * Fill times by count.
     * @param count count
     * @param hours hours
     * @param mins mins
     * @param userItemId user item id
     * @param userCalendar user calendar
     * @param hoursStart hours start
     * @param minsStart mins start
     * @return times
     */
    public static Optional<List<ClientRecordTime>> fillTimes(int count,
                                                             long hours,
                                                             long mins,
                                                             Long userItemId,
                                                             UserCalendar userCalendar,
                                                             Long hoursStart,
                                                             Long minsStart) {
        if (count == 0) {
            return Optional.empty();
        }

        List<ClientRecordTime> times = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.of(Math.toIntExact(userCalendar.getYear()),
                                                   Math.toIntExact(userCalendar.getMonth()),
                                                   Math.toIntExact(userCalendar.getDay()),
                                                   Math.toIntExact(Objects.isNull(hoursStart)
                                                                   ? userCalendar.getHoursStart()
                                                                   : hoursStart),
                                                   Math.toIntExact(Objects.isNull(minsStart)
                                                                   ? userCalendar.getMinsStart()
                                                                   : minsStart));
        for (int i = 1; i <= count; i++) {
            times.add(ClientRecordTime.builder()
                                      .hours(startDate.toLocalTime().getHour())
                                      .mins(startDate.toLocalTime().getMinute())
                                      .staffId(userCalendar.getAifUserStaffId())
                                      .calendarId(userCalendar.getId())
                                      .itemId(userItemId)
                                      .build());

            startDate = startDate.plusHours(hours);
            startDate = startDate.plusMinutes(mins);
        }

        return Optional.of(times);
    }

    /**
     * Get day of week.
     * @param day day
     * @param month month
     * @param year year
     * @return day of week
     */
    public static String getDayOfWeek(Long day, Long month, Long year) {
        return DAY_OF_WEEK.get(LocalDate.of(year.intValue(), month.intValue(), day.intValue()).getDayOfWeek().getValue());
    }

    /**
     * Get file data or empty.
     * @param userItemFileData user item file data
     * @return file data
     */
    public static byte[] getFileDataImage(String userItemFileData) {
        try {
            byte[] fileData;

            if (Objects.isNull(userItemFileData)) {
                fileData = IOUtils.toByteArray(Objects.requireNonNull(CommonUtils.class.getResourceAsStream(EMPTY_FILE_PATH)));
            } else {
                fileData = Base64.getDecoder().decode(userItemFileData);
            }

            return fileData;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
