package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.CREATE_BOT_ERROR_ANSWER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.CREATE_BOT_SUCCESS_ANSWER;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MY_BOTS_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_BOTS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_CONFIRM_CREATE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_MAIN;
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
import ru.aif.aifback.services.user.UserBotService;

/**
 * Admin Bot confirm create operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public abstract class BotConfirmCreateOperationService implements AdminBotOperationService {

    private final UserBotService userBotService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        Long botId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);

        String answer = CREATE_BOT_ERROR_ANSWER;
        List<ChatMessage.Button> buttons = new ArrayList<>();

        if (userBotService.createUserBot(webhookRequest.getChatId(), webhookRequest.getSource(), botId)) {
            answer = CREATE_BOT_SUCCESS_ANSWER;
            buttons.add(ChatMessage.Button.builder().title(MY_BOTS_TITLE).callback(BOT_BOTS.getType()).isBack(FALSE).build());
        } else {
            buttons.addAll(createBackButton(BOT_MAIN.getType()));
        }

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
        return BOT_CONFIRM_CREATE;
    }

}
