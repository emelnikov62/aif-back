package ru.aif.aifback.services.tg;

import static ru.aif.aifback.constants.Constants.BUY_BOT;
import static ru.aif.aifback.constants.Constants.MY_BOTS;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;

/**
 * TG buttons.
 * @author emelnikov
 */
public final class TgButtons {

    public static final String MENU_TITLE = "✅ Меню";
    public static final String MY_BOTS_TITLE = "\uD83D\uDCE6 Мои боты";
    public static final String CONNECT_BOT_TITLE = "\uD83C\uDF10 Подключить бота";

    /**
     * Create main menu.
     * @return main menu
     */
    public static Keyboard createMainMenuKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(MY_BOTS_TITLE).callbackData(MY_BOTS)
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(CONNECT_BOT_TITLE).callbackData(BUY_BOT)
                }
        );
    }
}
