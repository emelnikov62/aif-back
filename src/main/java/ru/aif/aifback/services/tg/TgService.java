package ru.aif.aifback.services.tg;

import ru.aif.aifback.model.requests.TgWebhookRequest;

/**
 * Common TG interface.
 * @author emelnikov
 */
public interface TgService {

    /**
     * Main proccessing.
     * @param webhookRequest webhookRequest
     * @return true/false
     */
    Boolean process(TgWebhookRequest webhookRequest);

}
