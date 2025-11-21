package ru.aif.aifback.services.tg.client.bot.record;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.DELIMITER_CHAR;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ACTIVE_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BACK_TO_GROUPS_MENU;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ACTIVE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ADD_RECORD;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_CONFIRM_SELECT_TIME;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_GROUP;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_HISTORY;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ITEMS;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ITEM_ADDITIONAL;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_RECORD_SHOW;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_SELECT_DAY;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_SELECT_MONTH;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_SELECT_TIME;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_SELECT_YEAR;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_SETTINGS;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_EMPTY_TIME_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_DAY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_MONTH_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_TIME_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CALENDAR_SELECT_YEAR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.CONFIRM_RECORD_ERROR_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.HISTORY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.ITEMS_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.RECORDS_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.SETTINGS_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.STAFF_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.STAFF_SELECT_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.formatTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.constants.Constants;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.model.user.UserStaff;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.client.ClientService;
import ru.aif.aifback.services.tg.TgBotService;
import ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons;
import ru.aif.aifback.services.tg.enums.TgClientRecordType;
import ru.aif.aifback.services.tg.enums.TgClientTypeBot;
import ru.aif.aifback.services.tg.utils.TgUtils;
import ru.aif.aifback.services.user.UserCalendarService;
import ru.aif.aifback.services.user.UserItemService;

/**
 * TG Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgRecordBotService implements TgBotService {

    private final UserItemService userItemService;
    private final UserCalendarService userCalendarService;
    private final ClientService clientService;
    private final ClientRecordService clientRecordService;

    /**
     * Webhook process.
     * @param webhookRequest webhookAdminRequest
     * @param userBot user bot
     * @return true/false
     */
    @Override
    public Boolean process(TgWebhookRequest webhookRequest, UserBot userBot) {
        if (webhookRequest.isCallback()) {
            processCallback(webhookRequest, userBot);
        } else {
            processNoCallback(webhookRequest, userBot);
        }

        return Boolean.TRUE;
    }

    /**
     * Callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        TelegramBot bot = new TelegramBot(userBot.getToken());
        try {
            String answer = null;
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

            if (Objects.equals(webhookRequest.getText(), BOT_ACTIVE)) {
                answer = processBotActiveRecords(webhookRequest.getChatId(), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_GROUP) || Objects.equals(webhookRequest.getText(), BACK_TO_GROUPS_MENU)) {
                answer = processBotGroups(userBot, keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (webhookRequest.getText().contains(BOT_ITEMS)) {
                String groupId = webhookRequest.getText().split(DELIMITER)[1];
                answer = processBotGroupItems(Long.valueOf(groupId), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_GROUPS_MENU));
            }

            if (webhookRequest.getText().contains(BOT_ITEM_ADDITIONAL)) {
                processBotItemAdditional(webhookRequest.getText().split(DELIMITER)[1], keyboard, webhookRequest.getChatId(), bot);
                return;
            }

            if (webhookRequest.getText().contains(BOT_ADD_RECORD)) {
                String itemId = webhookRequest.getText().split(DELIMITER)[1];
                answer = processBotCalendarYears(Long.valueOf(itemId), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s", BOT_ITEM_ADDITIONAL, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_YEAR)) {
                String year = webhookRequest.getText().split(DELIMITER)[1];
                String itemId = webhookRequest.getText().split(DELIMITER)[2];
                answer = processBotCalendarMonths(Long.valueOf(itemId), Long.valueOf(webhookRequest.getId()), Long.valueOf(year), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s", BOT_ADD_RECORD, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_MONTH)) {
                String month = webhookRequest.getText().split(DELIMITER)[1];
                String year = webhookRequest.getText().split(DELIMITER)[2];
                String itemId = webhookRequest.getText().split(DELIMITER)[3];
                answer = processBotCalendarDays(Long.valueOf(itemId),
                                                Long.valueOf(webhookRequest.getId()),
                                                Long.valueOf(year),
                                                Long.valueOf(month),
                                                keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s", BOT_SELECT_YEAR, year, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_DAY)) {
                String day = webhookRequest.getText().split(DELIMITER)[1];
                String month = webhookRequest.getText().split(DELIMITER)[2];
                String year = webhookRequest.getText().split(DELIMITER)[3];
                String itemId = webhookRequest.getText().split(DELIMITER)[4];
                answer = processBotCalendarTimes(Long.valueOf(itemId),
                                                 Long.valueOf(webhookRequest.getId()),
                                                 Long.valueOf(year),
                                                 Long.valueOf(month),
                                                 Long.valueOf(day),
                                                 keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s;%s", BOT_SELECT_MONTH, month, year, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_TIME)) {
                String hours = webhookRequest.getText().split(DELIMITER)[2];
                String mins = webhookRequest.getText().split(DELIMITER)[3];
                String calendarIds = webhookRequest.getText().split(DELIMITER)[1];
                String itemId = webhookRequest.getText().split(DELIMITER)[4];
                String day = webhookRequest.getText().split(DELIMITER)[5];
                String month = webhookRequest.getText().split(DELIMITER)[6];
                String year = webhookRequest.getText().split(DELIMITER)[7];
                answer = processBotSelectStaff(Long.valueOf(hours), Long.valueOf(mins), Long.valueOf(itemId), calendarIds, keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s;%s;%s", BOT_SELECT_DAY, day, month, year, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_CONFIRM_SELECT_TIME)) {
                String hours = webhookRequest.getText().split(DELIMITER)[2];
                String mins = webhookRequest.getText().split(DELIMITER)[3];
                String calendarId = webhookRequest.getText().split(DELIMITER)[1];
                String itemId = webhookRequest.getText().split(DELIMITER)[4];
                String staffId = webhookRequest.getText().split(DELIMITER)[5];
                answer = processBotConfirmRecord(Long.valueOf(hours),
                                                 Long.valueOf(mins),
                                                 Long.valueOf(itemId),
                                                 Long.valueOf(calendarId),
                                                 Long.valueOf(staffId),
                                                 Long.valueOf(webhookRequest.getId()),
                                                 webhookRequest.getChatId(),
                                                 keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_HISTORY)) {
                answer = HISTORY_TITLE;
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_SETTINGS)) {
                answer = SETTINGS_TITLE;
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BACK_TO_MAIN_MENU)) {
                answer = MENU_TITLE;
                keyboard = TgClientBotRecordButtons.createMainMenuKeyboard(userBot.getBot().getType());
            }

            if (Objects.isNull(answer)) {
                TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), TgAdminBotButtons.MENU_TITLE, bot);
            } else {
                TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), answer, keyboard, bot);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            TgUtils.sendMessage(TG_LOG_ID, e.getMessage(), bot);
        }
    }

    /**
     * No callback process.
     * @param webhookRequest webhook request
     * @param userBot user bot
     */
    @Override
    public void processNoCallback(TgWebhookRequest webhookRequest, UserBot userBot) {
        TelegramBot bot = new TelegramBot(userBot.getToken());
        TgUtils.sendMessage(Long.valueOf(webhookRequest.getChatId()), MENU_TITLE,
                            TgClientBotRecordButtons.createMainMenuKeyboard(userBot.getBot().getType()), bot);
    }

    /**
     * Get bot type.
     * @return bot type
     */
    @Override
    public TgClientTypeBot getBotType() {
        return TgClientTypeBot.BOT_RECORD;
    }

    /**
     * Process bot groups button.
     * @param userBot user bot
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotGroups(UserBot userBot, InlineKeyboardMarkup keyboard) {
        List<UserItemGroup> groups = userItemService.getUserItemGroupsAndActive(userBot.getId());
        if (groups.isEmpty()) {
            return GROUP_EMPTY_TITLE;
        }

        groups.forEach(group -> keyboard.addRow(TgClientBotRecordButtons.createGroupsBotMenu(group)));
        return ITEMS_TITLE;
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

        items.forEach(item -> keyboard.addRow(TgClientBotRecordButtons.createGroupItemsBotMenu(item)));

        return String.format(GROUP_TITLE, userItemGroup.get().getName());
    }

    /**
     * Process select years.
     * @param keyboard keyboard
     * @param userItemId user item id
     * @return answer
     */
    private String processBotCalendarYears(Long userItemId, InlineKeyboardMarkup keyboard) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int nextYear = currentYear + 1;

        keyboard.addRow(new InlineKeyboardButton(String.valueOf(currentYear)).callbackData(
                                String.format("%s;%s;%s", BOT_SELECT_YEAR, currentYear, userItemId)),
                        new InlineKeyboardButton(String.valueOf(nextYear)).callbackData(
                                String.format("%s;%s;%s", BOT_SELECT_YEAR, nextYear, userItemId)));

        return CALENDAR_SELECT_YEAR_TITLE;
    }

    /**
     * Process select month.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param keyboard keyboard
     */
    private String processBotCalendarMonths(Long userItemId, Long id, Long year, InlineKeyboardMarkup keyboard) {
        List<Long> months = userCalendarService.findAllMonthsByYear(year, id);
        if (months.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        while (num < months.size()) {
            InlineKeyboardButton btn = new InlineKeyboardButton(TgUtils.getMonthByNumber(months.get(num))).callbackData(
                    String.format("%s;%s;%s;%s", BOT_SELECT_MONTH, months.get(num), year, userItemId));
            btns.add(btn);

            num++;

            if (num % 4 == 0) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                btns.clear();
            }
        }

        if (!btns.isEmpty()) {
            keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
        }

        return String.format(CALENDAR_SELECT_MONTH_TITLE, year);
    }

    /**
     * Process select days.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param keyboard keyboard
     */
    private String processBotCalendarDays(Long userItemId, Long id, Long year, Long month, InlineKeyboardMarkup keyboard) {
        List<Long> days = userCalendarService.findAllDaysByMonthAndYear(year, month, id);
        if (days.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        while (num < days.size()) {
            String title = String.format("%s (%s)", days.get(num), TgUtils.getDayOfWeek(days.get(num), month, year));
            InlineKeyboardButton btn = new InlineKeyboardButton(title).callbackData(
                    String.format("%s;%s;%s;%s;%s", BOT_SELECT_DAY, days.get(num), month, year, userItemId));
            btns.add(btn);

            num++;

            if (num % 5 == 0) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                btns.clear();
            }
        }

        if (!btns.isEmpty()) {
            keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
        }

        return String.format(CALENDAR_SELECT_DAY_TITLE, TgUtils.getMonthByNumber(month));
    }

    /**
     * Process select times.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param day day
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotCalendarTimes(Long userItemId, Long id, Long year, Long month, Long day, InlineKeyboardMarkup keyboard) {
        List<UserCalendar> calendars = userCalendarService.findAllDaysByMonthAndYearAndDay(year, month, day, id);
        if (calendars.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        Optional<UserItem> userItem = userItemService.findUserItemById(userItemId);
        if (userItem.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        Map<String, List<ClientRecordTime>> times = new HashMap<>();
        for (UserCalendar calendar : calendars) {
            List<ClientRecord> records = clientRecordService.findAllRecordsByStaffAndDayAndStatus(
                    calendar.getAifUserStaffId(), calendar.getId(), id, TgClientRecordType.ACTIVE.getType());

            List<ClientRecordTime> timesList = TgUtils.formatTimeCalendar(calendar, userItem.get(), userItemService.getMinUserItem(id), records);
            if (timesList.isEmpty()) {
                continue;
            }

            timesList.forEach(time -> {
                String key = String.format("%02d:%02d", time.getHours(), time.getMins());

                if (!times.containsKey(key)) {
                    times.put(key, new ArrayList<>());
                }
                times.get(key).add(time);
            });
        }

        if (times.isEmpty()) {
            return CALENDAR_EMPTY_TIME_TITLE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        for (Map.Entry<String, List<ClientRecordTime>> entry : times.entrySet()
                                                                    .stream()
                                                                    .sorted(Comparator.comparingInt(o -> o.getValue().get(0).getHours()))
                                                                    .toList()) {
            if (entry.getValue().size() == 1) {
                btns.add(new InlineKeyboardButton(entry.getKey()).callbackData(String.format("%s;%s;%s;%s;%s;%s",
                                                                                             BOT_CONFIRM_SELECT_TIME,
                                                                                             entry.getValue().get(0).getCalendarId(),
                                                                                             entry.getValue().get(0).getHours(),
                                                                                             entry.getValue().get(0).getMins(),
                                                                                             userItemId,
                                                                                             entry.getValue().get(0).getStaffId())));
            } else {
                String listCalendarIds = Strings.join(entry.getValue().stream().map(ClientRecordTime::getCalendarId).toList(),
                                                      Constants.DELIMITER_CHAR.charAt(0));
                btns.add(new InlineKeyboardButton(entry.getKey()).callbackData(String.format("%s;%s;%s;%s;%s;%s;%s;%s",
                                                                                             BOT_SELECT_TIME,
                                                                                             listCalendarIds,
                                                                                             entry.getValue().get(0).getHours(),
                                                                                             entry.getValue().get(0).getMins(),
                                                                                             userItemId,
                                                                                             day,
                                                                                             month,
                                                                                             year)));
            }

            if (++num % 5 == 0) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                btns.clear();
            }
        }

        if (!btns.isEmpty()) {
            keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
        }

        return String.format(CALENDAR_SELECT_TIME_TITLE, day);
    }

    /**
     * Process select staff.
     * @param hours hours
     * @param mins mins
     * @param itemId itemId
     * @param calendarIds calendar ids
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotSelectStaff(Long hours, Long mins, Long itemId, String calendarIds, InlineKeyboardMarkup keyboard) {
        List<String> stringCalendarIds = Arrays.stream(calendarIds.split(DELIMITER_CHAR)).toList();
        if (stringCalendarIds.isEmpty()) {
            return STAFF_EMPTY_TITLE;
        }

        for (String calendarId : stringCalendarIds) {
            Optional<UserCalendar> userCalendar = userCalendarService.findById(Long.valueOf(calendarId));
            if (userCalendar.isEmpty()) {
                continue;
            }

            if (Objects.isNull(userCalendar.get().getStaff())) {
                continue;
            }

            UserStaff userStaff = userCalendar.get().getStaff();
            String staffFio = String.format("%s %s %s", userStaff.getSurname(), userStaff.getName(), userStaff.getThird());
            keyboard.addRow(new InlineKeyboardButton(staffFio).callbackData(
                    String.format("%s;%s;%s;%s;%s;%s", BOT_CONFIRM_SELECT_TIME, userCalendar.get().getId(), hours, mins, itemId, userStaff.getId())));
        }

        return keyboard.inlineKeyboard().length == 0 ? STAFF_EMPTY_TITLE : STAFF_SELECT_TITLE;
    }

    /**
     * Process confirm client record.
     * @param hours hours
     * @param mins mins
     * @param itemId item id
     * @param calendarId calendar id
     * @param staffId staff id
     * @param id user bot id
     * @param clientTgId client tg id
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotConfirmRecord(Long hours, Long mins, Long itemId, Long calendarId, Long staffId, Long id, String clientTgId,
                                           InlineKeyboardMarkup keyboard) {
        Long clientId = clientService.getClientIdOrCreate(clientTgId);
        if (Objects.isNull(clientId)) {
            return CONFIRM_RECORD_ERROR_TITLE;
        }

        Optional<Long> clientRecordId = clientRecordService.addClientRecord(clientId, id, itemId, calendarId, staffId, hours, mins);
        if (clientRecordId.isEmpty()) {
            return CONFIRM_RECORD_ERROR_TITLE;
        }

        return fillClientRecords(keyboard, clientId, TgClientRecordType.ACTIVE.getType()) ? ACTIVE_TITLE : CONFIRM_RECORD_ERROR_TITLE;
    }

    /**
     * Fill client records.
     * @param keyboard keyboard
     * @param clientId client id
     * @param status status
     */
    private Boolean fillClientRecords(InlineKeyboardMarkup keyboard, Long clientId, String status) {
        List<ClientRecord> clientRecords = clientRecordService.findAllByClientIdAndStatus(clientId, status);
        clientRecords.forEach(clientRecord -> {
            String dayOfWeek = TgUtils.getDayOfWeek(clientRecord.getUserCalendar().getDay(),
                                                    clientRecord.getUserCalendar().getMonth(),
                                                    clientRecord.getUserCalendar().getYear());
            keyboard.addRow(new InlineKeyboardButton(String.format("\uD83D\uDCC5 %s %s %s %02d:%02d (%s)",
                                                                   dayOfWeek,
                                                                   clientRecord.getUserCalendar().getDay(),
                                                                   TgUtils.getMonthByNumber(clientRecord.getUserCalendar().getMonth()),
                                                                   clientRecord.getHours(),
                                                                   clientRecord.getMins(),
                                                                   clientRecord.getUserItem().getName()))
                                    .callbackData(String.format("%s;%s", BOT_RECORD_SHOW, clientRecord.getId())));
        });

        return keyboard.inlineKeyboard().length == 0 ? Boolean.FALSE : Boolean.TRUE;
    }

    /**
     * Process active client records.
     * @param clientTgId client tg id
     * @param keyboard keyboard
     * @return answer
     */
    private String processBotActiveRecords(String clientTgId, InlineKeyboardMarkup keyboard) {
        Long clientId = clientService.getClientIdOrCreate(clientTgId);
        if (Objects.isNull(clientId)) {
            return RECORDS_EMPTY_TITLE;
        }

        return fillClientRecords(keyboard, clientId, TgClientRecordType.ACTIVE.getType()) ? ACTIVE_TITLE : RECORDS_EMPTY_TITLE;
    }

    /**
     * Process bot additional item.
     * @param itemId item id
     * @param keyboard keuboard
     * @param chatId chat id
     * @param bot bot
     */
    private void processBotItemAdditional(String itemId, InlineKeyboardMarkup keyboard, String chatId, TelegramBot bot) {
        Optional<UserItem> userItem = userItemService.findUserItemById(Long.valueOf(itemId));
        if (userItem.isPresent()) {
            Optional<UserItemGroup> group = userItemService.findUserItemGroupByItemId(userItem.get().getAifUserItemGroupId());

            if (group.isPresent()) {
                String answer = String.format("\uD83D\uDD38 <b>Группа:</b> %s \n\n", group.get().getName())
                                + String.format("\uD83D\uDCC3 <b>Наименование:</b> %s \n\n", userItem.get().getName())
                                + String.format("\uD83D\uDD5B <b>Продолжительность:</b> %s \n\n",
                                                formatTime(userItem.get().getHours().toString(), userItem.get().getMins().toString()))
                                + String.format("\uD83D\uDCB5 <b>Стоимость:</b> %s \n\n", String.format("%s руб.", userItem.get().getAmount()));

                keyboard.addRow(TgClientBotRecordButtons.createAddRecordButton(userItem.get()));
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s", BOT_ITEMS, group.get().getId())));

                TgUtils.sendPhoto(Long.valueOf(chatId), Base64.getDecoder().decode(userItem.get().getFileData()), answer, keyboard, bot);
            }
        }
    }

}
