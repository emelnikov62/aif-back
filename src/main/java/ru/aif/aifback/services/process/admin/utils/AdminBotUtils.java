package ru.aif.aifback.services.process.admin.utils;

import static ru.aif.aifback.enums.BotType.BOT_RECORD;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.BACK_BUTTON_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.CONNECT_BOT_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MENU_TITLE;
import static ru.aif.aifback.services.process.admin.constants.AdminBotButtons.MY_BOTS_TITLE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_BOTS;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_CREATE;
import static ru.aif.aifback.services.process.admin.enums.AdminBotOperationType.BOT_MAIN;
import static ru.aif.aifback.services.utils.CommonUtils.getDayOfWeek;
import static ru.aif.aifback.services.utils.CommonUtils.getMonthByNumber;

import java.util.List;
import java.util.Objects;

import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.services.process.client.bot.record.enums.ClientRecordType;

/**
 * Admin bot utils.
 * @author emelnikov
 */
public final class AdminBotUtils {

    /**
     * Create main menu.
     * @return main menu
     */
    public static List<ChatMessage.Button> createMainMenuKeyboard() {
        return List.of(ChatMessage.Button.builder().title(CONNECT_BOT_TITLE).callback(BOT_CREATE.getType()).build(),
                       ChatMessage.Button.builder().title(MY_BOTS_TITLE).callback(BOT_BOTS.getType()).build());
    }

    /**
     * Create back button.
     * @param backCallback back callback
     * @return back button
     */
    public static List<ChatMessage.Button> createBackButton(String backCallback) {
        return List.of(ChatMessage.Button.builder().title(BACK_BUTTON_TITLE).callback(backCallback).build(),
                       ChatMessage.Button.builder().title(MENU_TITLE).callback(BOT_MAIN.getType()).build());
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

    /**
     * Get client record info.
     * @param record client record
     * @param type client record type
     * @return client record info
     */
    public static String getClientRecordInfo(ClientRecord record, ClientRecordType type) {
        return String.format("%s <b>%s</b>\n\n", type.getIcon(), type.getName()) +
               String.format("\uD83D\uDCC5 <b>Дата:</b> %s %02d %s %s <b>%02d:%02d</b>",
                             getDayOfWeek(record.getUserCalendar().getDay(),
                                          record.getUserCalendar().getMonth(),
                                          record.getUserCalendar().getYear()),
                             record.getUserCalendar().getDay(),
                             getMonthByNumber(record.getUserCalendar().getMonth()),
                             record.getUserCalendar().getYear(),
                             record.getHours(),
                             record.getMins()) +
               String.format("\n\n\uD83D\uDCE6 <b>Услуга:</b> %s\n\n", record.getUserItem().getName()) +
               String.format("\uD83D\uDC64 <b>Специалист:</b> %s %s %s",
                             record.getUserStaff().getSurname(),
                             record.getUserStaff().getName(),
                             record.getUserStaff().getThird());
    }
}
