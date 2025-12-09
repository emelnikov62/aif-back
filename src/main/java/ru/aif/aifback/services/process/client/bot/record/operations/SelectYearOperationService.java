package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_SELECT_MONTH_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_MONTH;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_YEAR;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
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
 * Select year operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SelectYearOperationService implements ClientBotOperationService {

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
        String year = params[1];
        String itemId = params[2];
        String recordId = params[3];

        return processBotCalendarMonths(Long.valueOf(itemId),
                                        Long.valueOf(webhookRequest.getId()),
                                        Long.valueOf(year),
                                        recordId,
                                        webhookRequest.getSource(),
                                        webhookRequest.getChatId(),
                                        webhookRequest.getMessageId());
    }

    /**
     * Process select month.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param recordId record id
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @return messages
     */
    private List<ChatMessage> processBotCalendarMonths(Long userItemId, Long id, Long year, String recordId, String source, String chatId,
                                                       String messageId) {
        List<Long> months = userCalendarService.findAllMonthsByYear(year, id);
        if (months.isEmpty()) {
            return List.of(ChatMessage.builder()
                                      .text(CALENDAR_EMPTY_TIME_TITLE)
                                      .updated(TRUE)
                                      .source(findByType(source))
                                      .chatId(chatId)
                                      .messageId(messageId)
                                      .buttons(List.of(createBackButton(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItemId, recordId))))
                                      .build());
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        List<ChatMessage.Button> row = new ArrayList<>();
        int num = 0;
        while (num < months.size()) {
            row.add(ChatMessage.Button.builder()
                                      .title(getMonthByNumber(months.get(num)))
                                      .callback(String.format("%s;%s;%s;%s;%s",
                                                              BOT_SELECT_MONTH.getType(),
                                                              months.get(num),
                                                              year,
                                                              userItemId,
                                                              recordId))
                                      .build());

            num++;
            if (num % 4 == 0) {
                buttons.add(new ArrayList<>(row));
                row.clear();
            }
        }

        if (!row.isEmpty()) {
            buttons.add(new ArrayList<>(row));
            row.clear();
        }

        buttons.add(createBackButton(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItemId, recordId)));

        return List.of(ChatMessage.builder()
                                  .text(String.format(CALENDAR_SELECT_MONTH_TITLE, year))
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
        return BOT_SELECT_YEAR;
    }
}
