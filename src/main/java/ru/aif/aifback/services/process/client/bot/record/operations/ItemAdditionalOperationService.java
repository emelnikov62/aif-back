package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.ADD_RECORD_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ITEMS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ITEM_ADDITIONAL;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getFileDataImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.client.ClientStarService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * Item additional operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemAdditionalOperationService implements ClientBotOperationService {

    private final UserItemService userItemService;
    private final ClientStarService clientStarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String itemId = webhookRequest.getText().split(DELIMITER)[1];

        UserItem userItem = userItemService.findUserItemById(Long.valueOf(itemId)).orElse(null);
        if (Objects.isNull(userItem)) {
            return fillErrorMessages(webhookRequest.getSource(), webhookRequest.getChatId(), webhookRequest.getMessageId());
        }

        UserItemGroup group = userItemService.findUserItemGroupByItemId(userItem.getAifUserItemGroupId()).orElse(null);
        if (Objects.isNull(group)) {
            return fillErrorMessages(webhookRequest.getSource(), webhookRequest.getChatId(), webhookRequest.getMessageId());
        }

        Float calcStar = clientStarService.calcByUserItem(Long.valueOf(webhookRequest.getId()), Long.valueOf(itemId));
        String answer = String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.getName())
                        + String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.getName())
                        + String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n", userItem.getHours(), userItem.getMins())
                        + String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.getAmount()))
                        + String.format("⭐ <b>Оценка:</b> %.2f", calcStar);

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        buttons.add(List.of(ChatMessage.Button.builder()
                                              .title(ADD_RECORD_TITLE)
                                              .callback(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItem.getId(), EMPTY_PARAM))
                                              .build()));
        buttons.add(createBackButton(String.format("%s;%s", BOT_ITEMS.getType(), group.getId())));

        byte[] fileData = getFileDataImage(userItem.getFileData());
        if (Objects.isNull(fileData)) {
            return fillErrorMessages(webhookRequest.getSource(), webhookRequest.getChatId(), webhookRequest.getMessageId());
        }

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(buttons)
                                  .fileData(fileData)
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
                                  .text(SHOW_ERROR_TITLE)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(BOT_MAIN.getType())))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_ITEM_ADDITIONAL;
    }
}
