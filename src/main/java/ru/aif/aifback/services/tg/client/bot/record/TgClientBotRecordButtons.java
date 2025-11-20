package ru.aif.aifback.services.tg.client.bot.record;

import java.util.Objects;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.aif.aifback.model.user.UserItem;
import ru.aif.aifback.model.user.UserItemGroup;
import ru.aif.aifback.services.tg.enums.TgClientTypeBot;

/**
 * TG client buttons.
 * @author emelnikov
 */
public final class TgClientBotRecordButtons {

    public static final String MENU_TITLE = "✅ Меню";
    public static final String ACTIVE_TITLE = "✅ Активные записи";
    public static final String HISTORY_TITLE = "\uD83D\uDD5C История записей";
    public static final String SETTINGS_TITLE = "\uD83D\uDD27 Настройки";
    public static final String ITEMS_TITLE = "\uD83D\uDCE6 Услуги";
    public static final String BACK_BUTTON_TITLE = "⬅ Назад";
    public static final String GROUP_EMPTY_TITLE = "✅ Услуг пока нет";
    public static final String GROUP_TITLE = "\uD83D\uDD38 %s";
    public static final String ITEM_TITLE = "\uD83D\uDD38 %s (\uD83D\uDCB5 %s, \uD83D\uDD5B %s)";
    public static final String ADD_RECORD_TITLE = "\uD83D\uDCC6 Записаться";
    public static final String CALENDAR_SELECT_YEAR_TITLE = "\uD83D\uDCC6 Выберите год";
    public static final String CALENDAR_SELECT_MONTH_TITLE = "\uD83D\uDCC6 Выберите месяц";
    public static final String CALENDAR_SELECT_DAY_TITLE = "\uD83D\uDCC6 Выберите день";
    public static final String CALENDAR_SELECT_TIME_TITLE = "\uD83D\uDCC6 Выберите время";

    public final static String BOT_ACTIVE = "bot_active";
    public final static String BOT_HISTORY = "bot_history";
    public final static String BOT_SETTINGS = "bot_settings";
    public final static String BOT_GROUP = "bot_group";
    public final static String BOT_ITEMS = "bot_items";
    public final static String BOT_ITEM_ADDITIONAL = "bot_item_additional";
    public static final String BACK_TO_MAIN_MENU = "back_to_main_menu";
    public static final String BACK_TO_GROUPS_MENU = "back_to_groups_menu";
    public static final String BOT_SELECT_YEAR = "back_select_year";
    public static final String BOT_SELECT_MONTH = "back_select_month";
    public static final String BOT_SELECT_DAY = "back_select_day";
    public static final String BOT_SELECT_TIME = "back_select_time";
    public static final String BOT_ADD_RECORD = "back_add_record";

    /**
     * Create main menu.
     * @return main menu
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard(String typeBot) {
        if (Objects.equals(typeBot, TgClientTypeBot.BOT_RECORD.getType())) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.addRow(new InlineKeyboardButton(ACTIVE_TITLE).callbackData(BOT_ACTIVE));
            keyboard.addRow(new InlineKeyboardButton(ITEMS_TITLE).callbackData(BOT_GROUP));
            keyboard.addRow(new InlineKeyboardButton(HISTORY_TITLE).callbackData(BOT_HISTORY));
            keyboard.addRow(new InlineKeyboardButton(SETTINGS_TITLE).callbackData(BOT_SETTINGS));
            return keyboard;
        }

        return null;
    }

    /**
     * Create back button.
     * @param callback callback
     * @return back button
     */
    public static InlineKeyboardButton createBackButton(String callback) {
        return new InlineKeyboardButton(BACK_BUTTON_TITLE).callbackData(callback);
    }

    /**
     * Create add record button.
     * @param userItem user item
     * @return add record button
     */
    public static InlineKeyboardButton createAddRecordButton(UserItem userItem) {
        return new InlineKeyboardButton(ADD_RECORD_TITLE).callbackData(String.format("%s;%s", BOT_ADD_RECORD, userItem.getId()));
    }

    /**
     * Create groups bot menu.
     * @param group group
     * @return groups bot menu
     */
    public static InlineKeyboardButton[] createGroupsBotMenu(UserItemGroup group) {
        return new InlineKeyboardButton[] {
                new InlineKeyboardButton(String.format(GROUP_TITLE, group.getName())).callbackData(String.format("%s;%s", BOT_ITEMS, group.getId()))
        };
    }

    /**
     * Create group items bot menu.
     * @param item item
     * @return group items bot menu
     */
    public static InlineKeyboardButton[] createGroupItemsBotMenu(UserItem item) {
        return new InlineKeyboardButton[] {
                new InlineKeyboardButton(String.format(ITEM_TITLE, item.getName(), item.getAmount(),
                                                       formatTime(item.getHours().toString(), item.getMins().toString())))
                        .callbackData(String.format("%s;%s", BOT_ITEM_ADDITIONAL, item.getId()))
        };
    }

    /**
     * Format time.
     * @param hours hours
     * @param mins mins
     * @return time
     */
    public static String formatTime(String hours, String mins) {
        return String.format("%s:%s", hours.length() < 2 ? "0" + hours : hours, mins.length() < 2 ? "0" + mins : mins);
    }

}
