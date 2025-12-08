package ru.aif.aifback.services.process.admin.bot.tg;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.enums.BotSource.TELEGRAM;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createMainMenuKeyboard;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.bot.AdminBotProcessService;
import ru.aif.aifback.services.process.sender.tg.TgSenderService;

/**
 * TG Admin bot process API service.
 * @author emelnikov
 */
@Slf4j
@Service
public class TgAdminBotProcessService extends AdminBotProcessService {

    private final TgSenderService senderService;

    public TgAdminBotProcessService(List<AdminBotOperationService> operations, TgSenderService senderService) {
        super(operations);
        this.senderService = senderService;
    }

    /**
     * Send error message.
     * @param error error
     * @param webhookRequest webhook request
     */
    @Override
    public void sendErrorToLog(String error, WebhookRequest webhookRequest) {
        BotSource source = findByType(webhookRequest.getSource());

        sendMessages(List.of(ChatMessage.builder()
                                        .text(error)
                                        .updated(FALSE)
                                        .source(findByType(webhookRequest.getSource()))
                                        .chatId(TG_LOG_ID)
                                        .build(),
                             ChatMessage.builder()
                                        .text(MENU_TITLE)
                                        .updated(TRUE)
                                        .source(source)
                                        .chatId(webhookRequest.getChatId())
                                        .messageId(webhookRequest.getMessageId())
                                        .buttons(createMainMenuKeyboard())
                                        .build()));
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(WebhookRequest webhookRequest, UserBot userBot) {
        sendMessages(List.of(ChatMessage.builder()
                                        .text(MENU_TITLE)
                                        .updated(TRUE)
                                        .chatId(webhookRequest.getChatId())
                                        .messageId(webhookRequest.getMessageId())
                                        .buttons(createMainMenuKeyboard())
                                        .build()));
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
     */
    @Override
    public void sendMessages(List<ChatMessage> messages) {
        TelegramBot bot = new TelegramBot(TG_TOKEN_ADMIN);

        messages.forEach(message -> {
            message.setTelegramBot(bot);
            senderService.sendMessage(message);
        });
    }

}
