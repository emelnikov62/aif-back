package ru.aif.aifback.services.tg.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_RECORD_SHOW_BY_DAY;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
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
 * TG Admin Bot record day operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgBotRecordDayOperationService implements TgAdminBotOperationService {

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
        String month = params[1];
        String year = params[2];
        String userBotId = params[3];
        TgClientRecordType type = TgClientRecordType.findByType(params[4]);
        String answer = String.format("\uD83D\uDCC5 %s", getMonthByNumber(Long.valueOf(month)));

        List<NameWithCount> days = clientRecordService.findDaysRecordsByStatus(Long.valueOf(userBotId),
                                                                               Long.valueOf(year),
                                                                               Long.valueOf(month),
                                                                               type.getType());
        if (days.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            int num = 0;

            for (NameWithCount day : days) {
                InlineKeyboardButton btn = new InlineKeyboardButton(
                        String.format("%s %s (\uD83D\uDCDD %s)",
                                      getDayOfWeek(Long.valueOf(day.getName()), Long.valueOf(month), Long.valueOf(year)),
                                      day.getName(),
                                      day.getCount()))
                        .callbackData(String.format("%s;%s;%s;%s;%s;%s",
                                                    BOT_RECORD_SHOW_BY_DAY.getType(),
                                                    day.getName(),
                                                    month,
                                                    year,
                                                    userBotId,
                                                    type.getType()));
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

        keyboard.addRow(createBackButton(String.format("%s;%s;%s;%s", BOT_RECORD_MONTH.getType(), year, userBotId, type.getType())));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgAdminBotOperationType getOperationType() {
        return BOT_RECORD_DAY;
    }
}
