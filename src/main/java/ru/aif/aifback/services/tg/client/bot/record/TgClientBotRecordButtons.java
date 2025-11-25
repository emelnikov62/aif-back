package ru.aif.aifback.services.tg.client.bot.record;

import static ru.aif.aifback.services.tg.enums.TgBotType.BOT_RECORD;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_GROUP;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_RECORDS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordBotOperationType.BOT_SETTINGS;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.ACTIVE;
import static ru.aif.aifback.services.tg.enums.TgClientRecordType.NO_ACTIVE;

import java.util.Objects;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

/**
 * TG client buttons.
 * @author emelnikov
 */
public final class TgClientBotRecordButtons {

    public static final String MENU_TITLE = "\uD83D\uDCCB Меню";
    public static final String ACTIVE_TITLE = "\uD83D\uDD35 Активные записи";
    public static final String HISTORY_TITLE = "⏳ История записей";
    public static final String SETTINGS_TITLE = "\uD83D\uDD27 Настройки";
    public static final String ITEMS_TITLE = "\uD83D\uDCE6 Услуги";
    public static final String BACK_BUTTON_TITLE = "⬅ Назад";
    public static final String GROUP_EMPTY_TITLE = "\uD83D\uDEAB Услуг пока нет";
    public static final String GROUP_TITLE = "\uD83D\uDD38 %s";
    public static final String ITEM_TITLE = "\uD83D\uDD38 %s (\uD83D\uDCB5 %s, \uD83D\uDD5B %02d:%02d)";
    public static final String ADD_RECORD_TITLE = "\uD83D\uDCC6 Записаться";
    public static final String CALENDAR_SELECT_YEAR_TITLE = "\uD83D\uDCC6 Выберите год";
    public static final String CALENDAR_SELECT_MONTH_TITLE = "\uD83D\uDCC6 %s - Выберите месяц";
    public static final String CALENDAR_SELECT_DAY_TITLE = "\uD83D\uDCC6 %s %s - Выберите день";
    public static final String CALENDAR_SELECT_TIME_TITLE = "\uD83D\uDCC6 %s %02d %s %s - Выберите время";
    public static final String STAFF_SELECT_TITLE = "\uD83D\uDC64 %s %02d:%02d %02d.%02d.%04d - Выберите специалиста";
    public static final String CALENDAR_EMPTY_TIME_TITLE = "\uD83D\uDEAB Свободных мест нет. Выберите другой день";
    public static final String STAFF_EMPTY_TITLE = "\uD83D\uDEAB Свободных специалистов нет. Выберите другое время";
    public static final String RECORDS_EMPTY_TITLE = "\uD83D\uDEAB Записей нет";
    public static final String CONFIRM_RECORD_ERROR_TITLE = "\uD83D\uDEAB Не удалось выполнить запись. Попробуйте позже";
    public static final String SHOW_ERROR_TITLE = "\uD83D\uDEAB Не удалось выполнить запрос. Попробуйте позже";
    public static final String SUCCESS_CANCEL_RECORD = "✅ Ваша запись отменена";

    /**
     * Create main menu.
     * @return main menu
     */
    public static InlineKeyboardMarkup createMainMenuKeyboard(String typeBot) {
        if (Objects.equals(typeBot, BOT_RECORD.getType())) {
            InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
            keyboard.addRow(new InlineKeyboardButton(ACTIVE_TITLE)
                                    .callbackData(String.format("%s;%s", BOT_RECORDS.getType(), ACTIVE.getType())));
            keyboard.addRow(new InlineKeyboardButton(ITEMS_TITLE).callbackData(BOT_GROUP.getType()));
            keyboard.addRow(new InlineKeyboardButton(HISTORY_TITLE)
                                    .callbackData(String.format("%s;%s", BOT_RECORDS.getType(), NO_ACTIVE.getType())));
            keyboard.addRow(new InlineKeyboardButton(SETTINGS_TITLE).callbackData(BOT_SETTINGS.getType()));
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

}
