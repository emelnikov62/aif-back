package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOTS_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.getClientRecordInfo;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_SHOW_BY_DAY;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;

/**
 * TG Admin Bot record show by day operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotRecordShowByDayOperationService implements TgAdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String day = params[1];
        String month = params[2];
        String year = params[3];
        String userBotId = params[4];
        TgClientRecordType type = TgClientRecordType.findByType(params[5]);
        List<ClientRecord> records = clientRecordService.findByDate(Long.valueOf(day),
                                                                    Long.valueOf(month),
                                                                    Long.valueOf(year),
                                                                    Long.valueOf(userBotId),
                                                                    type.getType());
        if (records.isEmpty()) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.addRow(createBackButton(String.format("%s;%s;%s;%s;%s", BOT_RECORD_DAY.getType(), month, year, userBotId, type.getType())));
            sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), BOT_RECORDS_EMPTY, keyboard, bot, TRUE);
        } else {
            for (ClientRecord record : records) {
                String answer = getClientRecordInfo(record, type);
                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

                if (Objects.equals(record.getStatus(), ACTIVE.getType())) {
                    keyboard.addRow(new InlineKeyboardButton(BOTS_CANCEL_RECORD_TITLE).callbackData(
                            String.format("%s;%s;%s;%s;%s;%s", BOT_RECORD_CANCEL.getType(), month, year, userBotId, type.getType(), record.getId())));
                }

                keyboard.addRow(createBackButton(String.format("%s;%s;%s;%s;%s", BOT_RECORD_DAY.getType(), month, year, userBotId, type.getType())));
                sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, FALSE);
            }
        }
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_RECORD_SHOW_BY_DAY;
    }
}
