package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.constants.Constants.MESSAGE_ID_EMPTY;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORD_SHOW_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ACTIVE_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CONFIRM_RECORD_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;

/**
 * TG Confirm select time operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgConfirmSelectTimeOperationService implements TgClientBotOperationService {

    private final ClientService clientService;
    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String[] params = webhookRequest.getText().split(DELIMITER);
        String hours = params[2];
        String mins = params[3];
        String calendarId = params[1];
        String itemId = params[4];
        String staffId = params[5];
        String recordId = params[6];
        String answer = processBotConfirmRecord(Long.valueOf(hours),
                                                Long.valueOf(mins),
                                                Long.valueOf(itemId),
                                                Long.valueOf(calendarId),
                                                Long.valueOf(staffId),
                                                Long.valueOf(webhookRequest.getId()),
                                                webhookRequest.getChatId(),
                                                recordId,
                                                keyboard,
                                                userBot);

        keyboard.addRow(createBackButton(BOT_MAIN.getType()));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Process confirm client record.
     * @param hours hours
     * @param mins mins
     * @param itemId item id
     * @param calendarId calendar id
     * @param staffId staff id
     * @param id user bot id
     * @param clientTgId client tg id
     * @param recordId record id
     * @param keyboard keyboard
     * @param userBot user bot
     * @return answer
     */
    private String processBotConfirmRecord(Long hours, Long mins, Long itemId, Long calendarId, Long staffId, Long id, String clientTgId,
                                           String recordId, InlineKeyboardMarkup keyboard, UserBot userBot) {
        Long clientId = clientService.getClientIdOrCreate(clientTgId);
        if (Objects.isNull(clientId)) {
            return CONFIRM_RECORD_ERROR_TITLE;
        }

        Optional<Long> clientRecordId = clientRecordService.addClientRecord(
                clientId, id, itemId, calendarId, staffId, hours, mins, Objects.equals(recordId, EMPTY_PARAM) ? null : Long.valueOf(recordId));
        if (clientRecordId.isEmpty()) {
            return CONFIRM_RECORD_ERROR_TITLE;
        }

        sendNotification(userBot, clientRecordId.get());
        return fillClientRecords(keyboard, clientId, ACTIVE.getType()) ? ACTIVE_TITLE : CONFIRM_RECORD_ERROR_TITLE;
    }

    /**
     * Send notfication.
     * @param userBot user bot
     * @param clientRecordId client record id
     */
    private void sendNotification(UserBot userBot, Long clientRecordId) {
        ClientRecord clientRecord = clientRecordService.getClientRecordById(clientRecordId);
        if (Objects.isNull(clientRecord)) {
            return;
        }

        String notification = String.format("\uD83D\uDD35 <b>Новая запись:</b> %s %02d %s %s %02d:%02d\n\n",
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
                        .callbackData(String.format("%s;%s", TgAdminBotOperationType.BOT_RECORD_SHOW.getType(), clientRecordId)));

        sendMessage(userBot.getUser().getTgId(), MESSAGE_ID_EMPTY, notification, keyboard, new TelegramBot(TG_TOKEN_ADMIN), FALSE);
    }

    /**
     * Fill client records.
     * @param keyboard keyboard
     * @param clientId client id
     * @param status status
     */
    private Boolean fillClientRecords(InlineKeyboardMarkup keyboard, Long clientId, String status) {
        List<ClientRecord> clientRecords = clientRecordService.findAllByClientIdAndStatus(clientId, status);
        clientRecords.forEach(clientRecord -> {
            String dayOfWeek = getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                            clientRecord.getUserCalendar().getMonth(),
                                            clientRecord.getUserCalendar().getYear());
            keyboard.addRow(new InlineKeyboardButton(String.format("\uD83D\uDCC5 %s %s %s %s %02d:%02d (%s)",
                                                                   dayOfWeek,
                                                                   clientRecord.getUserCalendar().getDay(),
                                                                   getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                                   clientRecord.getUserCalendar().getYear(),
                                                                   clientRecord.getHours(),
                                                                   clientRecord.getMins(),
                                                                   clientRecord.getUserItem().getName()))
                                    .callbackData(String.format("%s;%s;%s", BOT_RECORD_SHOW.getType(), clientRecord.getId(), ACTIVE.getType())));
        });

        return keyboard.inlineKeyboard().length == 0 ? FALSE : TRUE;
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_CONFIRM_SELECT_TIME;
    }
}
