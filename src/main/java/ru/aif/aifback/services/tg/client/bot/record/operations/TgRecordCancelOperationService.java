package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SUCCESS_CANCEL_RECORD;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;

/**
 * TG Record cancel operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordCancelOperationService implements TgClientBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        Long recordId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String answer = SUCCESS_CANCEL_RECORD;
        if (!clientRecordService.cancelRecord(recordId)) {
            answer = SHOW_ERROR_TITLE;
        }

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), ACTIVE.getType())));
        sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_RECORD_CANCEL;
    }
}
