package ru.aif.aifback.services.process.client.bot.record.tg;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.NULL_PARAM;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.enums.BotSource.TELEGRAM;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_AI_RECORD_PROCESS;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createMainMenuKeyboard;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.ClientBotProcessService;
import ru.aif.aifback.services.process.sender.tg.TgSenderService;

/**
 * TG Client bot record process API service.
 * @author emelnikov
 */
@Slf4j
@Service
public class TgClientBotRecordProcessService extends ClientBotProcessService {

    private final TgSenderService senderService;

    public TgClientBotRecordProcessService(List<ClientBotOperationService> operations, TgSenderService senderService) {
        super(operations);
        this.senderService = senderService;
    }

    /**
     * Send error message.
     * @param error error
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void sendErrorToLog(String error, WebhookRequest webhookRequest, UserBot userBot) {
        BotSource source = findByType(webhookRequest.getSource());
        TelegramBot bot = new TelegramBot(userBot.getToken());

        sendMessages(List.of(ChatMessage.builder()
                                        .text(error)
                                        .updated(FALSE)
                                        .source(findByType(webhookRequest.getSource()))
                                        .chatId(TG_LOG_ID)
                                        .telegramBot(bot)
                                        .build(),
                             ChatMessage.builder()
                                        .text(MENU_TITLE)
                                        .updated(TRUE)
                                        .source(source)
                                        .chatId(webhookRequest.getChatId())
                                        .messageId(webhookRequest.getMessageId())
                                        .telegramBot(bot)
                                        .buttons(createMainMenuKeyboard(userBot.getBot().getType()))
                                        .build()),
                     userBot);
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(WebhookRequest webhookRequest, UserBot userBot) {
        if (Objects.nonNull(webhookRequest.getFileId()) && !Objects.equals(webhookRequest.getFileId(), NULL_PARAM)) {
            ClientBotOperationService aiOperation = getOperations().stream()
                                                                   .filter(f -> Objects.equals(f.getOperationType(), BOT_AI_RECORD_PROCESS))
                                                                   .findFirst()
                                                                   .orElse(null);
            if (Objects.nonNull(aiOperation)) {
                sendMessages(aiOperation.process(webhookRequest, userBot), userBot);
                return;
            }
        }

        sendMessages(List.of(ChatMessage.builder()
                                        .text(MENU_TITLE)
                                        .updated(TRUE)
                                        .chatId(webhookRequest.getChatId())
                                        .messageId(webhookRequest.getMessageId())
                                        .buttons(createMainMenuKeyboard(userBot.getBot().getType()))
                                        .telegramBot(new TelegramBot(userBot.getToken()))
                                        .build()),
                     userBot);
    }

    /**
     * Get bot source type.
     * @return bot source type
     */
    @Override
    public BotSource getSourceType() {
        return TELEGRAM;
    }

    /**
     * Send messages.
     * @param messages chat messages
     * @param userBot user bot
     */
    @Override
    public void sendMessages(List<ChatMessage> messages, UserBot userBot) {
        TelegramBot bot = new TelegramBot(userBot.getToken());

        messages.forEach(message -> {
            message.setTelegramBot(bot);
            senderService.sendMessage(message);
        });
    }

}
