package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_TIME_TITLE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Select day operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgSelectDayOperationService implements TgClientBotOperationService {

    private final UserCalendarService userCalendarService;
    private final UserItemService userItemService;
    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String day = webhookRequest.getText().split(DELIMITER)[1];
        String month = webhookRequest.getText().split(DELIMITER)[2];
        String year = webhookRequest.getText().split(DELIMITER)[3];
        String itemId = webhookRequest.getText().split(DELIMITER)[4];
        String answer = processBotCalendarTimes(Long.valueOf(itemId),
                                                Long.valueOf(webhookRequest.getId()),
                                                Long.valueOf(year),
                                                Long.valueOf(month),
                                                Long.valueOf(day),
                                                keyboard);
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s;%s",
                                                                                TgClientRecordBotOperationType.BOT_SELECT_MONTH.getType(),
                                                                                month,
                                                                                year,
                                                                                itemId)));

        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Process select times.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param day day
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotCalendarTimes(Long userItemId, Long id, Long year, Long month, Long day, InlineKeyboardMarkup keyboard) {
        List<UserCalendar> calendars = userCalendarService.findAllDaysByMonthAndYearAndDay(year, month, day, id);
        if (calendars.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        Optional<UserItem> userItem = userItemService.findUserItemById(userItemId);
        if (userItem.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        Map<String, List<ClientRecordTime>> times = new HashMap<>();
        for (UserCalendar calendar : calendars) {
            List<ClientRecord> records = clientRecordService.findAllRecordsByStaffAndDayAndStatus(
                    calendar.getAifUserStaffId(), calendar.getId(), id, TgClientRecordType.ACTIVE.getType());

            List<ClientRecordTime> timesList = TgUtils.formatTimeCalendar(calendar, userItem.get(), userItemService.getMinUserItem(id), records);
            if (timesList.isEmpty()) {
                continue;
            }

            timesList.forEach(time -> {
                String key = String.format("%02d:%02d", time.getHours(), time.getMins());

                if (!times.containsKey(key)) {
                    times.put(key, new ArrayList<>());
                }
                times.get(key).add(time);
            });
        }

        if (times.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        for (Map.Entry<String, List<ClientRecordTime>> entry : times.entrySet()
                                                                    .stream()
                                                                    .sorted(Comparator.comparingInt(o -> o.getValue().get(0).getHours()))
                                                                    .toList()) {
            if (entry.getValue().size() == 1) {
                btns.add(new InlineKeyboardButton(entry.getKey()).callbackData(String.format("%s;%s;%s;%s;%s;%s",
                                                                                             TgClientRecordBotOperationType.BOT_CONFIRM_SELECT_TIME.getType(),
                                                                                             entry.getValue().get(0).getCalendarId(),
                                                                                             entry.getValue().get(0).getHours(),
                                                                                             entry.getValue().get(0).getMins(),
                                                                                             userItemId,
                                                                                             entry.getValue().get(0).getStaffId())));
            } else {
                String listCalendarIds = Strings.join(entry.getValue().stream().map(ClientRecordTime::getCalendarId).toList(),
                                                      Constants.DELIMITER_CHAR.charAt(0));
                btns.add(new InlineKeyboardButton(entry.getKey()).callbackData(String.format("%s;%s;%s;%s;%s;%s;%s;%s",
                                                                                             TgClientRecordBotOperationType.BOT_SELECT_TIME.getType(),
                                                                                             listCalendarIds,
                                                                                             entry.getValue().get(0).getHours(),
                                                                                             entry.getValue().get(0).getMins(),
                                                                                             userItemId,
                                                                                             day,
                                                                                             month,
                                                                                             year)));
            }

            if (++num % 5 == 0) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                btns.clear();
            }
        }

        if (!btns.isEmpty()) {
            keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
        }

        return String.format(CALENDAR_SELECT_TIME_TITLE, TgUtils.getDayOfWeek(day, month, year), day, TgUtils.getMonthByNumber(month), year);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_SELECT_DAY;
    }
}
