package ru.aif.aifback.services.process.admin.bot.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BOTS_EMPTY_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MY_BOTS_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_BOTS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_SELECT;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.createBackButton;
import static ru.aif.aifback.services.process.admin.utils.AdminBotUtils.getBotIconByType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.services.process.admin.AdminBotOperationService;
import ru.aif.aifback.services.process.admin.enums.AdminBotOperationType;
import ru.aif.aifback.services.user.UserBotService;

/**
 * Admin Bots operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BotsOperationService implements AdminBotOperationService {

    private final UserBotService userBotService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest) {
        List<List<ChatMessage.Button>> buttons = new ArrayList<>();

        List<UserBot> userBots = userBotService.getUserBotsBySource(webhookRequest.getChatId(), webhookRequest.getSource());
        userBots.forEach(userBot -> {
            buttons.add(List.of(ChatMessage.Button.builder()
                                                  .title(String.format("%s %s (ID: %s) %s",
                                                                       getBotIconByType(userBot.getBot().getType()),
                                                                       userBot.getBot().getDescription(),
                                                                       userBot.getId(),
                                                                       (userBot.isActive() && Objects.nonNull(userBot.getToken()) ? "✅" : "❌")))
                                                  .callback(String.format("%s;%s", BOT_SELECT.getType(), userBot.getId()))
                                                  .build()));
        });

        buttons.add(createBackButton(BOT_MAIN.getType()));

        return List.of(ChatMessage.builder()
                                  .text(userBots.isEmpty() ? BOTS_EMPTY_TITLE : MY_BOTS_TITLE)
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
        return BOT_BOTS;
    }
}
