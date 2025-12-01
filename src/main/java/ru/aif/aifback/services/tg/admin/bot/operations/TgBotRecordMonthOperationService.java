package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.NameWithCount;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.admin.TgAdminBotOperationService;
import ru.aif.aifback.services.tg.enums.TgAdminBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;

/**
 * TG Admin Bot record month operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotRecordMonthOperationService implements TgAdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String[] params = webhookRequest.getText().split(DELIMITER);
        String year = params[1];
        String userBotId = params[2];
        TgClientRecordType type = TgClientRecordType.findByType(params[3]);
        String answer = String.format("\uD83D\uDCC5 %s", year);

        List<NameWithCount> months = clientRecordService.findMonthsRecordsByStatus(Long.valueOf(userBotId), Long.valueOf(year), type.getType());
        if (months.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            int num = 0;

            for (NameWithCount month : months) {
                InlineKeyboardButton btn = new InlineKeyboardButton(String.format("%s (\uD83D\uDCE6 %s)",
                                                                                  getMonthByNumber(Long.valueOf(month.getName())),
                                                                                  month.getCount()))
                        .callbackData(String.format("%s;%s;%s;%s;%s", BOT_RECORD_DAY.getType(), month.getName(), year, userBotId, type.getType()));
                btns.add(btn);

                num++;

                if (num % 4 == 0) {
                    keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                    btns.clear();
                }
            }

            if (!btns.isEmpty()) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
            }
        }

        keyboard.addRow(createBackButton(String.format("%s;%s;%s", BOT_RECORD_YEAR.getType(), type.getType(), userBotId)));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_RECORD_MONTH;
    }
}
