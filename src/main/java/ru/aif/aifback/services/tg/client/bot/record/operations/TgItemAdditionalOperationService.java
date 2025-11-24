package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ADD_RECORD_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;

import java.util.Base64;
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
 * TG Item additional operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgItemAdditionalOperationService implements TgClientBotOperationService {

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
        String itemId = webhookRequest.getText().split(DELIMITER)[1];

        Optional<UserItem> userItem = userItemService.findUserItemById(Long.valueOf(itemId));
        if (userItem.isEmpty()) {
            sendErrorMessage(keyboard, Long.valueOf(webhookRequest.getChatId()), bot);
            return;
        }

        Optional<UserItemGroup> group = userItemService.findUserItemGroupByItemId(userItem.get().getAifUserItemGroupId());
        if (group.isEmpty()) {
            sendErrorMessage(keyboard, Long.valueOf(webhookRequest.getChatId()), bot);
            return;
        }

        String answer = String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.get().getName())
                        + String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.get().getName())
                        + String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n", userItem.get().getHours(), userItem.get().getMins())
                        + String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.get().getAmount()));

        keyboard.addRow(new InlineKeyboardButton(ADD_RECORD_TITLE).callbackData(String.format("%s;%s",
                                                                                              TgClientRecordBotOperationType.BOT_ADD_RECORD.getType(),
                                                                                              userItem.get().getId())));
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(
                String.format("%s;%s", TgClientRecordBotOperationType.BOT_ITEMS.getType(), group.get().getId())));

        TgUtils.sendPhoto(Long.valueOf(webhookRequest.getChatId()), Base64.getDecoder().decode(userItem.get().getFileData()), answer, keyboard, bot);
    }

    /**
     * Send error message.
     * @param keyboard keyboard
     * @param chatId chat id
     * @param bot telegram bot
     */
    private void sendErrorMessage(InlineKeyboardMarkup keyboard, Long chatId, TelegramBot bot) {
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientRecordBotOperationType.BOT_MAIN.getType()));
        TgUtils.sendMessage(chatId, SHOW_ERROR_TITLE, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_ITEM_ADDITIONAL;
    }
}
