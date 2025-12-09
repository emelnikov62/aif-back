package ru.aif.aifback.services.process.client.bot.record.notification;

import static java.lang.Boolean.FALSE;

import static ru.aif.aifback.constants.Constants.MESSAGE_ID_EMPTY;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORD_SHOW_TITLE;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;
import ru.aif.aifback.services.utils.CommonUtils;

/**
 * Client bot record notification service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientBotRecordNotificationService {

    /**
     * Fill record date.
     * @param record client record
     * @return record date
     */
    private String fillRecordDate(ClientRecord record) {
        return String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s <b>%02d:%02d</b>",
                             CommonUtils.getDayOfWeek(record.getUserCalendar().getDay(),
                                                      record.getUserCalendar().getMonth(),
                                                      record.getUserCalendar().getYear()),
                             record.getUserCalendar().getDay(),
                             CommonUtils.getMonthByNumber(record.getUserCalendar().getMonth()),
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
    public void recordNotification(ClientRecord record, ClientRecordType status) {
        String notification = String.format("%s <b>%s</b>\n\n", status.getIcon(), status.getName()) +
                              fillRecordDate(record) +
                              String.format("\n\n\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", record.getUserItem().getName()) +
                              fillRecordStaff(record);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton(BOT_RECORD_SHOW_TITLE)
                        .callbackData(String.format("%s;%s;%s", ClientBotRecordOperationType.BOT_RECORD_SHOW.getType(), record.getId(),
                                                    ClientRecordType.NO_ACTIVE.getType())));

        CommonUtils.sendMessage(record.getClient().getSourceId(), MESSAGE_ID_EMPTY, notification, keyboard,
                                new TelegramBot(record.getUserBot().getToken()), FALSE);
    }

}
