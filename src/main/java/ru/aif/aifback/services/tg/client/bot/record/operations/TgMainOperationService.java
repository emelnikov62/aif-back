package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;

/**
 * TG Main operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgMainOperationService implements TgClientBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()),
                            MENU_TITLE,
                            TgClientBotRecordButtons.createMainMenuKeyboard(userBot.getBot().getType()),
                            bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_MAIN;
    }
}
