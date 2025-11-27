package ru.aif.aifback.services.tg.admin;

import static java.lang.Boolean.FALSE;

import static ru.aif.aifback.constants.Constants.MESSAGE_ID_EMPTY;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORD_SHOW_TITLE;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordEventType;

/**
 * Tg Admin notification service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAdminNotificationService {

    private final ClientRecordService clientRecordService;

    /**
     * Send notification about record.
     * @param userBot user bot
     * @param recordId client record id
     * @param prevStateRecord previous client record state
     * @param event record event type
     */
    public void recordNotification(UserBot userBot, Long recordId, ClientRecord prevStateRecord, TgClientRecordEventType event) {
        ClientRecord clientRecord = clientRecordService.getClientRecordById(recordId);
        if (Objects.isNull(clientRecord)) {
            return;
        }

        String notification = String.format("%s <b>%s</b>\n\n",
                                            event.getIcon(),
                                            event.getName()) +
                              fillRecordDate(clientRecord, prevStateRecord) +
                              String.format("\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", clientRecord.getUserItem().getName()) +
                              fillRecordStaff(clientRecord, prevStateRecord);

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton(BOT_RECORD_SHOW_TITLE)
                        .callbackData(String.format("%s;%s", TgAdminBotOperationType.BOT_RECORD_SHOW.getType(), recordId)));

        sendMessage(userBot.getUser().getTgId(), MESSAGE_ID_EMPTY, notification, keyboard, new TelegramBot(TG_TOKEN_ADMIN), FALSE);
    }

    /**
     * Fill record date.
     * @param current current state record
     * @param prev previous state record
     * @return record date
     */
    private String fillRecordDate(ClientRecord current, ClientRecord prev) {
        String date = String.format("\uD83D\uDCC5 <b>%s:</b> %s %02d %s %s <b>%02d:%02d</b>",
                                    Objects.isNull(prev) ? "Дата" : "Новая дата",
                                    getDayOfWeek(current.getUserCalendar().getDay(),
                                                 current.getUserCalendar().getMonth(),
                                                 current.getUserCalendar().getYear()),
                                    current.getUserCalendar().getDay(),
                                    getMonthByNumber(current.getUserCalendar().getMonth()),
                                    current.getUserCalendar().getYear(),
                                    current.getHours(),
                                    current.getMins());

        if (Objects.nonNull(prev)) {
            date += String.format("\n\n❌ <b>Прошлая дата:</b> %s %02d %s %s <b>%02d:%02d</b>\n\n",
                                  getDayOfWeek(prev.getUserCalendar().getDay(), prev.getUserCalendar().getMonth(), prev.getUserCalendar().getYear()),
                                  prev.getUserCalendar().getDay(),
                                  getMonthByNumber(prev.getUserCalendar().getMonth()),
                                  prev.getUserCalendar().getYear(),
                                  prev.getHours(),
                                  prev.getMins());
        }

        return date;
    }

    private String fillRecordStaff(ClientRecord current, ClientRecord prev) {
        String staff = String.format("\uD83D\uDC64 <b>%s:</b> %s %s %s",
                                     Objects.isNull(prev) ? "Специалист" : "Новый специалист",
                                     current.getUserStaff().getSurname(),
                                     current.getUserStaff().getName(),
                                     current.getUserStaff().getThird());

        if (Objects.nonNull(prev)) {
            staff += String.format("\n\n❌ <b>Прошлый специалист:</b> %s %s %s",
                                   current.getUserStaff().getSurname(),
                                   current.getUserStaff().getName(),
                                   current.getUserStaff().getThird());
        }

        return staff;
    }
}
