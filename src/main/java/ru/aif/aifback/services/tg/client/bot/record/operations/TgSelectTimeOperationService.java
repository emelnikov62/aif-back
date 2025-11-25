package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.DELIMITER_CHAR;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.STAFF_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.STAFF_SELECT_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_SELECT_DAY;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_SELECT_TIME;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

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
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
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

        String[] params = webhookRequest.getText().split(DELIMITER);
        String hours = params[2];
        String mins = params[3];
        String calendarIds = params[1];
        String itemId = params[4];
        String day = params[5];
        String month = params[6];
        String year = params[7];
        String recordId = params[8];
        String answer = processBotSelectStaff(Long.valueOf(day),
                                              Long.valueOf(month),
                                              Long.valueOf(year),
                                              Long.valueOf(hours),
                                              Long.valueOf(mins),
                                              Long.valueOf(itemId),
                                              calendarIds,
                                              recordId,
                                              keyboard);

        keyboard.addRow(createBackButton(String.format("%s;%s;%s;%s;%s;%s", BOT_SELECT_DAY.getType(), day, month, year, itemId, recordId)));
        sendMessage(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), answer, keyboard, bot, TRUE);
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
     * @param recordId record id
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotSelectStaff(Long day, Long month, Long year, Long hours, Long mins, Long itemId, String calendarIds,
                                         String recordId, InlineKeyboardMarkup keyboard) {
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
            keyboard.addRow(new InlineKeyboardButton(staffFio).callbackData(String.format("%s;%s;%s;%s;%s;%s;%s",
                                                                                          BOT_CONFIRM_SELECT_TIME.getType(),
                                                                                          userCalendar.get().getId(),
                                                                                          hours,
                                                                                          mins,
                                                                                          itemId,
                                                                                          userStaff.getId(),
                                                                                          recordId)));
        }

        return keyboard.inlineKeyboard().length == 0
               ? STAFF_EMPTY_TITLE
               : String.format(STAFF_SELECT_TITLE, getDayOfWeek(day, month, year), hours, mins, day, month, year);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_SELECT_TIME;
    }
}
