package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_YEAR_TITLE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_ITEM_ADDITIONAL;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_SELECT_YEAR;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;

/**
 * TG Add record operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAddRecordOperationService implements TgClientBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String itemId = webhookRequest.getText().split(DELIMITER)[1];
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int nextYear = currentYear + 1;

        keyboard.addRow(new InlineKeyboardButton(String.valueOf(currentYear)).callbackData(
                                String.format("%s;%s;%s", BOT_SELECT_YEAR.getType(), currentYear, itemId)),
                        new InlineKeyboardButton(String.valueOf(nextYear)).callbackData(
                                String.format("%s;%s;%s", BOT_SELECT_YEAR.getType(), nextYear, itemId)));
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s", BOT_ITEM_ADDITIONAL.getType(), itemId)));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), CALENDAR_SELECT_YEAR_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_ADD_RECORD;
    }
}
