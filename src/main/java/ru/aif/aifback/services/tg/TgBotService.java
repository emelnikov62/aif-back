package ru.aif.aifback.services.tg;

import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.enums.TgClientTypeBot;

/**
 * Common TG Bot interface.
 * @author emelnikov
 */
public interface TgBotService {

    /**
     * Main proccessing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return true/false
     */
    Boolean process(TgWebhookRequest webhookRequest, UserBot userBot);

    /**
     * Process with callback.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     */
    void processCallback(TgWebhookRequest webhookRequest, UserBot userBot);

    /**
     * Process without callback.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     */
    void processNoCallback(TgWebhookRequest webhookRequest, UserBot userBot);

    /**
     * Get bot type.
     * @return bot type
     */
    TgClientTypeBot getBotType();

}
