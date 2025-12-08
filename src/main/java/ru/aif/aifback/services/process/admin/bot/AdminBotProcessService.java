package ru.aif.aifback.services.process.admin.bot;

import static java.lang.Boolean.TRUE;

import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.enums.BotType;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.BotProcessService;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;

/**
 * Admin bot process API service.
 * @author emelnikov
 */
@Slf4j
@AllArgsConstructor
public abstract class AdminBotProcessService implements BotProcessService {

    private final List<AdminBotOperationService> operations;

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @param userBot user bot
     * @return true/false
     */
    @Override
    public Boolean process(WebhookRequest webhookRequest, UserBot userBot) {
        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest, userBot);
        } else {
            processNoCallback(webhookRequest, userBot);
        }

        return TRUE;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processCallback(WebhookRequest webhookRequest, UserBot userBot) {
        try {
            AdminBotOperationService operation = operations.stream()
                                                           .filter(f -> webhookRequest.getText().contains(f.getOperationType().getType()))
                                                           .findFirst()
                                                           .orElse(null);
            if (Objects.isNull(operation)) {
                throw new Exception(Strings.EMPTY);
            }

            sendMessages(operation.process(webhookRequest));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendErrorToLog(e.getMessage(), webhookRequest);
        }
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public BotType getBotType() {
        return BotType.BOT_ADMIN;
    }

    /**
     * Send error message to log
     * @param error error
     * @param webhookRequest webhook request
     */
    public abstract void sendErrorToLog(String error, WebhookRequest webhookRequest);

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public abstract void processNoCallback(WebhookRequest webhookRequest, UserBot userBot);

    /**
     * Get bot source type.
     * @return bot source type
     */
    public abstract BotSource getSourceType();

    /**
     * Send messages to chat.
     * @param messages chat messages
     */
    public abstract void sendMessages(List<ChatMessage> messages);

}
