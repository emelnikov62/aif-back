package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORDS_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.CANCEL;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.FINISHED;
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
import ru.aif.aifback.services.tg.enums.TgClientRecordType;

/**
 * TG Admin Bot records operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotRecordsOperationService implements TgAdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String userBotId = webhookRequest.getText().split(DELIMITER)[1];

        Function<TgClientRecordType, String> names = (type) -> String.format("%s %s", type.getIcon(), type.getNames());
        BiFunction<TgClientRecordType, String, String> callbackData = (type, id) ->
                String.format("%s;%s;%s", BOT_RECORD_YEAR.getType(), type.getType(), id);

        keyboard.addRow(new InlineKeyboardButton(names.apply(ACTIVE)).callbackData(callbackData.apply(ACTIVE, userBotId)));
        keyboard.addRow(new InlineKeyboardButton(names.apply(CANCEL)).callbackData(callbackData.apply(CANCEL, userBotId)));
        keyboard.addRow(new InlineKeyboardButton(names.apply(FINISHED)).callbackData(callbackData.apply(FINISHED, userBotId)));

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_SELECT.getType(), userBotId)));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), BOT_RECORDS_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_RECORDS;
    }
}
