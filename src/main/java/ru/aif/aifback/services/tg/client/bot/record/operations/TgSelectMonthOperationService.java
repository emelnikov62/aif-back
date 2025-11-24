package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_DAY_TITLE;

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
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserCalendarService;

/**
 * TG Select month operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgSelectMonthOperationService implements TgClientBotOperationService {

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

        String month = webhookRequest.getText().split(DELIMITER)[1];
        String year = webhookRequest.getText().split(DELIMITER)[2];
        String itemId = webhookRequest.getText().split(DELIMITER)[3];
        String answer = processBotCalendarDays(Long.valueOf(itemId),
                                               Long.valueOf(webhookRequest.getId()),
                                               Long.valueOf(year),
                                               Long.valueOf(month),
                                               keyboard);
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s",
                                                                                TgClientRecordBotOperationType.BOT_SELECT_YEAR.getType(),
                                                                                year,
                                                                                itemId)));

        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Process select days.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param keyboard keyboard
     */
    private String processBotCalendarDays(Long userItemId, Long id, Long year, Long month, InlineKeyboardMarkup keyboard) {
        List<Long> days = userCalendarService.findAllDaysByMonthAndYear(year, month, id);
        if (days.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        while (num < days.size()) {
            String title = String.format("%s (%s)", days.get(num), TgUtils.getDayOfWeek(days.get(num), month, year));
            InlineKeyboardButton btn = new InlineKeyboardButton(title).callbackData(
                    String.format("%s;%s;%s;%s;%s", TgClientRecordBotOperationType.BOT_SELECT_DAY.getType(), days.get(num), month, year, userItemId));
            btns.add(btn);

            num++;

            if (num % 5 == 0) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                btns.clear();
            }
        }

        if (!btns.isEmpty()) {
            keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
        }

        return String.format(CALENDAR_SELECT_DAY_TITLE, TgUtils.getMonthByNumber(month), year);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_SELECT_MONTH;
    }
}
