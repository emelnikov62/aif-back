package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SUCCESS_CLIENT_STAR;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_CLIENT_STAR;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.NO_ACTIVE;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientStarService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;

/**
 * TG Client star operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgClientStarOperationService implements TgClientBotOperationService {

    private final ClientRecordService clientRecordService;
    private final ClientStarService clientStarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String[] params = webhookRequest.getText().split(DELIMITER);
        String star = params[1];
        String recordId = params[2];

        ClientRecord clientRecord = clientRecordService.getClientRecordById(Long.valueOf(recordId));
        if (Objects.isNull(clientRecord)) {
            sendErrorMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), recordId, keyboard, bot);
            return;
        }

        if (Objects.isNull(clientStarService.addClientStar(clientRecord.getAifClientId(),
                                                           clientRecord.getAifUserBotId(),
                                                           clientRecord.getAifUserItemId(),
                                                           clientRecord.getAifUserStaffId(),
                                                           Long.valueOf(star)))) {
            sendErrorMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), recordId, keyboard, bot);
            return;
        }

        keyboard.addRow(createBackButton(String.format("%s;%s;%s", BOT_RECORD_SHOW.getType(), recordId, NO_ACTIVE.getType())));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), SUCCESS_CLIENT_STAR, keyboard, bot, TRUE);
    }

    /**
     * Send error message.
     * @param chatId chat id
     * @param messageId message id
     * @param recordId record id
     * @param keyboard keyboard
     * @param bot bot
     */
    private void sendErrorMessage(String chatId, int messageId, String recordId, InlineKeyboardMarkup keyboard, TelegramBot bot) {
        keyboard.addRow(createBackButton(String.format("%s;%s;%s", BOT_RECORD_SHOW, recordId, NO_ACTIVE.getType())));
        sendMessage(chatId, messageId, SHOW_ERROR_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_CLIENT_STAR;
    }
}
