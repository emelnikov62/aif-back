package ru.aif.aifback.services.process.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.createMainMenuKeyboard;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.utils.CommonUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.utils.CommonUtils;
import ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType;

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
    public void process(WebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        CommonUtils.sendMessage(webhookRequest.getChatId(),
                                Integer.parseInt(webhookRequest.getMessageId()),
                                MENU_TITLE,
                                createMainMenuKeyboard(userBot.getBot().getType()),
                                bot,
                                TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientRecordBotOperationType getOperationType() {
        return BOT_MAIN;
    }
}
