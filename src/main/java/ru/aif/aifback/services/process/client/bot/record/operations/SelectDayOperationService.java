package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_TIMES;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.DELIMITER_CHAR;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_SELECT_TIME_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_DAY;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_MONTH;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_TIME;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.formatTimeCalendar;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * Select day operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelectDayOperationService implements ClientBotOperationService {

    private final UserCalendarService userCalendarService;
    private final UserItemService userItemService;
    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String day = params[1];
        String month = params[2];
        String year = params[3];
        String itemId = params[4];
        String recordId = params[5];

        return processBotCalendarTimes(Long.valueOf(itemId),
                                       Long.valueOf(webhookRequest.getId()),
                                       Long.valueOf(year),
                                       Long.valueOf(month),
                                       Long.valueOf(day),
                                       recordId,
                                       webhookRequest.getSource(),
                                       webhookRequest.getChatId(),
                                       webhookRequest.getMessageId());
    }

    /**
     * Process select times.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param day day
     * @param recordId record id
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @return messages
     */
    private List<ChatMessage> processBotCalendarTimes(Long userItemId, Long id, Long year, Long month, Long day, String recordId,
                                                      String source, String chatId, String messageId) {
        List<UserCalendar> calendars = userCalendarService.findAllDaysByMonthAndYearAndDay(year, month, day, id);
        if (calendars.isEmpty()) {
            return fillEmptyMessages(source, chatId, messageId, month, year, userItemId, recordId);
        }

        Optional<UserItem> userItem = userItemService.findUserItemById(userItemId);
        if (userItem.isEmpty()) {
            return fillEmptyMessages(source, chatId, messageId, month, year, userItemId, recordId);
        }

        Map<String, List<ClientRecordTime>> times = new HashMap<>();
        for (UserCalendar calendar : calendars) {
            List<ClientRecord> records = clientRecordService.findAllRecordsByStaffAndDayAndStatus(
                    calendar.getAifUserStaffId(), calendar.getId(), id, ClientRecordType.ACTIVE.getType());

            List<ClientRecordTime> timesList = formatTimeCalendar(calendar, userItem.get(), userItemService.getMinUserItem(id), records);
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
            return fillEmptyMessages(source, chatId, messageId, month, year, userItemId, recordId);
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        List<ChatMessage.Button> row = new ArrayList<>();
        int num = 0;
        for (Map.Entry<String, List<ClientRecordTime>> entry : times.entrySet()
                                                                    .stream()
                                                                    .sorted(Comparator.comparingInt(o -> o.getValue().get(0).getHours()))
                                                                    .toList()) {
            if (entry.getValue().size() == 1) {
                row.add(ChatMessage.Button.builder()
                                          .title(entry.getKey())
                                          .callback(String.format("%s;%s;%s;%s;%s;%s;%s",
                                                                  BOT_CONFIRM_SELECT_TIME.getType(),
                                                                  entry.getValue().get(0).getCalendarId(),
                                                                  entry.getValue().get(0).getHours(),
                                                                  entry.getValue().get(0).getMins(),
                                                                  userItemId,
                                                                  entry.getValue().get(0).getStaffId(),
                                                                  recordId))
                                          .build());
            } else {
                String listCalendarIds = Strings.join(entry.getValue().stream().map(ClientRecordTime::getCalendarId).toList(),
                                                      DELIMITER_CHAR.charAt(0));
                row.add(ChatMessage.Button.builder()
                                          .title(entry.getKey())
                                          .callback(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s",
                                                                  BOT_SELECT_TIME.getType(),
                                                                  listCalendarIds,
                                                                  entry.getValue().get(0).getHours(),
                                                                  entry.getValue().get(0).getMins(),
                                                                  userItemId,
                                                                  day,
                                                                  month,
                                                                  year,
                                                                  recordId))
                                          .build());
            }

            if (++num % COLUMNS_TIMES == 0) {
                buttons.add(row);
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            buttons.add(row);
        }

        buttons.add(createBackButton(String.format("%s;%s;%s;%s;%s",
                                                   BOT_SELECT_MONTH.getType(),
                                                   month,
                                                   year,
                                                   userItemId,
                                                   recordId)));
        return List.of(ChatMessage.builder()
                                  .text(String.format(CALENDAR_SELECT_TIME_TITLE, getDayOfWeek(day, month, year), day, getMonthByNumber(month), year))
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
                                                Long itemId, String recordId) {
        return List.of(ChatMessage.builder()
                                  .text(CALENDAR_EMPTY_TIME_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(String.format("%s;%s;%s;%s;%s",
                                                                                  BOT_SELECT_MONTH.getType(),
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
        return BOT_SELECT_DAY;
    }
}
