package ru.aif.aifback.services.process.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.ADD_RECORD_TITLE;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_ITEMS;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_ITEM_ADDITIONAL;
import static ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.utils.CommonUtils.sendMessage;
import static ru.aif.aifback.services.utils.CommonUtils.sendPhoto;

import java.util.Base64;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.process.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.process.client.enums.ClientRecordBotOperationType;
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
    public void process(WebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        String itemId = webhookRequest.getText().split(DELIMITER)[1];

        UserItem userItem = userItemService.findUserItemById(Long.valueOf(itemId)).orElse(null);
        if (Objects.isNull(userItem)) {
            sendErrorMessage(keyboard, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot);
            return;
        }

        UserItemGroup group = userItemService.findUserItemGroupByItemId(userItem.getAifUserItemGroupId()).orElse(null);
        if (Objects.isNull(group)) {
            sendErrorMessage(keyboard, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot);
            return;
        }

        String answer = String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.getName())
                        + String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.getName())
                        + String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n", userItem.getHours(), userItem.getMins())
                        + String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.getAmount()));

        keyboard.addRow(
                new InlineKeyboardButton(ADD_RECORD_TITLE).callbackData(
                        String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItem.getId(), EMPTY_PARAM)));
        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_ITEMS.getType(), group.getId())));

        sendPhoto(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()),
                  Base64.getDecoder().decode(userItem.getFileData()), answer, keyboard, bot);
    }

    /**
     * Send error message.
     * @param keyboard keyboard
     * @param chatId chat id
     * @param messageId message id
     * @param bot telegram bot
     */
    private void sendErrorMessage(InlineKeyboardMarkup keyboard, String chatId, int messageId, TelegramBot bot) {
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));
        sendMessage(chatId, messageId, SHOW_ERROR_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientRecordBotOperationType getOperationType() {
        return BOT_ITEM_ADDITIONAL;
    }
}
