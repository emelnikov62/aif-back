package ru.aif.aifback.services.tg.client.bot.record;

import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createMainMenuKeyboard;
import static ru.aif.aifback.services.tg.enums.TgBotType.BOT_RECORD;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.tg.TgBotService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgBotType;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordBotService implements TgBotService {

    private final List<TgClientBotOperationService> operations;
    private final UserItemService userItemService;
    private final UserCalendarService userCalendarService;
    private final ClientService clientService;
    private final ClientRecordService clientRecordService;

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

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        TelegramBot bot = new TelegramBot(userBot.getToken());
        try {
            TgClientBotOperationService operation = operations.stream()
                                                              .filter(f -> webhookRequest.getText().contains(f.getOperationType().getType()))
                                                              .findFirst()
                                                              .orElse(null);
            if (Objects.isNull(operation)) {
                sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE, createMainMenuKeyboard(userBot.getBot().getType()), bot);
                return;
            }

            operation.process(webhookRequest, userBot, bot);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(TG_LOG_ID, e.getMessage(), bot);
        }
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        TelegramBot bot = new TelegramBot(userBot.getToken());
        sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE, createMainMenuKeyboard(userBot.getBot().getType()), bot);
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public TgBotType getBotType() {
        return BOT_RECORD;
    }

}
