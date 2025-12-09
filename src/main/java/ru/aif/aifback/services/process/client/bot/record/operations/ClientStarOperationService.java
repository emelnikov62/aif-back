package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SUCCESS_CLIENT_STAR;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_CLIENT_STAR;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.NO_ACTIVE;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;

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
import ru.aif.aifback.services.client.ClientStarService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;

/**
 * Client star operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientStarOperationService implements ClientBotOperationService {

    private final ClientRecordService clientRecordService;
    private final ClientStarService clientStarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String star = params[1];
        String recordId = params[2];

        ClientRecord clientRecord = clientRecordService.getClientRecordById(Long.valueOf(recordId));
        if (Objects.isNull(clientRecord)) {
            return fillErrorMessages(webhookRequest.getChatId(), webhookRequest.getMessageId(), recordId, webhookRequest.getSource());
        }

        if (Objects.isNull(clientStarService.addClientStar(clientRecord.getAifClientId(),
                                                           clientRecord.getAifUserBotId(),
                                                           clientRecord.getAifUserItemId(),
                                                           clientRecord.getAifUserStaffId(),
                                                           Long.valueOf(star)))) {
            return fillErrorMessages(webhookRequest.getChatId(), webhookRequest.getMessageId(), recordId, webhookRequest.getSource());
        }

        return List.of(ChatMessage.builder()
                                  .text(SUCCESS_CLIENT_STAR)
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(List.of(createBackButton(
                                          String.format("%s;%s;%s", BOT_RECORD_SHOW.getType(), recordId, NO_ACTIVE.getType()))))
                                  .build());
    }

    /**
     * Fill error messages.
     * @param chatId chat id
     * @param messageId message id
     * @param recordId record id
     * @param source source
     * @return messages
     */
    private List<ChatMessage> fillErrorMessages(String chatId, String messageId, String recordId, String source) {
        return List.of(ChatMessage.builder()
                                  .text(SHOW_ERROR_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(String.format("%s;%s;%s", BOT_RECORD_SHOW, recordId, NO_ACTIVE.getType()))))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_CLIENT_STAR;
    }
}
