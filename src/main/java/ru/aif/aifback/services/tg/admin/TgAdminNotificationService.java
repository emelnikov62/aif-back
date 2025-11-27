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
     */
    public void recordNotification(UserBot userBot, Long recordId, TgClientRecordEventType event) {
        ClientRecord clientRecord = clientRecordService.getClientRecordById(recordId);
        if (Objects.isNull(clientRecord)) {
            return;
        }

        String notification = String.format("%s <b>%s:</b> %s %02d %s %s <b>%02d:%02d</b>\n\n",
                                            event.getIcon(),
                                            event.getName(),
                                            getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                                         clientRecord.getUserCalendar().getMonth(),
                                                         clientRecord.getUserCalendar().getYear()),
                                            clientRecord.getUserCalendar().getDay(),
                                            getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                            clientRecord.getUserCalendar().getYear(),
                                            clientRecord.getHours(),
                                            clientRecord.getMins()) +
                              String.format("\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", clientRecord.getUserItem().getName()) +
                              String.format("\uD83D\uDC64 <b>Специалист:</b> %s %s %s",
                                            clientRecord.getUserStaff().getSurname(),
                                            clientRecord.getUserStaff().getName(),
                                            clientRecord.getUserStaff().getThird());

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton(BOT_RECORD_SHOW_TITLE)
                        .callbackData(String.format("%s;%s", TgAdminBotOperationType.BOT_RECORD_SHOW.getType(), recordId)));

        sendMessage(userBot.getUser().getTgId(), MESSAGE_ID_EMPTY, notification, keyboard, new TelegramBot(TG_TOKEN_ADMIN), FALSE);

    }
}
