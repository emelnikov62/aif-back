package ru.aif.aifback.services.process.client.bot;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.enums.BotType.BOT_RECORD;

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
import ru.aif.aifback.services.process.client.ClientBotOperationService;

/**
 * Client API bot record process service.
 * @author emelnikov
 */
@Slf4j
@AllArgsConstructor
public abstract class ClientBotProcessService implements BotProcessService {

    private final List<ClientBotOperationService> operations;

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
            ClientBotOperationService operation = operations.stream()
                                                            .filter(f -> webhookRequest.getText().contains(f.getOperationType().getType()))
                                                            .findFirst()
                                                            .orElse(null);
            if (Objects.isNull(operation)) {
                throw new Exception(Strings.EMPTY);
            }

            sendMessages(operation.process(webhookRequest, userBot), userBot);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendErrorToLog(e.getMessage(), webhookRequest, userBot);
        }
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public BotType getBotType() {
        return BOT_RECORD;
    }

    /**
     * Get list operations.
     * @return operations
     */
    public List<ClientBotOperationService> getOperations() {
        return operations;
    }

    /**
     * Send error message to log
     * @param error error
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    public abstract void sendErrorToLog(String error, WebhookRequest webhookRequest, UserBot userBot);

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
     * @param userBot user bot
     */
    public abstract void sendMessages(List<ChatMessage> messages, UserBot userBot);

}
