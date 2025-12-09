package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ACTIVE_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.HISTORY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.RECORDS_EMPTY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.NO_ACTIVE;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

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
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;

/**
 * Records active operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordsOperationService implements ClientBotOperationService {

    private final ClientRecordService clientRecordService;
    private final ClientService clientService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String status = webhookRequest.getText().split(DELIMITER)[1];

        Long clientId = clientService.getClientIdOrCreate(webhookRequest.getChatId());
        if (Objects.isNull(clientId)) {
            return List.of(
                    ChatMessage.builder()
                               .text(RECORDS_EMPTY_TITLE)
                               .updated(TRUE)
                               .source(BotSource.findByType(webhookRequest.getSource()))
                               .chatId(webhookRequest.getChatId())
                               .messageId(webhookRequest.getMessageId())
                               .buttons(List.of(createBackButton(BOT_MAIN.getType())))
                               .build()
            );
        }

        List<List<ChatMessage.Button>> buttons = fillClientRecords(clientId, status);
        String answer = !buttons.isEmpty()
                        ? (Objects.equals(status, NO_ACTIVE.getType()) ? HISTORY_TITLE : ACTIVE_TITLE)
                        : RECORDS_EMPTY_TITLE;

        buttons.add(createBackButton(BOT_MAIN.getType()));

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
     * Fill client records.
     * @param clientId client id
     * @param status status
     * @return buttons
     */
    private List<List<ChatMessage.Button>> fillClientRecords(Long clientId, String status) {
        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        List<ClientRecord> clientRecords = Objects.equals(status, NO_ACTIVE.getType())
                                           ? clientRecordService.findAllCompletedByClientId(clientId)
                                           : clientRecordService.findAllByClientIdAndStatus(clientId, status);

        clientRecords.forEach(clientRecord -> {
            String dayOfWeek = getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                            clientRecord.getUserCalendar().getMonth(),
                                            clientRecord.getUserCalendar().getYear());
            ClientRecordType recordStatus = ClientRecordType.findByType(clientRecord.getStatus());
            buttons.add(List.of(ChatMessage.Button.builder()
                                                  .title(String.format("%s \uD83D\uDCC5 %s %02d %s %s %02d:%02d (%s)",
                                                                       recordStatus.getIcon(),
                                                                       dayOfWeek,
                                                                       clientRecord.getUserCalendar().getDay(),
                                                                       getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                                       clientRecord.getUserCalendar().getYear(),
                                                                       clientRecord.getHours(),
                                                                       clientRecord.getMins(),
                                                                       clientRecord.getUserItem().getName()))
                                                  .callback(String.format("%s;%s;%s",
                                                                          BOT_RECORD_SHOW.getType(),
                                                                          clientRecord.getId(),
                                                                          status))
                                                  .build()));
        });

        return buttons;
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_RECORDS;
    }
}
