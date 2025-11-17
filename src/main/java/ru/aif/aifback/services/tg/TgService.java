package ru.aif.aifback.services.tg;

import ru.aif.aifback.model.requests.TgWebhookRequest;

/**
 * Common TG service.
 * @author emelnikov
 */
public interface TgService {

    /**
     * Main proccessing.
     * @param webhookRequest webhookRequest
     * @return true/false
     */
    Boolean process(TgWebhookRequest webhookRequest);

    /**
     * Process with callback.
     * @param webhookRequest webhookRequest
     */
    void processCallback(TgWebhookRequest webhookRequest);

    /**
     * Process without callback.
     * @param webhookRequest webhookRequest
     */
    void processNoCallback(TgWebhookRequest webhookRequest);

}
