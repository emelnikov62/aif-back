package ru.aif.aifback.services.tg;

import com.pengrad.telegrambot.TelegramBot;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;

/**
 * Common TG Bot operation interface.
 * @author emelnikov
 */
public interface TgBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot);

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    TgClientRecordBotOperationType getOperationType();

}
