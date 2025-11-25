package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ACTIVE_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.HISTORY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.RECORDS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.NO_ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.findByType;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.updateMessage;

import java.util.List;
import java.util.Objects;

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
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;

/**
 * TG Records active operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordsOperationService implements TgClientBotOperationService {

    private final ClientRecordService clientRecordService;
    private final ClientService clientService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String status = webhookRequest.getText().split(DELIMITER)[1];
        String answer = processBotRecords(webhookRequest.getChatId(), status, keyboard);
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));

        //sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
        updateMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot);
    }

    /**
     * Process active client records.
     * @param clientTgId client tg id
     * @param status status
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotRecords(String clientTgId, String status, InlineKeyboardMarkup keyboard) {
        Long clientId = clientService.getClientIdOrCreate(clientTgId);
        if (Objects.isNull(clientId)) {
            return RECORDS_EMPTY_TITLE;
        }

        return fillClientRecords(keyboard, clientId, status)
               ? (Objects.equals(status, NO_ACTIVE.getType()) ? HISTORY_TITLE : ACTIVE_TITLE)
               : RECORDS_EMPTY_TITLE;
    }

    /**
     * Fill client records.
     * @param keyboard keyboard
     * @param clientId client id
     * @param status status
     * @return true/false
     */
    private Boolean fillClientRecords(InlineKeyboardMarkup keyboard, Long clientId, String status) {
        List<ClientRecord> clientRecords = Objects.equals(status, NO_ACTIVE.getType())
                                           ? clientRecordService.findAllCompletedByClientId(clientId)
                                           : clientRecordService.findAllByClientIdAndStatus(clientId, status);

        clientRecords.forEach(clientRecord -> {
            String dayOfWeek = getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                            clientRecord.getUserCalendar().getMonth(),
                                            clientRecord.getUserCalendar().getYear());
            TgClientRecordType recordStatus = findByType(clientRecord.getStatus());
            keyboard.addRow(new InlineKeyboardButton(String.format("%s \uD83D\uDCC5 %s %s %s %s %02d:%02d (%s)",
                                                                   recordStatus.getIcon(),
                                                                   dayOfWeek,
                                                                   clientRecord.getUserCalendar().getDay(),
                                                                   getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                                   clientRecord.getUserCalendar().getYear(),
                                                                   clientRecord.getHours(),
                                                                   clientRecord.getMins(),
                                                                   clientRecord.getUserItem().getName()))
                                    .callbackData(String.format("%s;%s;%s",
                                                                BOT_RECORD_SHOW.getType(),
                                                                clientRecord.getId(),
                                                                status)));
        });

        return keyboard.inlineKeyboard().length == 0 ? FALSE : TRUE;
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_RECORDS;
    }
}
