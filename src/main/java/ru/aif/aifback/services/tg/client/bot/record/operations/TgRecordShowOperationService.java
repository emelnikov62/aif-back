package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;

import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.TgBotOperationService;
import ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Record show operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordShowOperationService implements TgBotOperationService {

    private final ClientRecordService clientRecordService;
    private final UserItemService userItemService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        Long recordId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        Long chatId = Long.valueOf(webhookRequest.getChatId());

        Optional<ClientRecord> clientRecord = clientRecordService.getClientRecordById(recordId);
        if (clientRecord.isEmpty()) {
            sendErrorMessage(keyboard, chatId, bot);
            return;
        }

        Optional<UserItem> userItem = userItemService.findUserItemById(clientRecord.get().getAifUserItemId());
        if (userItem.isEmpty()) {
            sendErrorMessage(keyboard, chatId, bot);
            return;
        }

        Optional<UserItemGroup> group = userItemService.findUserItemGroupByItemId(userItem.get().getAifUserItemGroupId());
        if (group.isEmpty()) {
            sendErrorMessage(keyboard, chatId, bot);
            return;
        }

        String answer = String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.get().getName())
                        + String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.get().getName())
                        + String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n", userItem.get().getHours(), userItem.get().getMins())
                        + String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.get().getAmount()));

        keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientRecordBotOperationType.BOT_RECORD_ACTIVE.getType()));

        TgUtils.sendPhoto(chatId, Base64.getDecoder().decode(userItem.get().getFileData()), answer, keyboard, bot);
    }

    /**
     * Send error message.
     * @param keyboard keyboard
     * @param chatId chat id
     * @param bot telegram bot
     */
    private void sendErrorMessage(InlineKeyboardMarkup keyboard, Long chatId, TelegramBot bot) {
        keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientRecordBotOperationType.BOT_RECORD_ACTIVE.getType()));
        TgUtils.sendMessage(chatId, SHOW_ERROR_TITLE, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return TgClientRecordBotOperationType.BOT_RECORD_SHOW;
    }
}
