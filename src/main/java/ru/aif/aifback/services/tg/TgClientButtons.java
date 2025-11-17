package ru.aif.aifback.services.tg;

import java.util.Objects;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.aif.aifback.model.UserItemGroup;
import ru.aif.aifback.services.tg.enums.TgClientTypeBot;

/**
 * TG client buttons.
 * @author emelnikov
 */
public final class TgClientButtons {

    public static final String MENU_TITLE = "✅ Меню";
    public static final String ACTIVE_TITLE = "✅ Активные записи";
    public static final String HISTORY_TITLE = "\uD83D\uDD5C История записей";
    public static final String SETTINGS_TITLE = "\uD83D\uDD27 Настройки";
    public static final String ITEMS_TITLE = "\uD83D\uDCE6 Товары/Услуги";
    public static final String BACK_BUTTON_TITLE = "⬅ Назад";
    public static final String GROUP_EMPTY_TITLE = "✅ Товаров/Услуг пока нет";
    public static final String GROUP_TITLE = "\uD83D\uDD38 %s";

    public final static String BOT_ACTIVE = "bot_active";
    public final static String BOT_HISTORY = "bot_history";
    public final static String BOT_SETTINGS = "bot_settings";
    public final static String BOT_GROUP = "bot_group";
    public final static String BOT_ITEMS = "bot_items";
    public static final String BACK_TO_MAIN_MENU = "back_to_main_menu";

    /**
     * Create main menu.
     * @return main menu
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard(String typeBot) {
        if (Objects.equals(typeBot, TgClientTypeBot.BOT_RECORD.getType())) {
            return new InlineKeyboardMarkup(
                    new InlineKeyboardButton(ACTIVE_TITLE).callbackData(BOT_ACTIVE),
                    new InlineKeyboardButton(ITEMS_TITLE).callbackData(BOT_GROUP),
                    new InlineKeyboardButton(HISTORY_TITLE).callbackData(BOT_HISTORY),
                    new InlineKeyboardButton(SETTINGS_TITLE).callbackData(BOT_SETTINGS)
            );
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
     * Create groups bot menu.
     * @param group group
     * @return groups bot menu
     */
    public static InlineKeyboardButton[] createGroupsBotMenu(UserItemGroup group) {
        return new InlineKeyboardButton[] {
                new InlineKeyboardButton(String.format(GROUP_TITLE, group.getName())).callbackData(String.format("%s;%s", BOT_ITEMS, group.getId()))
        };
    }
}
