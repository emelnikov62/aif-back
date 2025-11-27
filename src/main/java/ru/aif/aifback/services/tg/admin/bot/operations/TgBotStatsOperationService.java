package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_STATS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_STATS_SELECT;
import static ru.aif.aifback.services.tg.enums.TgAdminStatsType.ALL;
import static ru.aif.aifback.services.tg.enums.TgAdminStatsType.MONTH;
import static ru.aif.aifback.services.tg.enums.TgAdminStatsType.YEAR;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgAdminStatsType;

/**
 * TG Admin Bot stats operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotStatsOperationService implements TgAdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String userBotId = webhookRequest.getText().split(DELIMITER)[1];

        Function<TgAdminStatsType, String> statsName = (type) -> String.format("%s %s", type.getIcon(), type.getName());
        BiFunction<TgAdminStatsType, String, String> callbackData = (type, id) ->
                String.format("%s;%s;%s", BOT_STATS_SELECT.getType(), type.getType(), id);

        keyboard.addRow(
                new InlineKeyboardButton(statsName.apply(MONTH)).callbackData(callbackData.apply(MONTH, userBotId)),
                new InlineKeyboardButton(statsName.apply(YEAR)).callbackData(callbackData.apply(YEAR, userBotId)),
                new InlineKeyboardButton(statsName.apply(ALL)).callbackData(callbackData.apply(ALL, userBotId)));

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_SELECT.getType(), userBotId)));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), BOT_STATS_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_STATS;
    }
}
