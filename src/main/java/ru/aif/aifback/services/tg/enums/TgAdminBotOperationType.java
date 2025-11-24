package ru.aif.aifback.services.tg.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin operation type bot.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum TgAdminBotOperationType {

    BOT_MAIN("bot_main"),
    BOT_SELECT("bot_select"),
    BOTS_BOTS("bot_bots"),
    BOT_CREATE("bot_create"),
    BOT_DELETE("bot_delete"),
    BOT_STATS("bot_stats"),
    BOT_CONFIRM_CREATE("bot_confirm_create");

    private final String type;

}
