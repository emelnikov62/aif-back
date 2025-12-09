package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.GROUP_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ITEM_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_GROUP;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ITEMS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ITEM_ADDITIONAL;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * Items operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemsOperationService implements ClientBotOperationService {

    private final UserItemService userItemService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        Long groupId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);

        Optional<UserItemGroup> userItemGroup = userItemService.findUserItemGroupByItemId(groupId);
        if (userItemGroup.isEmpty()) {
            return fillErrorMessages(webhookRequest.getSource(), webhookRequest.getChatId(), webhookRequest.getMessageId());
        }

        List<UserItem> items = userItemService.getUserItemsByGroupIdAndActive(groupId);
        if (items.isEmpty()) {
            return fillErrorMessages(webhookRequest.getSource(), webhookRequest.getChatId(), webhookRequest.getMessageId());
        }

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        items.forEach(item -> buttons.add(List.of(ChatMessage.Button.builder()
                                                                    .title(String.format(ITEM_TITLE,
                                                                                         item.getName(),
                                                                                         item.getAmount(),
                                                                                         item.getHours(),
                                                                                         item.getMins()))
                                                                    .callback(String.format("%s;%s",
                                                                                            BOT_ITEM_ADDITIONAL.getType(),
                                                                                            item.getId()))
                                                                    .build())));

        buttons.add(createBackButton(BOT_GROUP.getType()));

        return List.of(ChatMessage.builder()
                                  .text(String.format(GROUP_TITLE, userItemGroup.get().getName()))
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Fill error messages.
     * @param source source
     * @param chatId chat id
     * @param messageId message id
     * @return messages
     */
    private List<ChatMessage> fillErrorMessages(String source, String chatId, String messageId) {
        return List.of(ChatMessage.builder()
                                  .text(GROUP_EMPTY_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(BOT_GROUP.getType())))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_ITEMS;
    }
}
