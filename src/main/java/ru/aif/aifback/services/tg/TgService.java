package ru.aif.aifback.services.tg;

import ru.aif.aifback.model.requests.TgWebhookRequest;

/**
 * Description.
 * @author emelnikov
 */
public interface TgService {

    Boolean process(TgWebhookRequest webhookRequest);

    void processCallback(TgWebhookRequest webhookRequest);

    void processNoCallback(TgWebhookRequest webhookRequest);

}
