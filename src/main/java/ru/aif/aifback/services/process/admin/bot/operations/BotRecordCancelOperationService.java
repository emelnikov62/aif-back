package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOTS_ERROR_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOTS_SUCCESS_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.CANCEL;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordNotificationService;

/**
 * Admin Bot record cancel operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordCancelOperationService implements AdminBotOperationService {

    private final ClientRecordService clientRecordService;
    private final TgClientBotRecordNotificationService tgClientBotRecordNotificationService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String month = params[1];
        String year = params[2];
        String type = params[4];
        String userBotId = params[3];
        String recordId = params[5];

        String answer = BOTS_ERROR_CANCEL_RECORD_TITLE;

        if (clientRecordService.cancelRecord(Long.valueOf(recordId))) {
            answer = BOTS_SUCCESS_CANCEL_RECORD_TITLE;

            ClientRecord clientRecord = clientRecordService.getClientRecordById(Long.valueOf(recordId));
            if (Objects.nonNull(clientRecord)) {
                tgClientBotRecordNotificationService.recordNotification(clientRecord, CANCEL);
            }
        }

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(FALSE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(createBackButton(String.format("%s;%s;%s;%s;%s", BOT_RECORD_DAY.getType(), month, year, userBotId, type)))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public AdminBotOperationType getOperationType() {
        return BOT_RECORD_CANCEL;
    }
}
