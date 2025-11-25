package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_CLIENT_STAR;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.findByType;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendPhoto;

import java.util.Base64;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientStar;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientStarService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Record show operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordShowOperationService implements TgClientBotOperationService {

    private final ClientStarService clientStarService;
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
        String status = webhookRequest.getText().split(DELIMITER)[2];
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        ClientRecord clientRecord = clientRecordService.getClientRecordById(recordId);
        if (Objects.isNull(clientRecord)) {
            sendErrorMessage(keyboard, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot, status);
            return;
        }

        UserItem userItem = userItemService.findUserItemById(clientRecord.getAifUserItemId()).orElse(null);
        if (Objects.isNull(userItem)) {
            sendErrorMessage(keyboard, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot, status);
            return;
        }

        UserItemGroup group = userItemService.findUserItemGroupByItemId(userItem.getAifUserItemGroupId()).orElse(null);
        if (Objects.isNull(group)) {
            sendErrorMessage(keyboard, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot, status);
            return;
        }

        Float calcStar = clientStarService.calcByStaffAndUserItem(Long.valueOf(webhookRequest.getId()),
                                                                  clientRecord.getAifUserStaffId(),
                                                                  clientRecord.getAifUserItemId());
        TgClientRecordType recordStatus = findByType(clientRecord.getStatus());
        String answer = String.format(String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.getName()) +
                                      String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.getName()) +
                                      String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n",
                                                    userItem.getHours(), userItem.getMins()) +
                                      String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.getAmount())) +
                                      String.format("\uD83D\uDC64 <b>Специалист:</b> %s %s %s\n\n",
                                                    clientRecord.getUserStaff().getSurname(),
                                                    clientRecord.getUserStaff().getName(),
                                                    clientRecord.getUserStaff().getThird()) +
                                      String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s %02d:%02d\n\n",
                                                    getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                                                 clientRecord.getUserCalendar().getMonth(),
                                                                 clientRecord.getUserCalendar().getYear()),
                                                    clientRecord.getUserCalendar().getDay(),
                                                    getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                    clientRecord.getUserCalendar().getYear(),
                                                    clientRecord.getHours(),
                                                    clientRecord.getMins()) +
                                      String.format("%s <b>Статус:</b> %s\n\n", recordStatus.getIcon(), recordStatus.getName()) +
                                      String.format("⭐ <b>Оценка:</b> %.2f", calcStar));

        if (Objects.equals(status, ACTIVE.getType())) {
            keyboard.addRow(new InlineKeyboardButton("\uD83D\uDCDD Изменить")
                                    .callbackData(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItem.getId(), clientRecord.getId())),
                            new InlineKeyboardButton("\uD83D\uDEAB Отменить")
                                    .callbackData(String.format("%s;%s", BOT_RECORD_CANCEL.getType(), clientRecord.getId())));
        } else {
            ClientStar clientStar = clientStarService.findClientStarByUserItemAndStaff(clientRecord.getAifClientId(),
                                                                                       clientRecord.getAifUserItemId(),
                                                                                       clientRecord.getAifUserStaffId());
            if (Objects.isNull(clientStar)) {
                keyboard.addRow(
                        new InlineKeyboardButton("⭐ 1").callbackData(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 1, clientRecord.getId())),
                        new InlineKeyboardButton("⭐ 2").callbackData(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 2, clientRecord.getId())),
                        new InlineKeyboardButton("⭐ 3").callbackData(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 3, clientRecord.getId())),
                        new InlineKeyboardButton("⭐ 4").callbackData(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 4, clientRecord.getId())),
                        new InlineKeyboardButton("⭐ 5").callbackData(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 5, clientRecord.getId())));
            }

            keyboard.addRow(new InlineKeyboardButton("\uD83D\uDD04 Повторить")
                                    .callbackData(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItem.getId(), EMPTY_PARAM)));
        }

        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), status)));
        sendPhoto(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), Base64.getDecoder().decode(userItem.getFileData()),
                  answer, keyboard, bot);
    }

    /**
     * Send error message.
     * @param keyboard keyboard
     * @param chatId chat id
     * @param messageId message id
     * @param bot telegram bot
     * @param status status
     */
    private void sendErrorMessage(InlineKeyboardMarkup keyboard, String chatId, int messageId, TelegramBot bot, String status) {
        keyboard.addRow(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), status)));
        sendMessage(chatId, messageId, SHOW_ERROR_TITLE, keyboard, bot, TRUE);
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
