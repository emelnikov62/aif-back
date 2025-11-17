package ru.aif.aifback.services.tg;

import java.util.Objects;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import ru.aif.aifback.services.tg.enums.TgClientTypeBot;

/**
 * TG client buttons.
 * @author emelnikov
 */
public final class TgClientButtons {

    public static final String MENU_TITLE = "✅ Меню";
    public static final String ITEMS_TITLE = "\uD83D\uDCE6 Товары/Услуги";
    public static final String BACK_BUTTON_TITLE = "⬅ Назад";

    public final static String BOT_ITEMS = "bot_items";
    public static final String BACK_TO_MAIN_MENU = "back_to_main_menu";

    /**
     * Create main menu.
     * @return main menu
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard(String typeBot) {
        if (Objects.equals(typeBot, TgClientTypeBot.BOT_RECORD.getType())) {
            return new InlineKeyboardMarkup(
                    new InlineKeyboardButton(ITEMS_TITLE).callbackData(BOT_ITEMS)
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
}
