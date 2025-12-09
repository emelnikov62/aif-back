package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SUCCESS_CANCEL_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordEventType.CANCEL;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.admin.notification.TgAdminNotificationService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;

/**
 * Record cancel operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordCancelOperationService implements ClientBotOperationService {

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
        Long recordId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);

        String answer = SUCCESS_CANCEL_RECORD;
        if (!clientRecordService.cancelRecord(recordId)) {
            answer = SHOW_ERROR_TITLE;
        }

        tgAdminNotificationService.recordNotification(userBot, recordId, null, CANCEL);

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(List.of(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), ACTIVE.getType()))))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_RECORD_CANCEL;
    }
}
