package ru.aif.aifback.services.process.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.SUCCESS_CANCEL_RECORD;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.process.client.enums.ClientRecordEventType.CANCEL;
import static ru.aif.aifback.services.utils.CommonUtils.sendMessage;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.client.enums.ClientRecordType;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.admin.notification.TgAdminNotificationService;
import ru.aif.aifback.services.process.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType;

/**
 * TG Record cancel operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordCancelOperationService implements TgClientBotOperationService {

    private final ClientRecordService clientRecordService;
    private final TgAdminNotificationService tgAdminNotificationService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(WebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        Long recordId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String answer = SUCCESS_CANCEL_RECORD;
        if (!clientRecordService.cancelRecord(recordId)) {
            answer = SHOW_ERROR_TITLE;
        }

        tgAdminNotificationService.recordNotification(userBot, recordId, null, CANCEL);

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), ClientRecordType.ACTIVE.getType())));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientRecordBotOperationType getOperationType() {
        return BOT_RECORD_CANCEL;
    }
}
