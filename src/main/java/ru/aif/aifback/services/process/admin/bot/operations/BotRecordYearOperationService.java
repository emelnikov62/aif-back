package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_YEARS;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.findByType;

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
 * Admin Bot record year operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordYearOperationService implements AdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        ClientRecordType type = findByType(params[1]);
        String userBotId = params[2];
        String answer = String.format("%s %s", type.getIcon(), type.getNames());
        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        List<NameWithCount> years = clientRecordService.findYearsRecordsByStatus(Long.valueOf(userBotId), type.getType());
        if (years.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            List<ChatMessage.Button> row = new ArrayList<>();
            int num = 0;
            for (NameWithCount year : years) {
                row.add(ChatMessage.Button.builder()
                                          .title(String.format("%s (\uD83D\uDCDD %s)", year.getName(), year.getCount()))
                                          .callback(String.format("%s;%s;%s;%s",
                                                                  BOT_RECORD_MONTH.getType(),
                                                                  year.getName(),
                                                                  userBotId,
                                                                  type.getType()))
                                          .build());

                num++;
                if (num % COLUMNS_YEARS == 0) {
                    buttons.add(row);
                    row.clear();
                }
            }

            if (!row.isEmpty()) {
                buttons.add(row);
            }
        }

        buttons.add(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), userBotId)));

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
        return BOT_RECORD_YEAR;
    }
}
