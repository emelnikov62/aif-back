package ru.aif.aifback.services.process.admin;

import java.util.List;

import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;

/**
 * Common Admin Bot operation interface.
 * @author emelnikov
 */
public interface AdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    List<ChatMessage> process(WebhookRequest webhookRequest);

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    AdminBotOperationType getOperationType();

}
