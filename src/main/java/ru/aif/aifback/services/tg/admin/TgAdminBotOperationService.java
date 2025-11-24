package ru.aif.aifback.services.tg.admin;

import com.pengrad.telegrambot.TelegramBot;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;

/**
 * Common TG Admin Bot operation interface.
 * @author emelnikov
 */
public interface TgAdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    void process(TgWebhookRequest webhookRequest, TelegramBot bot);

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    TgAdminBotOperationType getOperationType();

}
