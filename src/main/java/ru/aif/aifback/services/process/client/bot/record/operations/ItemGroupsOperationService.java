package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.GROUP_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ITEMS_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_GROUP;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ITEMS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * Item groups operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemGroupsOperationService implements ClientBotOperationService {

    private final UserItemService userItemService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        List<UserItemGroup> groups = userItemService.getUserItemGroupsAndActive(userBot.getId());
        if (groups.isEmpty()) {
            return List.of(ChatMessage.builder()
                                      .text(GROUP_EMPTY_TITLE)
                                      .updated(TRUE)
                                      .source(findByType(webhookRequest.getSource()))
                                      .chatId(webhookRequest.getChatId())
                                      .messageId(webhookRequest.getMessageId())
                                      .buttons(List.of(createBackButton(BOT_MAIN.getType())))
                                      .build());
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        groups.forEach(group -> buttons.add(List.of(ChatMessage.Button.builder()
                                                                      .title(String.format(GROUP_TITLE, group.getName()))
                                                                      .callback(String.format("%s;%s", BOT_ITEMS.getType(), group.getId()))
                                                                      .build())));

        buttons.add(createBackButton(BOT_MAIN.getType()));

        return List.of(ChatMessage.builder()
                                  .text(ITEMS_TITLE)
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
        return BOT_GROUP;
    }
}
