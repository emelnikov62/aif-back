package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_DAYS;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_SHOW_BY_DAY;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.findByType;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.NameWithCount;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.process.client.enums.ClientRecordType;

/**
 * Admin Bot record day operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordDayOperationService implements AdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String month = params[1];
        String year = params[2];
        String userBotId = params[3];
        ClientRecordType type = findByType(params[4]);
        String answer = String.format("\uD83D\uDCC5 %s", getMonthByNumber(Long.valueOf(month)));
        List<ChatMessage.Button> buttons = new ArrayList<>();

        List<NameWithCount> days = clientRecordService.findDaysRecordsByStatus(Long.valueOf(userBotId),
                                                                               Long.valueOf(year),
                                                                               Long.valueOf(month),
                                                                               type.getType());
        if (days.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            for (NameWithCount day : days) {
                buttons.add(ChatMessage.Button.builder()
                                              .title(String.format("%s %s (\uD83D\uDCDD %s)",
                                                                   getDayOfWeek(Long.valueOf(day.getName()),
                                                                                Long.valueOf(month),
                                                                                Long.valueOf(year)),
                                                                   day.getName(),
                                                                   day.getCount()))
                                              .callback(String.format("%s;%s;%s;%s;%s;%s",
                                                                      BOT_RECORD_SHOW_BY_DAY.getType(),
                                                                      day.getName(),
                                                                      month,
                                                                      year,
                                                                      userBotId,
                                                                      type.getType()))
                                              .isBack(FALSE)
                                              .build());
            }
        }

        buttons.addAll(createBackButton(String.format("%s;%s;%s;%s", BOT_RECORD_MONTH.getType(), year, userBotId, type.getType())));
        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(BotSource.findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .columns(COLUMNS_DAYS)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public AdminBotOperationType getOperationType() {
        return BOT_RECORD_DAY;
    }
}
