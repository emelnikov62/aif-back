package ru.aif.aifback.services.process.client;

import java.util.List;

import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;

/**
 * Common client bot operation interface.
 * @author emelnikov
 */
public interface ClientBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return list messages
     */
    List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot);

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    ClientBotRecordOperationType getOperationType();

}
