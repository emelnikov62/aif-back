package ru.aif.aifback.services.ai.recognize;

import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;

/**
 * Voice recognize service.
 * @author emelnikov
 */
public interface VoiceRecognizeService {

    /**
     * Recognize input voice.
     * @param webhookRequest request
     * @param userBot user bot
     * @return text
     */
    String recognize(WebhookRequest webhookRequest, UserBot userBot);
}
