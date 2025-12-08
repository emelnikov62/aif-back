package ru.aif.aifback.services.process.admin.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin bot operation type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum AdminBotOperationType {

    BOT_MAIN("bot_main"),
    BOT_SELECT("bot_select"),
    BOT_BOTS("bot_bots"),
    BOT_CREATE("bot_create"),
    BOT_DELETE("bot_delete"),
    BOT_STATS("bot_stats"),
    BOT_CONFIRM_CREATE("bot_confirm_create"),
    BOT_STATS_SELECT("bot_show_stats"),
    BOT_RECORD_YEAR("bot_record_year"),
    BOT_RECORD_MONTH("bot_record_month"),
    BOT_RECORD_DAY("bot_record_day"),
    BOT_RECORDS("bot_records"),
    BOT_RECORD_SHOW_BY_DAY("bot_record_show_day"),
    BOT_RECORD_SHOW_ADDITIONAL("bot_record_show_additional"),
    BOT_RECORD_CANCEL("bot_record_cancel");

    private final String type;

}
