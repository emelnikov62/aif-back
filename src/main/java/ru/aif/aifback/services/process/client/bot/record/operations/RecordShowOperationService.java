package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.SHOW_ERROR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_ADD_RECORD;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_CLIENT_STAR;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORD_CANCEL;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType.findByType;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getFileDataImage;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientStar;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientStarService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;
import ru.aif.aifback.services.user.UserItemService;

/**
 * Record show operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecordShowOperationService implements ClientBotOperationService {

    private final ClientStarService clientStarService;
    private final ClientRecordService clientRecordService;
    private final UserItemService userItemService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        Long recordId = Long.valueOf(webhookRequest.getText().split(DELIMITER)[1]);
        String status = webhookRequest.getText().split(DELIMITER)[2];

        ClientRecord clientRecord = clientRecordService.getClientRecordById(recordId);
        if (Objects.isNull(clientRecord)) {
            return fillErrorMessages(webhookRequest.getChatId(), webhookRequest.getMessageId(), webhookRequest.getSource(), status);
        }

        UserItem userItem = userItemService.findUserItemById(clientRecord.getAifUserItemId()).orElse(null);
        if (Objects.isNull(userItem)) {
            return fillErrorMessages(webhookRequest.getChatId(), webhookRequest.getMessageId(), webhookRequest.getSource(), status);
        }

        UserItemGroup group = userItemService.findUserItemGroupByItemId(userItem.getAifUserItemGroupId()).orElse(null);
        if (Objects.isNull(group)) {
            return fillErrorMessages(webhookRequest.getChatId(), webhookRequest.getMessageId(), webhookRequest.getSource(), status);
        }

        Float calcStar = clientStarService.calcByStaffAndUserItem(Long.valueOf(webhookRequest.getId()), clientRecord.getAifUserStaffId(),
                                                                  clientRecord.getAifUserItemId());
        ClientRecordType recordStatus = findByType(clientRecord.getStatus());
        String answer = String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.getName()) +
                        String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.getName()) +
                        String.format("\uD83D\uDD5B <b>Продолжительность:</b> %02d:%02d \n\n", userItem.getHours(), userItem.getMins()) +
                        String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.getAmount())) +
                        String.format("\uD83D\uDC64 <b>Специалист:</b> %s %s %s\n\n", clientRecord.getUserStaff().getSurname(),
                                      clientRecord.getUserStaff().getName(), clientRecord.getUserStaff().getThird()) +
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
                        String.format("⭐ <b>Оценка:</b> %.2f", calcStar);

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        if (Objects.equals(status, ACTIVE.getType())) {
            buttons.add(List.of(
                    ChatMessage.Button.builder()
                                      .title("\uD83D\uDCDD Изменить")
                                      .callback(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItem.getId(), clientRecord.getId()))
                                      .build(),
                    ChatMessage.Button.builder()
                                      .title("\uD83D\uDEAB Отменить")
                                      .callback(String.format("%s;%s", BOT_RECORD_CANCEL.getType(), clientRecord.getId()))
                                      .build()));
        } else {
            ClientStar clientStar = clientStarService.findClientStarByUserItemAndStaff(clientRecord.getAifClientId(),
                                                                                       clientRecord.getAifUserItemId(),
                                                                                       clientRecord.getAifUserStaffId());
            if (Objects.isNull(clientStar)) {
                buttons.add(List.of(
                        ChatMessage.Button.builder()
                                          .title("⭐ 1")
                                          .callback(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 1, clientRecord.getId()))
                                          .build(),
                        ChatMessage.Button.builder()
                                          .title("⭐ 2")
                                          .callback(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 2, clientRecord.getId()))
                                          .build(),
                        ChatMessage.Button.builder()
                                          .title("⭐ 3")
                                          .callback(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 3, clientRecord.getId()))
                                          .build(),
                        ChatMessage.Button.builder()
                                          .title("⭐ 4")
                                          .callback(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 4, clientRecord.getId()))
                                          .build(),
                        ChatMessage.Button.builder()
                                          .title("⭐ 5")
                                          .callback(String.format("%s;%s;%s", BOT_CLIENT_STAR.getType(), 5, clientRecord.getId()))
                                          .build()));
            }

            buttons.add(List.of(ChatMessage.Button.builder()
                                                  .title("\uD83D\uDD04 Повторить")
                                                  .callback(String.format("%s;%s;%s", BOT_ADD_RECORD.getType(), userItem.getId(), EMPTY_PARAM))
                                                  .build()));
        }

        buttons.add(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), status)));

        byte[] fileData = getFileDataImage(userItem.getFileData());
        if (Objects.isNull(fileData)) {
            return fillErrorMessages(webhookRequest.getChatId(), webhookRequest.getMessageId(), webhookRequest.getSource(), status);
        }

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(BotSource.findByType(webhookRequest.getSource()))
                                  .chatId(webhookRequest.getChatId())
                                  .messageId(webhookRequest.getMessageId())
                                  .buttons(buttons)
                                  .fileData(fileData)
                                  .build());
    }

    /**
     * Fill error messages.
     * @param chatId chat id
     * @param messageId message id
     * @param source source
     * @param status status
     * @return messages
     */
    private List<ChatMessage> fillErrorMessages(String chatId, String messageId, String source, String status) {
        return List.of(ChatMessage.builder()
                                  .text(SHOW_ERROR_TITLE)
                                  .updated(TRUE)
                                  .source(BotSource.findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(List.of(createBackButton(String.format("%s;%s", BOT_RECORDS.getType(), status))))
                                  .build());
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public ClientBotRecordOperationType getOperationType() {
        return BOT_RECORD_SHOW;
    }
}
