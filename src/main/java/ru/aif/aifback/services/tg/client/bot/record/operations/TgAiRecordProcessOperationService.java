package ru.aif.aifback.services.tg.client.bot.record.operations;

import static java.lang.Boolean.TRUE;

import static ru.aif.aifback.constants.Constants.EMPTY_PARAM;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.AI_RECORD_CONFIRM_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.AI_RECORD_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.AI_RECORD_PROCESS_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.createBackButton;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_AI_RECORD_PROCESS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.utils.TgUtils.deleteMessage;
import static ru.aif.aifback.services.tg.utils.TgUtils.getDayOfWeek;
import static ru.aif.aifback.services.tg.utils.TgUtils.getMonthByNumber;
import static ru.aif.aifback.services.tg.utils.TgUtils.sendMessage;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.response.AiRecordResponse;
import ru.aif.aifback.model.response.AiRecordStaffResponse;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.services.ai.recognize.VoiceRecognizeService;
import ru.aif.aifback.services.ai.record.RecordSearchService;
import ru.aif.aifback.services.tg.client.TgClientBotOperationService;
import ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG AI record operation API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgAiRecordProcessOperationService implements TgClientBotOperationService {

    private final VoiceRecognizeService voiceRecognizeService;
    private final RecordSearchService recordSearchService;
    private final UserItemService userItemService;
    private final UserCalendarService userCalendarService;

    /**
     * Main processing.
     * @param webhookRequest webhookRequest
     * @param userBot user bot
     * @param bot telegram bot
     */
    @Override
    public void process(TgWebhookRequest webhookRequest, UserBot userBot, TelegramBot bot) {
        String result = voiceRecognizeService.recognize(webhookRequest, userBot);
        if (Objects.isNull(result)) {
            sendErrorSearchRecord(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot);
            return;
        }

        Integer waitMessageId = sendMessage(
                webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), AI_RECORD_PROCESS_TITLE, bot, TRUE);

        AiRecordResponse response = recordSearchService.search(result, userBot);
        if (Objects.isNull(response) || response.getStaffs().isEmpty()) {
            sendErrorSearchRecord(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot);
            return;
        }

        UserItem userItem = userItemService.findUserItemById(Long.valueOf(response.getItemId())).orElse(null);
        if (Objects.isNull(userItem)) {
            sendErrorSearchRecord(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot);
            return;
        }

        Long hours = Long.valueOf(response.getHours());
        Long mins = Long.valueOf(response.getMins());
        List<AiRecordStaffResponse> staffs = response.getStaffs().stream().filter(f -> Objects.nonNull(f.getCalendarId())).toList();
        if (staffs.isEmpty()) {
            sendErrorSearchRecord(webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot);
            return;
        }

        if (Objects.nonNull(waitMessageId)) {
            deleteMessage(webhookRequest.getChatId(), waitMessageId, bot);
        }

        if (staffs.size() == 1) {
            AiRecordStaffResponse staff = staffs.get(0);
            processOneStaff(staff, hours, mins, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot, userItem);
        } else {
            processAllStaffs(staffs, hours, mins, webhookRequest.getChatId(), Integer.parseInt(webhookRequest.getMessageId()), bot, userItem);
        }
    }

    /**
     * Process one staff.
     * @param staff staff
     * @param hours hours
     * @param mins mins
     * @param chatId chat id
     * @param messageId message id
     * @param bot telegram bot
     * @param userItem user item
     */
    private void processOneStaff(AiRecordStaffResponse staff, Long hours, Long mins, String chatId, int messageId, TelegramBot bot,
                                 UserItem userItem) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        UserCalendar calendar = userCalendarService.findById(Long.valueOf(staff.getCalendarId())).orElse(null);
        if (Objects.isNull(calendar)) {
            sendErrorSearchRecord(chatId, messageId, bot);
            return;
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

        keyboard.addRow(new InlineKeyboardButton(AI_RECORD_CONFIRM_TITLE).callbackData(
                String.format("%s;%s;%s;%s;%s;%s;%s",
                              BOT_CONFIRM_SELECT_TIME.getType(),
                              calendar.getId(),
                              hours,
                              mins,
                              userItem.getId(),
                              staff.getId(),
                              EMPTY_PARAM)));
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));

        sendMessage(chatId, messageId, answer, keyboard, bot, TRUE);
    }

    /**
     * Process all staffs.
     * @param staffs staffs
     * @param hours hours
     * @param mins mins
     * @param chatId chat id
     * @param messageId message id
     * @param bot bot
     * @param userItem user item
     */
    void processAllStaffs(List<AiRecordStaffResponse> staffs, Long hours, Long mins, String chatId, int messageId, TelegramBot bot,
                          UserItem userItem) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        UserCalendar calendar = userCalendarService.findById(Long.valueOf(staffs.get(0).getCalendarId())).orElse(null);
        if (Objects.isNull(calendar)) {
            sendErrorSearchRecord(chatId, messageId, bot);
            return;
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

        for (AiRecordStaffResponse staff : staffs) {
            keyboard.addRow(new InlineKeyboardButton(String.format("\uD83D\uDC64 %s", staff.getName()))
                                    .callbackData(String.format("%s;%s;%s;%s;%s;%s;%s",
                                                                BOT_CONFIRM_SELECT_TIME.getType(),
                                                                staff.getCalendarId(),
                                                                hours,
                                                                mins,
                                                                userItem.getId(),
                                                                staff.getId(),
                                                                EMPTY_PARAM)));

        }

        keyboard.addRow(createBackButton(BOT_MAIN.getType()));
        sendMessage(chatId, messageId, answer, keyboard, bot, TRUE);
    }

    /**
     * Send error ai search.
     * @param chatId chat id
     * @param messageId message id
     * @param bot bot
     */
    private void sendErrorSearchRecord(String chatId, int messageId, TelegramBot bot) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(createBackButton(BOT_MAIN.getType()));
        sendMessage(chatId, messageId, AI_RECORD_ERROR_TITLE, keyboard, bot, TRUE);
    }

    /**
     * Get bot operation type.
     * @return bot operation type
     */
    @Override
    public TgClientRecordBotOperationType getOperationType() {
        return BOT_AI_RECORD_PROCESS;
    }
}
