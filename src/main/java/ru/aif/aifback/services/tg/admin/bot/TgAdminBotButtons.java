package ru.aif.aifback.services.tg.admin.bot;

import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_BOTS;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_CREATE;
import static ru.aif.aifback.services.tg.enums.TgAdminBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.tg.enums.TgBotType.BOT_RECORD;

import java.util.Objects;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

/**
 * TG admin buttons.
 * @author emelnikov
 */
public final class TgAdminBotButtons {

    public static final String MENU_TITLE = "\uD83D\uDCCB Меню";
    public static final String SELECT_BOT_TITLE = "\uD83C\uDF10 Выберите бота";
    public static final String MY_BOTS_TITLE = "\uD83D\uDCE6 Мои боты";
    public static final String CONNECT_BOT_TITLE = "\uD83C\uDF10 Подключить бота";
    public static final String BACK_BUTTON_TITLE = "⬅ Назад";
    public static final String LINK_TOKEN_TITLE = "\uD83D\uDD03 Привязать TOKEN";
    public static final String DELETE_BOT_TITLE = "❌ Удалить";
    public static final String BOT_STATS_TITLE = "\uD83D\uDCCA Статистика";
    public static final String BOT_ITEMS_TITLE = "\uD83D\uDCE6 Товары/Услуги";
    public static final String BOT_CALENDAR_TITLE = "\uD83D\uDCC5 Календарь";
    public static final String BOT_STAFF_TITLE = "\uD83D\uDC64 Специалисты";
    public static final String BOTS_EMPTY_TITLE = "\uD83D\uDCAD У Вас пока нет ботов";
    public static final String DELETE_BOT_ERROR_ANSWER = "⛔ Не удалось удалить бота. Попробуйте еще раз";
    public static final String DELETE_BOT_SUCCESS_ANSWER = "✅ Бот удален";
    public static final String CREATE_BOT_ERROR_ANSWER = "⛔ Не удалось создать бота. Попробуйте еще раз";
    public static final String CREATE_BOT_SUCCESS_ANSWER = "✅ Бот создан";
    public static final String BOTS_TO_CREATE_EMPTY_TITLE = "\uD83D\uDCAD В данный момент нельзя подключить ботов";
    public static final String BOT_RECORD_SHOW_TITLE = "\uD83D\uDCDD Подробнее";

    /**
     * Create main menu.
     * @return main menu
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard() {
        return new InlineKeyboardMarkup(
                new InlineKeyboardButton(CONNECT_BOT_TITLE).callbackData(BOT_CREATE.getType()),
                new InlineKeyboardButton(MY_BOTS_TITLE).callbackData(BOT_BOTS.getType())
        );
    }

    /**
     * Create back button.
     * @param callback callback
     * @return back button
     */
    public static InlineKeyboardButton[] createBackButton(String callback) {
        return new InlineKeyboardButton[] {
                new InlineKeyboardButton(BACK_BUTTON_TITLE).callbackData(callback),
                new InlineKeyboardButton(MENU_TITLE).callbackData(BOT_MAIN.getType())
        };
    }

    /**
     * Get bot icon.
     * @param type type
     * @return bot icon
     */
    public static String getBotIconByType(String type) {
        String icon = "\uD83C\uDF10";

        if (Objects.equals(type, BOT_RECORD.getType())) {
            icon = "\uD83D\uDCDD";
        }

        return icon;
    }
}
