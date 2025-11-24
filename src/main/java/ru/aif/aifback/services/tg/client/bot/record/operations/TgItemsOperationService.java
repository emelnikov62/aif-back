package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ITEM_TITLE;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Items operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgItemsOperationService implements TgClientBotOperationService {

    private final UserItemService userItemService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        String groupId = webhookRequest.getText().split(DELIMITER)[1];
        String answer = processBotGroupItems(Long.valueOf(groupId), keyboard);
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientRecordBotOperationType.BOT_GROUP.getType()));

        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
    }

    /**
     * Process bot group items button.
     * @param groupId group id
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotGroupItems(Long groupId, InlineKeyboardMarkup keyboard) {
        Optional<UserItemGroup> userItemGroup = userItemService.findUserItemGroupByItemId(groupId);
        if (userItemGroup.isEmpty()) {
            return GROUP_EMPTY_TITLE;
        }

        List<UserItem> items = userItemService.getUserItemsByGroupIdAndActive(groupId);
        if (items.isEmpty()) {
            return GROUP_EMPTY_TITLE;
        }

        items.forEach(item -> keyboard.addRow(new InlineKeyboardButton(String.format(ITEM_TITLE,
                                                                                     item.getName(),
                                                                                     item.getAmount(),
                                                                                     item.getHours(),
                                                                                     item.getMins()))
                                                      .callbackData(String.format("%s;%s",
                                                                                  TgClientRecordBotOperationType.BOT_ITEM_ADDITIONAL.getType(),
                                                                                  item.getId()))));

        return String.format(GROUP_TITLE, userItemGroup.get().getName());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_ITEMS;
    }
}
