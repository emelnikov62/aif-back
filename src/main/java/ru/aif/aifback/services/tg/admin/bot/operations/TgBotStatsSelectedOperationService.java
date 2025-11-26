package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_STATS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS_SELECT;
import static ru.aif.aifback.services.tg.enums.TgAdminStatsType.findByType;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgAdminStatsType;

/**
 * TG Admin Bot stats selected operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotStatsSelectedOperationService implements TgAdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String[] params = webhookRequest.getText().split(DELIMITER);
        TgAdminStatsType type = findByType(params[1]);
        String userBotId = params[2];

        String answer = String.format("%s: %s \n\n", BOT_STATS_TITLE, type.getName());

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_STATS.getType(), userBotId)));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_STATS_SELECT;
    }
}
