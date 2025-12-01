package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_YEAR;
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
 * TG Admin Bot record year operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotRecordYearOperationService implements TgAdminBotOperationService {

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
        TgClientRecordType type = TgClientRecordType.findByType(params[1]);
        String userBotId = params[2];
        String answer = String.format("%s %s", type.getIcon(), type.getNames());

        List<NameWithCount> years = clientRecordService.findYearsRecordsByStatus(Long.valueOf(userBotId), type.getType());
        if (years.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            int num = 0;

            for (NameWithCount year : years) {
                InlineKeyboardButton btn = new InlineKeyboardButton(String.format("%s (\uD83D\uDCDD %s)", year.getName(), year.getCount()))
                        .callbackData(String.format("%s;%s;%s;%s", BOT_RECORD_MONTH.getType(), year.getName(), userBotId, type.getType()));
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

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), userBotId)));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_RECORD_YEAR;
    }
}
