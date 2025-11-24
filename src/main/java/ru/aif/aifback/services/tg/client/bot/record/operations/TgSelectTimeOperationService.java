package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.DELIMITER_CHAR;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.STAFF_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.STAFF_SELECT_TITLE;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserStaff;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserCalendarService;

/**
 * TG Select time operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgSelectTimeOperationService implements TgClientBotOperationService {

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

        String hours = webhookRequest.getText().split(DELIMITER)[2];
        String mins = webhookRequest.getText().split(DELIMITER)[3];
        String calendarIds = webhookRequest.getText().split(DELIMITER)[1];
        String itemId = webhookRequest.getText().split(DELIMITER)[4];
        String day = webhookRequest.getText().split(DELIMITER)[5];
        String month = webhookRequest.getText().split(DELIMITER)[6];
        String year = webhookRequest.getText().split(DELIMITER)[7];
        String answer = processBotSelectStaff(Long.valueOf(day),
                                              Long.valueOf(month),
                                              Long.valueOf(year),
                                              Long.valueOf(hours),
                                              Long.valueOf(mins),
                                              Long.valueOf(itemId),
                                              calendarIds,
                                              keyboard);
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s;%s;%s",
                                                                                TgClientRecordBotOperationType.BOT_SELECT_DAY.getType(),
                                                                                day,
                                                                                month,
                                                                                year,
                                                                                itemId)));

        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Process select staff.
     * @param day day
     * @param month month
     * @param year year
     * @param hours hours
     * @param mins mins
     * @param itemId itemId
     * @param calendarIds calendar ids
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotSelectStaff(Long day, Long month, Long year, Long hours, Long mins, Long itemId, String calendarIds,
                                         InlineKeyboardMarkup keyboard) {
        List<String> stringCalendarIds = Arrays.stream(calendarIds.split(DELIMITER_CHAR)).toList();
        if (stringCalendarIds.isEmpty()) {
            return STAFF_EMPTY_TITLE;
        }

        for (String calendarId : stringCalendarIds) {
            Optional<UserCalendar> userCalendar = userCalendarService.findById(Long.valueOf(calendarId));
            if (userCalendar.isEmpty()) {
                continue;
            }

            if (Objects.isNull(userCalendar.get().getStaff())) {
                continue;
            }

            UserStaff userStaff = userCalendar.get().getStaff();
            String staffFio = String.format("%s %s %s", userStaff.getSurname(), userStaff.getName(), userStaff.getThird());
            keyboard.addRow(new InlineKeyboardButton(staffFio).callbackData(String.format("%s;%s;%s;%s;%s;%s",
                                                                                          TgClientRecordBotOperationType.BOT_CONFIRM_SELECT_TIME.getType(),
                                                                                          userCalendar.get().getId(),
                                                                                          hours,
                                                                                          mins,
                                                                                          itemId,
                                                                                          userStaff.getId())));
        }

        return keyboard.inlineKeyboard().length == 0
               ? STAFF_EMPTY_TITLE
               : String.format(STAFF_SELECT_TITLE, TgUtils.getDayOfWeek(day, month, year), hours, mins, day, month, year);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_SELECT_TIME;
    }
}
