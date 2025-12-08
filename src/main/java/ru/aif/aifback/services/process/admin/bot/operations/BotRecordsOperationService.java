package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_RECORDS_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.CANCEL;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.FINISHED;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.process.client.enums.ClientRecordType;

/**
 * Admin Bot records operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotRecordsOperationService implements AdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String userBotId = webhookRequest.getText().split(DELIMITER)[1];
        List<ChatMessage.Button> buttons = new ArrayList<>();

        Function<ClientRecordType, String> names = (type) -> String.format("%s %s", type.getIcon(), type.getNames());
        BiFunction<ClientRecordType, String, String> callbackData = (type, id) ->
                String.format("%s;%s;%s", AdminBotOperationType.BOT_RECORD_YEAR.getType(), type.getType(), id);

        buttons.add(ChatMessage.Button.builder()
                                      .title(names.apply(ACTIVE))
                                      .callback(callbackData.apply(ACTIVE, userBotId))
                                      .isBack(FALSE)
                                      .build());
        buttons.add(ChatMessage.Button.builder()
                                      .title(names.apply(CANCEL))
                                      .callback(callbackData.apply(CANCEL, userBotId))
                                      .isBack(FALSE)
                                      .build());
        buttons.add(ChatMessage.Button.builder()
                                      .title(names.apply(FINISHED))
                                      .callback(callbackData.apply(FINISHED, userBotId))
                                      .isBack(FALSE)
                                      .build());

        buttons.addAll(createBackButton(String.format("%s;%s", AdminBotOperationType.BOT_SELECT.getType(), userBotId)));

        return List.of(ChatMessage.builder()
                                  .text(BOT_RECORDS_TITLE)
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
    public AdminBotOperationType getOperationType() {
        return BOT_RECORDS;
    }
}
