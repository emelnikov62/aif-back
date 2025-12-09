package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.DELIMITER_CHAR;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.STAFF_EMPTY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.STAFF_SELECT_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_DAY;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_TIME;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserStaff;
import ru.aif.aifback.services.client.ClientStarService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.user.UserCalendarService;

/**
 * Select time operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelectTimeOperationService implements ClientBotOperationService {

    private final UserCalendarService userCalendarService;
    private final ClientStarService clientStarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String hours = params[2];
        String mins = params[3];
        String calendarIds = params[1];
        String itemId = params[4];
        String day = params[5];
        String month = params[6];
        String year = params[7];
        String recordId = params[8];

        return processBotSelectStaff(Long.valueOf(webhookRequest.getId()),
                                     Long.valueOf(day),
                                     Long.valueOf(month),
                                     Long.valueOf(year),
                                     Long.valueOf(hours),
                                     Long.valueOf(mins),
                                     Long.valueOf(itemId),
                                     calendarIds,
                                     recordId,
                                     webhookRequest.getSource(),
                                     webhookRequest.getChatId(),
                                     webhookRequest.getMessageId());
    }

    /**
     * Process select staff.
     * @param userBotId user bot id
     * @param day day
     * @param month month
     * @param year year
     * @param hours hours
     * @param mins mins
     * @param itemId itemId
     * @param calendarIds calendar ids
     * @param recordId record id
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @return messages
     */
    private List<ChatMessage> processBotSelectStaff(Long userBotId, Long day, Long month, Long year, Long hours, Long mins, Long itemId,
                                                    String calendarIds, String recordId, String source, String chatId, String messageId) {
        List<String> stringCalendarIds = Arrays.stream(calendarIds.split(DELIMITER_CHAR)).toList();
        if (stringCalendarIds.isEmpty()) {
            return fillEmptyMessages(source, chatId, messageId, month, year, itemId, recordId, day);
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        for (String calendarId : stringCalendarIds) {
            UserCalendar userCalendar = userCalendarService.findById(Long.valueOf(calendarId)).orElse(null);
            if (Objects.isNull(userCalendar)) {
                continue;
            }

            if (Objects.isNull(userCalendar.getStaff())) {
                continue;
            }

            UserStaff userStaff = userCalendar.getStaff();
            Float calcStar = clientStarService.calcByStaffAndUserItem(userBotId, userStaff.getId(), itemId);
            String staffFio = String.format("%s %s %s (⭐ %.2f)", userStaff.getSurname(), userStaff.getName(), userStaff.getThird(), calcStar);
            buttons.add(List.of(ChatMessage.Button.builder()
                                                  .title(staffFio)
                                                  .callback(String.format("%s;%s;%s;%s;%s;%s;%s",
                                                                          BOT_CONFIRM_SELECT_TIME.getType(),
                                                                          userCalendar.getId(),
                                                                          hours,
                                                                          mins,
                                                                          itemId,
                                                                          userStaff.getId(),
                                                                          recordId))
                                                  .build()));
        }

        if (buttons.isEmpty()) {
            return fillEmptyMessages(source, chatId, messageId, month, year, itemId, recordId, day);
        }

        buttons.add(createBackButton(String.format("%s;%s;%s;%s;%s;%s",
                                                   BOT_SELECT_DAY.getType(),
                                                   day,
                                                   month,
                                                   year,
                                                   itemId,
                                                   recordId)));

        return List.of(ChatMessage.builder()
                                  .text(String.format(STAFF_SELECT_TITLE, getDayOfWeek(day, month, year), hours, mins, day, month, year))
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Fill empty messages.
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @param month month
     * @param year year
     * @param itemId item id
     * @param recordId record id
     * @return messages
     */
    private List<ChatMessage> fillEmptyMessages(String source, String chatId, String messageId, Long month, Long year,
                                                Long itemId, String recordId, Long day) {
        return List.of(ChatMessage.builder()
                                  .text(STAFF_EMPTY_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(String.format("%s;%s;%s;%s;%s;%s",
                                                                                  BOT_SELECT_DAY.getType(),
                                                                                  day,
                                                                                  month,
                                                                                  year,
                                                                                  itemId,
                                                                                  recordId))))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_SELECT_TIME;
    }
}
