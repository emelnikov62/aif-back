package ru.aif.aifback.services.tg.admin.bot;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.constants.Constants.TG_TOKEN_ADMIN;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createMainMenuKeyboard;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.TgBotService;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgBotType;

/**
 * TG Admin API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAdminBotService implements TgBotService {

    private final List<TgAdminBotOperationService> operations;
    private TelegramBot bot;

    /**
     * Post construct.
     */
    @PostConstruct
    void init() {
        bot = new TelegramBot(TG_TOKEN_ADMIN);
    }

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @param userBot user bot
     * @return true/false
     */
    @Override
    public Boolean process(TgWebhookRequest webhookRequest, UserBot userBot) {
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
    public void processCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        try {
            TgAdminBotOperationService operation = operations.stream()
                                                             .filter(f -> webhookRequest.getText().contains(f.getOperationType().getType()))
                                                             .findFirst()
                                                             .orElse(null);
            if (Objects.isNull(operation)) {
                sendMessage(webhookRequest.getChatId(),
                            Integer.parseInt(webhookRequest.getMessageId()),
                            MENU_TITLE,
                            createMainMenuKeyboard(),
                            bot,
                            FALSE);
                return;
            }

            operation.process(webhookRequest, bot);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(TG_LOG_ID, Integer.parseInt(webhookRequest.getMessageId()), e.getMessage(), bot, FALSE);
        }
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), MENU_TITLE, createMainMenuKeyboard(), bot, FALSE);
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public TgBotType getBotType() {
        return TgBotType.BOT_ADMIN;
    }

}
