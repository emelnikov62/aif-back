package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_TIMES;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_SELECT_DAY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_DAY;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_MONTH;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_YEAR;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.user.UserCalendarService;

/**
 * Select month operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelectMonthOperationService implements ClientBotOperationService {

    private final UserCalendarService userCalendarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String month = params[1];
        String year = params[2];
        String itemId = params[3];
        String recordId = params[4];

        return processBotCalendarDays(Long.valueOf(itemId),
                                      Long.valueOf(webhookRequest.getId()),
                                      Long.valueOf(year),
                                      Long.valueOf(month),
                                      recordId,
                                      webhookRequest.getSource(),
                                      webhookRequest.getChatId(),
                                      webhookRequest.getMessageId());
    }

    /**
     * Process select days.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param recordId record id
     * @return messages
     */
    private List<ChatMessage> processBotCalendarDays(Long userItemId, Long id, Long year, Long month, String recordId,
                                                     String source, String chatId, String messageId) {
        List<Long> days = userCalendarService.findAllDaysByMonthAndYear(year, month, id);
        if (days.isEmpty()) {
            return List.of(ChatMessage.builder()
                                      .text(CALENDAR_EMPTY_TIME_TITLE)
                                      .updated(TRUE)
                                      .source(findByType(source))
                                      .chatId(chatId)
                                      .messageId(messageId)
                                      .buttons(List.of(createBackButton(
                                              String.format("%s;%s;%s;%s", BOT_SELECT_YEAR.getType(), year, userItemId, recordId))))
                                      .build());
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        List<ChatMessage.Button> row = new ArrayList<>();
        int num = 0;
        while (num < days.size()) {
            String title = String.format("%02d (%s)", days.get(num), getDayOfWeek(days.get(num), month, year));
            row.add(ChatMessage.Button.builder()
                                      .title(title)
                                      .callback(String.format("%s;%s;%s;%s;%s;%s",
                                                              BOT_SELECT_DAY.getType(),
                                                              days.get(num),
                                                              month,
                                                              year,
                                                              userItemId,
                                                              recordId))
                                      .build());

            num++;
            if (num % COLUMNS_TIMES == 0) {
                buttons.add(row);
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            buttons.add(row);
        }

        buttons.add(createBackButton(String.format("%s;%s;%s;%s", BOT_SELECT_YEAR.getType(), year, userItemId, recordId)));

        return List.of(ChatMessage.builder()
                                  .text(String.format(CALENDAR_SELECT_DAY_TITLE, getMonthByNumber(month), year))
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_SELECT_MONTH;
    }
}
