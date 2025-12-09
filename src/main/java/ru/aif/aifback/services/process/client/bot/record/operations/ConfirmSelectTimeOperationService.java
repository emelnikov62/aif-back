package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ACTIVE_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CONFIRM_RECORD_ERROR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordEventType.EDIT;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordEventType.NEW;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.process.admin.notification.TgAdminNotificationService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;

/**
 * Confirm select time operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmSelectTimeOperationService implements ClientBotOperationService {

    private final ClientService clientService;
    private final ClientRecordService clientRecordService;
    private final TgAdminNotificationService tgAdminNotificationService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String hours = params[2];
        String mins = params[3];
        String calendarId = params[1];
        String itemId = params[4];
        String staffId = params[5];
        String recordId = params[6];

        return processBotConfirmRecord(Long.valueOf(hours),
                                       Long.valueOf(mins),
                                       Long.valueOf(itemId),
                                       Long.valueOf(calendarId),
                                       Long.valueOf(staffId),
                                       webhookRequest.getChatId(),
                                       webhookRequest.getMessageId(),
                                       recordId,
                                       webhookRequest.getSource(),
                                       userBot);
    }

    /**
     * Process confirm client record.
     * @param hours hours
     * @param mins mins
     * @param itemId item id
     * @param calendarId calendar id
     * @param staffId staff id
     * @param chatId client id
     * @param messageId message id
     * @param recordId record id
     * @param source source
     * @param userBot user bot
     * @return messages
     */
    private List<ChatMessage> processBotConfirmRecord(Long hours, Long mins, Long itemId, Long calendarId, Long staffId, String chatId,
                                                      String messageId, String recordId, String source, UserBot userBot) {
        Long clientId = clientService.getClientIdOrCreate(chatId);
        if (Objects.isNull(clientId)) {
            return fillEmptyMessages(source, chatId, messageId);
        }

        ClientRecord clientRecordPrev = null;
        if (!Objects.equals(recordId, EMPTY_PARAM)) {
            clientRecordPrev = clientRecordService.getClientRecordById(Long.valueOf(recordId));
        }

        Long clientRecordId = clientRecordService.addClientRecord(clientId,
                                                                  userBot.getId(),
                                                                  itemId,
                                                                  calendarId,
                                                                  staffId,
                                                                  hours,
                                                                  mins,
                                                                  Objects.equals(recordId, EMPTY_PARAM) ? null : Long.valueOf(recordId))
                                                 .orElse(null);
        if (Objects.isNull(clientRecordId)) {
            return fillEmptyMessages(source, chatId, messageId);
        }

        tgAdminNotificationService.recordNotification(userBot, clientRecordId, clientRecordPrev, Objects.equals(recordId, EMPTY_PARAM) ? NEW : EDIT);

        return fillClientRecords(clientId, ACTIVE.getType(), source, chatId, messageId);
    }

    /**
     * Fill client records.
     * @param clientId client id
     * @param status status
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @return messages
     */
    private List<ChatMessage> fillClientRecords(Long clientId, String status, String source, String chatId, String messageId) {
        List<ClientRecord> clientRecords = clientRecordService.findAllByClientIdAndStatus(clientId, status);
        if (clientRecords.isEmpty()) {
            return fillEmptyMessages(source, chatId, messageId);
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        clientRecords.forEach(clientRecord -> {
            String dayOfWeek = getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                            clientRecord.getUserCalendar().getMonth(),
                                            clientRecord.getUserCalendar().getYear());
            buttons.add(List.of(ChatMessage.Button.builder()
                                                  .title(String.format("\uD83D\uDCC5 %s %s %s %s %02d:%02d (%s)",
                                                                       dayOfWeek,
                                                                       clientRecord.getUserCalendar().getDay(),
                                                                       getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                                       clientRecord.getUserCalendar().getYear(),
                                                                       clientRecord.getHours(),
                                                                       clientRecord.getMins(),
                                                                       clientRecord.getUserItem().getName()))
                                                  .callback(String.format("%s;%s;%s", BOT_RECORD_SHOW.getType(), clientRecord.getId(),
                                                                          ACTIVE.getType()))
                                                  .build()));
        });

        buttons.add(createBackButton(BOT_MAIN.getType()));

        return List.of(ChatMessage.builder()
                                  .text(ACTIVE_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Fill empty messages.
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @return messages
     */
    private List<ChatMessage> fillEmptyMessages(String source, String chatId, String messageId) {
        return List.of(ChatMessage.builder()
                                  .text(CONFIRM_RECORD_ERROR_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(BOT_MAIN.getType())))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_CONFIRM_SELECT_TIME;
    }
}
