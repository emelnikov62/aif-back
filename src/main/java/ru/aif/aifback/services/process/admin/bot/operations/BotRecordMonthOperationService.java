package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_MONTHS;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.findByType;
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
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;

/**
 * Admin Bot record month operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordMonthOperationService implements AdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String year = params[1];
        String userBotId = params[2];
        ClientRecordType type = findByType(params[3]);
        String answer = String.format("\uD83D\uDCC5 %s", year);
        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        List<NameWithCount> months = clientRecordService.findMonthsRecordsByStatus(Long.valueOf(userBotId), Long.valueOf(year), type.getType());
        if (months.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            List<ChatMessage.Button> row = new ArrayList<>();
            int num = 0;
            for (NameWithCount month : months) {
                row.add(ChatMessage.Button.builder()
                                          .title(String.format("%s (\uD83D\uDCDD %s)",
                                                               getMonthByNumber(Long.valueOf(month.getName())),
                                                               month.getCount()))
                                          .callback(String.format("%s;%s;%s;%s;%s",
                                                                  BOT_RECORD_DAY.getType(),
                                                                  month.getName(),
                                                                  year,
                                                                  userBotId,
                                                                  type.getType()))
                                          .build());

                num++;
                if (num % COLUMNS_MONTHS == 0) {
                    buttons.add(row);
                    row.clear();
                }
            }

            if (!row.isEmpty()) {
                buttons.add(row);
            }
        }

        buttons.add(createBackButton(String.format("%s;%s;%s", BOT_RECORD_YEAR.getType(), type.getType(), userBotId)));

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(BotSource.findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public AdminBotOperationType getOperationType() {
        return BOT_RECORD_MONTH;
    }
}
