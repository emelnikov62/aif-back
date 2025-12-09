package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.CALENDAR_SELECT_YEAR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ITEM_ADDITIONAL;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_SELECT_YEAR;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;

import java.time.LocalDate;
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

/**
 * Add record operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddRecordOperationService implements ClientBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        String[] params = webhookRequest.getText().split(DELIMITER);
        String itemId = params[1];
        String recordId = params[2];

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int nextYear = currentYear + 1;

        buttons.add(List.of(
                ChatMessage.Button.builder()
                                  .title(String.valueOf(currentYear))
                                  .callback(String.format("%s;%s;%s;%s", BOT_SELECT_YEAR.getType(), currentYear, itemId, recordId))
                                  .build(),
                ChatMessage.Button.builder()
                                  .title(String.valueOf(nextYear))
                                  .callback(String.format("%s;%s;%s;%s", BOT_SELECT_YEAR.getType(), nextYear, itemId, recordId))
                                  .build()));

        buttons.add(createBackButton(String.format("%s;%s", BOT_ITEM_ADDITIONAL.getType(), itemId)));

        return List.of(ChatMessage.builder()
                                  .text(CALENDAR_SELECT_YEAR_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
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
    public ClientBotRecordOperationType getOperationType() {
        return BOT_ADD_RECORD;
    }
}
