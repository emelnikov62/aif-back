package ru.aif.aifback.services.process;

import ru.aif.aifback.enums.BotType;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;

/**
 * Common Bot process interface.
 * @author emelnikov
 */
public interface BotProcessService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return true/false
     */
    Boolean process(WebhookRequest webhookRequest, UserBot userBot);

    /**
     * Process with callback.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     */
    void processCallback(WebhookRequest webhookRequest, UserBot userBot);

    /**
     * Process without callback.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     */
    void processNoCallback(WebhookRequest webhookRequest, UserBot userBot);

    /**
     * Get bot type.
     * @return bot type
     */
    BotType getBotType();

}
