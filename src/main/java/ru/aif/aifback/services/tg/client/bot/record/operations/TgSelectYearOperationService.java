package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_MONTH_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_SELECT_MONTH;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_SELECT_YEAR;
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
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.user.UserCalendarService;

/**
 * TG Select year operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgSelectYearOperationService implements TgClientBotOperationService {

    private final UserCalendarService userCalendarService;

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
        String year = params[1];
        String itemId = params[2];
        String recordId = params[3];
        String answer = processBotCalendarMonths(Long.valueOf(itemId), Long.valueOf(webhookRequest.getId()), Long.valueOf(year), recordId, keyboard);
        keyboard.addRow(createBackButton(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), itemId, recordId)));

        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
    }

    /**
     * Process select month.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param recordId record id
     * @param keyboard keyboard
     */
    private String processBotCalendarMonths(Long userItemId, Long id, Long year, String recordId, InlineKeyboardMarkup keyboard) {
        List<Long> months = userCalendarService.findAllMonthsByYear(year, id);
        if (months.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        while (num < months.size()) {
            InlineKeyboardButton btn = new InlineKeyboardButton(getMonthByNumber(months.get(num)))
                    .callbackData(String.format("%s;%s;%s;%s;%s", BOT_SELECT_MONTH.getType(), months.get(num), year, userItemId, recordId));
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

        return String.format(CALENDAR_SELECT_MONTH_TITLE, year);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_SELECT_YEAR;
    }
}
