package ru.aif.aifback.services.tg.client.bot.record;

import static ru.aif.aifback.constants.Constants.DELIMITER;
import static ru.aif.aifback.constants.Constants.TG_LOG_ID;
import static ru.aif.aifback.services.tg.admin.bot.TgAdminBotButtons.BACK_TO_MAIN_MENU;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BACK_TO_GROUPS_MENU;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ACTIVE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ADD_RECORD;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_GROUP;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_HISTORY;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ITEMS;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.BOT_ITEM_ADDITIONAL;
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
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.GROUP_EMPTY_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.MENU_TITLE;
import static ru.aif.aifback.services.tg.client.bot.record.TgClientBotRecordButtons.formatTime;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.client.ClientRecordTime;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.model.user.UserCalendar;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
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
                answer = MENU_TITLE;
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_GROUP) || Objects.equals(webhookRequest.getText(), BACK_TO_GROUPS_MENU)) {
                answer = MENU_TITLE;
                if (!processBotGroups(userBot, keyboard)) {
                    answer = GROUP_EMPTY_TITLE;
                }
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (webhookRequest.getText().contains(BOT_ITEMS)) {
                answer = MENU_TITLE;
                String groupId = webhookRequest.getText().split(DELIMITER)[1];
                if (!processBotGroupItems(Long.valueOf(groupId), keyboard)) {
                    answer = GROUP_EMPTY_TITLE;
                }
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_GROUPS_MENU));
            }

            if (webhookRequest.getText().contains(BOT_ITEM_ADDITIONAL)) {
                processBotItemAdditional(webhookRequest.getText().split(DELIMITER)[1], keyboard, webhookRequest.getChatId(), bot);
                return;
            }

            if (webhookRequest.getText().contains(BOT_ADD_RECORD)) {
                String itemId = webhookRequest.getText().split(DELIMITER)[1];
                answer = CALENDAR_SELECT_YEAR_TITLE;
                processBotCalendarYears(Long.valueOf(itemId), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s", BOT_ITEM_ADDITIONAL, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_YEAR)) {
                String year = webhookRequest.getText().split(DELIMITER)[1];
                String itemId = webhookRequest.getText().split(DELIMITER)[2];
                answer = String.format(CALENDAR_SELECT_MONTH_TITLE, year);
                processBotCalendarMonths(Long.valueOf(itemId), Long.valueOf(webhookRequest.getId()), Long.valueOf(year), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s", BOT_ADD_RECORD, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_MONTH)) {
                String month = webhookRequest.getText().split(DELIMITER)[1];
                String year = webhookRequest.getText().split(DELIMITER)[2];
                String itemId = webhookRequest.getText().split(DELIMITER)[3];
                answer = String.format(CALENDAR_SELECT_DAY_TITLE, TgUtils.getMonthByNumber(Long.valueOf(month)));
                processBotCalendarDays(Long.valueOf(itemId), Long.valueOf(webhookRequest.getId()), Long.valueOf(year), Long.valueOf(month), keyboard);
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s", BOT_SELECT_YEAR, year, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_DAY)) {
                String day = webhookRequest.getText().split(DELIMITER)[1];
                String month = webhookRequest.getText().split(DELIMITER)[2];
                String year = webhookRequest.getText().split(DELIMITER)[3];
                String itemId = webhookRequest.getText().split(DELIMITER)[4];

                answer = String.format(CALENDAR_SELECT_TIME_TITLE, day);
                if (!processBotCalendarTimes(Long.valueOf(itemId),
                                             Long.valueOf(webhookRequest.getId()),
                                             Long.valueOf(year),
                                             Long.valueOf(month),
                                             Long.valueOf(day),
                                             keyboard)) {
                    answer = CALENDAR_EMPTY_TIME_TITLE;
                }

                keyboard.addRow(TgClientBotRecordButtons.createBackButton(String.format("%s;%s;%s;%s", BOT_SELECT_MONTH, month, year, itemId)));
            }

            if (webhookRequest.getText().contains(BOT_SELECT_TIME)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_HISTORY)) {
                answer = MENU_TITLE;
                keyboard.addRow(TgClientBotRecordButtons.createBackButton(TgClientBotRecordButtons.BACK_TO_MAIN_MENU));
            }

            if (Objects.equals(webhookRequest.getText(), BOT_SETTINGS)) {
                answer = MENU_TITLE;
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
     * @return true/false
     */
    private boolean processBotGroups(UserBot userBot, InlineKeyboardMarkup keyboard) {
        List<UserItemGroup> groups = userItemService.getUserItemGroupsAndActive(userBot.getId());
        if (groups.isEmpty()) {
            return Boolean.FALSE;
        }

        groups.forEach(group -> keyboard.addRow(TgClientBotRecordButtons.createGroupsBotMenu(group)));

        return Boolean.TRUE;
    }

    /**
     * Process bot group items button.
     * @param groupId group id
     * @param keyboard keyboard
     * @return true/false
     */
    private boolean processBotGroupItems(Long groupId, InlineKeyboardMarkup keyboard) {
        List<UserItem> items = userItemService.getUserItemsByGroupIdAndActive(groupId);
        if (items.isEmpty()) {
            return Boolean.FALSE;
        }

        items.forEach(item -> keyboard.addRow(TgClientBotRecordButtons.createGroupItemsBotMenu(item)));

        return Boolean.TRUE;
    }

    /**
     * Process select years.
     * @param keyboard keyboard
     * @param userItemId user item id
     */
    private void processBotCalendarYears(Long userItemId, InlineKeyboardMarkup keyboard) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int nextYear = currentYear + 1;

        keyboard.addRow(new InlineKeyboardButton(String.valueOf(currentYear)).callbackData(
                                String.format("%s;%s;%s", BOT_SELECT_YEAR, currentYear, userItemId)),
                        new InlineKeyboardButton(String.valueOf(nextYear)).callbackData(
                                String.format("%s;%s;%s", BOT_SELECT_YEAR, nextYear, userItemId)));
    }

    /**
     * Process select month.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param keyboard keyboard
     */
    private void processBotCalendarMonths(Long userItemId, Long id, Long year, InlineKeyboardMarkup keyboard) {
        List<Long> months = userCalendarService.findAllMonthsByYear(year, id);
        if (months.isEmpty()) {
            return;
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
    }

    /**
     * Process select days.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param keyboard keyboard
     */
    private void processBotCalendarDays(Long userItemId, Long id, Long year, Long month, InlineKeyboardMarkup keyboard) {
        List<Long> days = userCalendarService.findAllDaysByMonthAndYear(year, month, id);
        if (days.isEmpty()) {
            return;
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
    }

    /**
     * Process select times.
     * @param userItemId user item id
     * @param id id
     * @param year year
     * @param month month
     * @param day day
     * @param keyboard keyboard
     * @return true/false
     */
    private Boolean processBotCalendarTimes(Long userItemId, Long id, Long year, Long month, Long day, InlineKeyboardMarkup keyboard) {
        List<UserCalendar> calendars = userCalendarService.findAllDaysByMonthAndYearAndDay(year, month, day, id);
        if (calendars.isEmpty()) {
            return Boolean.FALSE;
        }

        Optional<UserItem> userItem = userItemService.findUserItemById(userItemId);
        if (userItem.isEmpty()) {
            return Boolean.FALSE;
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
            return Boolean.FALSE;
        }

        List<InlineKeyboardButton> btns = new ArrayList<>();
        int num = 0;
        for (Map.Entry<String, List<ClientRecordTime>> entry : times.entrySet()
                                                                    .stream()
                                                                    .sorted(Comparator.comparingInt(o -> o.getValue().get(0).getHours()))
                                                                    .toList()) {
            btns.add(new InlineKeyboardButton(entry.getKey()).callbackData(String.format("%s;%s", BOT_SELECT_TIME, userItemId)));

            if (++num % 5 == 0) {
                keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
                btns.clear();
            }
        }

        if (!btns.isEmpty()) {
            keyboard.addRow(btns.toArray(new InlineKeyboardButton[0]));
        }

        return Boolean.TRUE;
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
