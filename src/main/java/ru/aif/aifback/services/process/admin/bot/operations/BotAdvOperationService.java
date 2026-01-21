package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOT_ADV_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_ADV;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;

/**
 * Admin Advertise operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotAdvOperationService implements AdminBotOperationService {

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        Long userBotId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        buttons.add(createBackButton(String.format("%s;%s", BOT_SELECT.getType(), userBotId)));

        return List.of(ChatMessage.builder()
                                  .text(BOT_ADV_TITLE)
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
        return BOT_ADV;
    }
}
