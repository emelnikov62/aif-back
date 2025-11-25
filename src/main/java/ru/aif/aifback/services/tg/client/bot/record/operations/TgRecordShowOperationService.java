package ru.aif.aifback.services.tg.client.bot.record.operations;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_EDIT;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendPhoto;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Record show operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordShowOperationService implements TgClientBotOperationService {

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

        ClientRecord clientRecord = clientRecordService.getClientRecordById(recordId);
        if (Objects.isNull(clientRecord)) {
            sendErrorMessage(keyboard, chatId, bot);
            return;
        }

        Optional<UserItem> userItem = userItemService.findUserItemById(clientRecord.getAifUserItemId());
        if (userItem.isEmpty()) {
            sendErrorMessage(keyboard, chatId, bot);
            return;
        }

        Optional<UserItemGroup> group = userItemService.findUserItemGroupByItemId(userItem.get().getAifUserItemGroupId());
        if (group.isEmpty()) {
            sendErrorMessage(keyboard, chatId, bot);
            return;
        }

        String answer = String.format(String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.get().getName()) +
                                      String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.get().getName()) +
                                      String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n", userItem.get().getHours(),
                                                    userItem.get().getMins()) +
                                      String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.get().getAmount())) +
                                      String.format("\uD83D\uDC64 <b>Специалист:</b> %s %s %s\n\n",
                                                    clientRecord.getUserStaff().getSurname(),
                                                    clientRecord.getUserStaff().getName(),
                                                    clientRecord.getUserStaff().getThird()) +
                                      String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s %02d:%02d",
                                                    getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                                                 clientRecord.getUserCalendar().getMonth(),
                                                                 clientRecord.getUserCalendar().getYear()),
                                                    clientRecord.getUserCalendar().getDay(),
                                                    getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                    clientRecord.getUserCalendar().getYear(),
                                                    clientRecord.getHours(),
                                                    clientRecord.getMins()));

        keyboard.addRow(new InlineKeyboardButton("\uD83D\uDCDD Изменить")
                                .callbackData(String.format("%s;%s", BOT_RECORD_EDIT.getType(), clientRecord.getId())),
                        new InlineKeyboardButton("\uD83D\uDEAB Отменить")
                                .callbackData(String.format("%s;%s", BOT_RECORD_CANCEL.getType(), clientRecord.getId())));

        keyboard.addRow(createBackButton(BOT_RECORD_ACTIVE.getType()));
        sendPhoto(chatId, Base64.getDecoder().decode(userItem.get().getFileData()), answer, keyboard, bot);
    }

    /**
     * Send error message.
     * @param keyboard keyboard
     * @param chatId chat id
     * @param bot telegram bot
     */
    private void sendErrorMessage(InlineKeyboardMarkup keyboard, Long chatId, TelegramBot bot) {
        keyboard.addRow(createBackButton(BOT_RECORD_ACTIVE.getType()));
        sendMessage(chatId, SHOW_ERROR_TITLE, keyboard, bot);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_RECORD_SHOW;
    }
}
