package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.FALSE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOTS_ERROR_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOTS_SUCCES_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.CANCEL;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordNotificationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;

/**
 * TG Admin Bot record cancel operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotRecordCancelOperationService implements TgAdminBotOperationService {

    private final ClientRecordService clientRecordService;
    private final TgClientBotRecordNotificationService tgClientBotRecordNotificationService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String[] params = webhookRequest.getText().split(DELIMITER);
        String month = params[1];
        String year = params[2];
        String type = params[3];
        String userBotId = params[4];
        String recordId = params[5];

        String answer = BOTS_ERROR_CANCEL_RECORD_TITLE;
        if (clientRecordService.cancelRecord(Long.valueOf(recordId))) {
            answer = BOTS_SUCCES_CANCEL_RECORD_TITLE;

            ClientRecord clientRecord = clientRecordService.getClientRecordById(Long.valueOf(recordId));
            if (Objects.nonNull(clientRecord)) {
                tgClientBotRecordNotificationService.recordNotification(clientRecord, CANCEL);
            }
        }

        keyboard.addRow(createBackButton(String.format("%s;%s;%s;%s;%s", BOT_RECORD_DAY.getType(), month, year, userBotId, type)));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, FALSE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_RECORD_CANCEL;
    }
}
