package ru.aif.aifback.services.tg.client.bot.record;

import static java.lang.Boolean.FALSE;

import static ru.aif.aifback.constants.Constants.MESSAGE_ID_EMPTY;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORD_SHOW_TITLE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.FINISHED;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.NO_ACTIVE;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;

/**
 * Tg Client bot record notification service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientBotRecordNotificationService {

    /**
     * Fill record date.
     * @param record client record
     * @return record date
     */
    private String fillRecordDate(ClientRecord record) {
        return String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s <b>%02d:%02d</b>",
                             getDayOfWeek(record.getUserCalendar().getDay(),
                                          record.getUserCalendar().getMonth(),
                                          record.getUserCalendar().getYear()),
                             record.getUserCalendar().getDay(),
                             getMonthByNumber(record.getUserCalendar().getMonth()),
                             record.getUserCalendar().getYear(),
                             record.getHours(),
                             record.getMins());
    }

    /**
     * Fill client record staff.
     * @param record client record
     * @return record staff
     */
    private String fillRecordStaff(ClientRecord record) {
        return String.format("\uD83D\uDC64 <b>Специалист:</b> %s %s %s",
                             record.getUserStaff().getSurname(),
                             record.getUserStaff().getName(),
                             record.getUserStaff().getThird());
    }

    /**
     * Send notification complete record.
     * @param record client record
     */
    public void recordCompleteNotification(ClientRecord record) {
        String notification = String.format("%s <b>%s</b>\n\n", FINISHED.getIcon(), FINISHED.getName()) +
                              fillRecordDate(record) +
                              String.format("\n\n\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", record.getUserItem().getName()) +
                              fillRecordStaff(record);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton(BOT_RECORD_SHOW_TITLE)
                        .callbackData(String.format("%s;%s;%s", BOT_RECORD_SHOW.getType(), record.getId(), NO_ACTIVE.getType())));

        sendMessage(record.getClient().getTgId(), MESSAGE_ID_EMPTY, notification, keyboard, new TelegramBot(record.getUserBot().getToken()), FALSE);
    }
}
