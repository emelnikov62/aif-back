package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.COLUMNS_DAYS;
import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_MONTH;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_YEAR;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.findByType;

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
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        ClientRecordType type = findByType(params[1]);
        String userBotId = params[2];
        String answer = String.format("%s %s", type.getIcon(), type.getNames());
        List<ChatMessage.Button> buttons = new ArrayList<>();

        List<NameWithCount> years = clientRecordService.findYearsRecordsByStatus(Long.valueOf(userBotId), type.getType());
        if (years.isEmpty()) {
            answer = BOT_RECORDS_EMPTY;
        } else {
            for (NameWithCount year : years) {
                buttons.add(ChatMessage.Button.builder()
                                              .title(String.format("%s (\uD83D\uDCDD %s)", year.getName(), year.getCount()))
                                              .callback(String.format("%s;%s;%s;%s",
                                                                      BOT_RECORD_MONTH.getType(),
                                                                      year.getName(),
                                                                      userBotId,
                                                                      type.getType()))
                                              .isBack(FALSE)
                                              .build());
            }
        }

        buttons.addAll(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), userBotId)));

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
        return BOT_RECORD_YEAR;
    }
}
