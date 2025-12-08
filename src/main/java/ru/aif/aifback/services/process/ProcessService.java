package ru.aif.aifback.services.process;

import ru.aif.aifback.model.requests.WebhookRequest;

/**
 * Common process interface.
 * @author emelnikov
 */
public interface ProcessService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return true/false
     */
    Boolean process(WebhookRequest webhookRequest);

}
