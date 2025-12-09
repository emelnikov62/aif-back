package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOTS_TO_CREATE_EMPTY_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.SELECT_BOT_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_CONFIRM_CREATE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_CREATE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.getBotIconByType;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.dictionary.Bot;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.user.BotService;

/**
 * Admin Bot create operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotCreateOperationService implements AdminBotOperationService {

    private final BotService botService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        String answer = SELECT_BOT_TITLE;
        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        List<Bot> bots = botService.getBots();
        if (bots.isEmpty()) {
            answer = BOTS_TO_CREATE_EMPTY_TITLE;
        } else {
            bots.forEach(b -> buttons.add(List.of(ChatMessage.Button.builder()
                                                                    .title(String.format("%s %s", getBotIconByType(b.getType()), b.getDescription()))
                                                                    .callback(String.format("%s;%s", BOT_CONFIRM_CREATE.getType(), b.getId()))
                                                                    .build())));
        }

        buttons.add(createBackButton(BOT_MAIN.getType()));
        return List.of(ChatMessage.builder()
                                  .text(answer)
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
        return BOT_CREATE;
    }
}
