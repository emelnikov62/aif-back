package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOTS_CANCEL_RECORD_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_EMPTY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_DAY;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORD_SHOW_BY_DAY;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.getClientRecordInfo;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.findByType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
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
 * Admin Bot record show by day operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordShowByDayOperationService implements AdminBotOperationService {

    private final ClientRecordService clientRecordService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String[] params = webhookRequest.getText().split(DELIMITER);
        String day = params[1];
        String month = params[2];
        String year = params[3];
        String userBotId = params[4];
        ClientRecordType type = findByType(params[5]);

        List<ClientRecord> records = clientRecordService.findByDate(Long.valueOf(day),
                                                                    Long.valueOf(month),
                                                                    Long.valueOf(year),
                                                                    Long.valueOf(userBotId),
                                                                    type.getType());
        if (records.isEmpty()) {
            return List.of(ChatMessage.builder()
                                      .text(BOT_RECORDS_EMPTY)
                                      .updated(TRUE)
                                      .source(BotSource.findByType(webhookRequest.getSource()))
                                      .chatId(webhookRequest.getChatId())
                                      .messageId(webhookRequest.getMessageId())
                                      .buttons(createBackButton(String.format("%s;%s;%s;%s;%s",
                                                                              BOT_RECORD_DAY.getType(),
                                                                              month,
                                                                              year,
                                                                              userBotId,
                                                                              type.getType())))
                                      .build());
        }

        List<ChatMessage> messages = new ArrayList<>();
        for (ClientRecord record : records) {
            List<ChatMessage.Button> buttons = new ArrayList<>();
            String answer = getClientRecordInfo(record, type);
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(record.getStatus(), ACTIVE.getType())) {
                buttons.add(ChatMessage.Button.builder()
                                              .title(BOTS_CANCEL_RECORD_TITLE)
                                              .callback(String.format("%s;%s;%s;%s;%s;%s",
                                                                      BOT_RECORD_CANCEL.getType(),
                                                                      month,
                                                                      year,
                                                                      userBotId,
                                                                      type.getType(),
                                                                      record.getId()))
                                              .isBack(FALSE)
                                              .build());
            }

            buttons.addAll(createBackButton(String.format("%s;%s;%s;%s;%s", BOT_RECORD_DAY.getType(), month, year, userBotId, type.getType())));

            messages.add(ChatMessage.builder()
                                    .text(answer)
                                    .updated(FALSE)
                                    .source(BotSource.findByType(webhookRequest.getSource()))
                                    .chatId(webhookRequest.getChatId())
                                    .messageId(webhookRequest.getMessageId())
                                    .buttons(buttons)
                                    .build());
        }

        return messages;
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public AdminBotOperationType getOperationType() {
        return BOT_RECORD_SHOW_BY_DAY;
    }
}
