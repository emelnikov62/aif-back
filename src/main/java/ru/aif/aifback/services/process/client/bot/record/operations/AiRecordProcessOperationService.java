package ru.aif.aifback.services.process.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.enums.BotSource.findByType;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.AI_RECORD_CONFIRM_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.AI_RECORD_ERROR_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.constants.ClientBotRecordButtons.AI_RECORD_PROCESS_TITLE;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_AI_RECORD_PROCESS;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType.BOT_MAIN;
import static ru.aif.aifback.services.process.client.bot.record.utils.ClientBotRecordUtils.createBackButton;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.model.requests.WebhookRequest;
import ru.aif.aifback.model.response.AiRecordResponse;
import ru.aif.aifback.model.response.AiRecordStaffResponse;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.ai.recognize.tg.TgVoiceRecognizeService;
import ru.aif.aifback.services.ai.record.RecordSearchService;
import ru.aif.aifback.services.process.client.ClientBotOperationService;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientBotRecordOperationType;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * AI record operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRecordProcessOperationService implements ClientBotOperationService {

    private final TgVoiceRecognizeService voiceRecognizeService;
    private final RecordSearchService recordSearchService;
    private final UserItemService userItemService;
    private final UserCalendarService userCalendarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @return messages
     */
    @Override
    public List<ChatMessage> process(WebhookRequest webhookRequest, UserBot userBot) {
        String result = voiceRecognizeService.recognize(webhookRequest, userBot);
        if (Objects.isNull(result)) {
            return fillErrorSearchRecord(webhookRequest.getChatId(), webhookRequest.getMessageId(), userBot.getSource());
        }

        List<ChatMessage> messages = new ArrayList<>();

        messages.add(ChatMessage.builder()
                                .text(AI_RECORD_PROCESS_TITLE)
                                .updated(TRUE)
                                .source(findByType(webhookRequest.getSource()))
                                .chatId(webhookRequest.getChatId())
                                .messageId(webhookRequest.getMessageId())
                                .build());

        AiRecordResponse response = recordSearchService.search(result, userBot, webhookRequest.getChatId());
        if (Objects.isNull(response) || response.getStaffs().isEmpty()) {
            messages.addAll(fillErrorSearchRecord(webhookRequest.getChatId(), webhookRequest.getMessageId(), userBot.getSource()));
            return messages;
        }

        UserItem userItem = userItemService.findUserItemById(Long.valueOf(response.getItemId())).orElse(null);
        if (Objects.isNull(userItem)) {
            messages.addAll(fillErrorSearchRecord(webhookRequest.getChatId(), webhookRequest.getMessageId(), userBot.getSource()));
            return messages;
        }

        Long hours = Long.valueOf(response.getHours());
        Long mins = Long.valueOf(response.getMins());
        List<AiRecordStaffResponse> staffs = response.getStaffs().stream().filter(f -> Objects.nonNull(f.getCalendarId())).toList();
        if (staffs.isEmpty()) {
            messages.addAll(fillErrorSearchRecord(webhookRequest.getChatId(), webhookRequest.getMessageId(), userBot.getSource()));
            return messages;
        }

        if (staffs.size() == 1) {
            messages.addAll(processOneStaff(staffs.get(0),
                                            hours,
                                            mins,
                                            webhookRequest.getChatId(),
                                            webhookRequest.getMessageId(),
                                            userBot.getSource(),
                                            userItem));
        } else {
            messages.addAll(processAllStaffs(staffs,
                                             hours,
                                             mins,
                                             webhookRequest.getChatId(),
                                             webhookRequest.getMessageId(),
                                             userBot.getSource(),
                                             userItem));
        }

        return messages;
    }

    /**
     * Process one staff.
     * @param staff staff
     * @param hours hours
     * @param mins mins
     * @param chatId chat id
     * @param messageId message id
     * @param source source
     * @param userItem user item
     * @return messages
     */
    private List<ChatMessage> processOneStaff(AiRecordStaffResponse staff, Long hours, Long mins, String chatId, String messageId, String source,
                                              UserItem userItem) {
        UserCalendar calendar = userCalendarService.findById(Long.valueOf(staff.getCalendarId())).orElse(null);
        if (Objects.isNull(calendar)) {
            return fillErrorSearchRecord(chatId, messageId, source);
        }

        String answer = "\uD83D\uDD35 <b>Новая запись</b>\n\n" +
                        String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s <b>%02d:%02d</b>",
                                      getDayOfWeek(calendar.getDay(), calendar.getMonth(), calendar.getYear()),
                                      calendar.getDay(),
                                      getMonthByNumber(calendar.getMonth()),
                                      calendar.getYear(),
                                      hours,
                                      mins) +
                        String.format("\n\n\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", userItem.getName()) +
                        String.format("\uD83D\uDC64 <b>Специалист:</b> %s", staff.getName());

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        buttons.add(List.of(ChatMessage.Button.builder()
                                              .title(AI_RECORD_CONFIRM_TITLE)
                                              .callback(String.format("%s;%s;%s;%s;%s;%s;%s",
                                                                      BOT_CONFIRM_SELECT_TIME.getType(),
                                                                      calendar.getId(),
                                                                      hours,
                                                                      mins,
                                                                      userItem.getId(),
                                                                      staff.getId(),
                                                                      EMPTY_PARAM))
                                              .build()));
        buttons.add(createBackButton(BOT_MAIN.getType()));

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Process all staffs.
     * @param staffs staffs
     * @param hours hours
     * @param mins mins
     * @param chatId chat id
     * @param messageId message id
     * @param source source
     * @param userItem user item
     * @return messages
     */
    private List<ChatMessage> processAllStaffs(List<AiRecordStaffResponse> staffs, Long hours, Long mins, String chatId, String messageId,
                                               String source, UserItem userItem) {
        UserCalendar calendar = userCalendarService.findById(Long.valueOf(staffs.get(0).getCalendarId())).orElse(null);
        if (Objects.isNull(calendar)) {
            return fillErrorSearchRecord(chatId, messageId, source);
        }

        String answer = "\uD83D\uDD35 <b>Новая запись</b>\n\n" +
                        String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s <b>%02d:%02d</b>",
                                      getDayOfWeek(calendar.getDay(), calendar.getMonth(), calendar.getYear()),
                                      calendar.getDay(),
                                      getMonthByNumber(calendar.getMonth()),
                                      calendar.getYear(),
                                      hours,
                                      mins) +
                        String.format("\n\n\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", userItem.getName());

        List<List<ChatMessage.Button>> buttons = new ArrayList<>();
        for (AiRecordStaffResponse staff : staffs) {
            buttons.add(List.of(ChatMessage.Button.builder()
                                                  .title(String.format("\uD83D\uDC64 %s", staff.getName()))
                                                  .callback(String.format("%s;%s;%s;%s;%s;%s;%s",
                                                                          BOT_CONFIRM_SELECT_TIME.getType(),
                                                                          staff.getCalendarId(),
                                                                          hours,
                                                                          mins,
                                                                          userItem.getId(),
                                                                          staff.getId(),
                                                                          EMPTY_PARAM))
                                                  .build()));

        }

        buttons.add(createBackButton(BOT_MAIN.getType()));

        return List.of(ChatMessage.builder()
                                  .text(answer)
                                  .updated(TRUE)
                                  .source(findByType(source))
                                  .chatId(chatId)
                                  .messageId(messageId)
                                  .buttons(buttons)
                                  .build());
    }

    /**
     * Fill error ai search.
     * @param chatId chat id
     * @param messageId message id
     * @param source source
     * @return messages
     */
    private List<ChatMessage> fillErrorSearchRecord(String chatId, String messageId, String source) {
        return List.of(ChatMessage.builder()
                                  .text(AI_RECORD_ERROR_TITLE)
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
        return BOT_AI_RECORD_PROCESS;
    }
}
