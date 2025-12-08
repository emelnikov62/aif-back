package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOTS_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_SHOW_ADDITIONAL;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.getClientRecordInfo;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.findByType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.process.client.enums.ClientRecordType;

/**
 * Admin Bot record show additional operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordShowAdditionalOperationService implements AdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String recordId = params[1];
        String userBotId = params[2];
        List<ChatMessage.Button> buttons = new ArrayList<>();

        String answer = BOT_RECORDS_EMPTY;
        ClientRecord record = clientRecordService.getClientRecordById(Long.valueOf(recordId));
        if (Objects.nonNull(record)) {
            answer = getClientRecordInfo(record, findByType(record.getStatus()));
        }

        if (Objects.equals(record.getStatus(), ClientRecordType.ACTIVE.getType())) {
            buttons.add(ChatMessage.Button.builder()
                                          .title(BOTS_CANCEL_RECORD_TITLE)
                                          .callback(String.format("%s;%s;%s;%s;%s;%s",
                                                                  BOT_RECORD_CANCEL.getType(),
                                                                  record.getUserCalendar().getMonth(),
                                                                  record.getUserCalendar().getYear(),
                                                                  userBotId,
                                                                  record.getStatus(),
                                                                  record.getId()))
                                          .isBack(FALSE)
                                          .build());
        }

        buttons.addAll(createBackButton(String.format("%s;%s;%s;%s;%s",
                                                      BOT_RECORD_DAY.getType(),
                                                      record.getUserCalendar().getMonth(),
                                                      record.getUserCalendar().getYear(),
                                                      userBotId,
                                                      record.getStatus())));

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
        return BOT_RECORD_SHOW_ADDITIONAL;
    }
}
