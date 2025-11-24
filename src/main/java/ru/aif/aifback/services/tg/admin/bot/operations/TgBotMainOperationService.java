package ru.aif.aifback.services.tg.admin.bot.operations;

import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createMainMenuKeyboard;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_MAIN;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;

/**
 * TG Admin Main operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotMainOperationService implements TgAdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE, createMainMenuKeyboard(), bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_MAIN;
    }
}
