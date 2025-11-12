package ru.aif.aifback.services.tg;

import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

/**
 * TG buttons.
 * @author emelnikov
 */
public final class TgButtons {

    public static final String MENU_TITLE = "✅ Меню";
    public static final String SELECT_BOT_TITLE = "✅ Выберите бота";
    public static final String MY_BOTS_TITLE = "\uD83D\uDCE6 Мои боты";
    public static final String CONNECT_BOT_TITLE = "\uD83C\uDF10 Подключить бота";
    public static final String BACK_BUTTON_TITLE = "⬅ Назад";
    public static final String LINK_TOKEN_TITLE = "✅ Привязать TOKEN";
    public static final String DELETE_BOT_TITLE = "⛔ Удалить";
    public static final String BOT_STATS_TITLE = "\uD83D\uDCCA Статистика";
    public static final String BOT_SETTINGS_TITLE = "\uD83D\uDD27 Настройки";

    public final static String BOT_SELECT = "bot_select";
    public final static String MY_BOTS = "my_bots";
    public final static String BUY_BOT = "buy_bot";
    public final static String BOT_DELETE = "bot_delete";
    public final static String BOT_STATS = "bot_stats";
    public final static String BOT_SETTINGS = "bot_settings";
    public static final String BACK_TO_MAIN_MENU = "back_to_main_menu";
    public static final String BOT_CREATE = "bot_create";

    /**
     * Create main menu.
     * @return main menu
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton(CONNECT_BOT_TITLE).callbackData(BUY_BOT),
                new InlineKeyboardButton(MY_BOTS_TITLE).callbackData(MY_BOTS)
        );
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
     * Create link bot button.
     * @param id id
     * @return link bot button
     */
    public static InlineKeyboardButton createLinkBotButton(String id) {
        return new InlineKeyboardButton(LINK_TOKEN_TITLE)
                .webApp(new WebAppInfo("https://aif-admin-emelnikov62.amvera.io/link-bot-form?id=" + id));
    }

    /**
     * Create delete bot button.
     * @param id id
     * @return delete bot button
     */
    public static InlineKeyboardButton createDeleteBotButton(String id) {
        return new InlineKeyboardButton(DELETE_BOT_TITLE).callbackData(String.format("%s;%s", BOT_DELETE, id));
    }

    /**
     * Create selected bot menu.
     * @param id id
     * @return selected bot menu
     */
    public static InlineKeyboardButton[] createSelectedBotMenu(String id) {
        return new InlineKeyboardButton[] {
                new InlineKeyboardButton(BOT_STATS_TITLE).callbackData(String.format("%s;%s", BOT_STATS, id)),
                new InlineKeyboardButton(BOT_SETTINGS_TITLE).callbackData(String.format("%s;%s", BOT_SETTINGS, id))
        };
    }
}
