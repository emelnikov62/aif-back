package ru.aif.aifback.constants;

import java.util.Map;

/**
 * Constants.
 * @author emelnikov
 */
public final class Constants {

    public final static String DELIMITER = ";";
    public final static String DELIMITER_CHAR = ",";

    public final static String MAIN_URL = "/aif";
    public final static String ADMIN_URL = "/admin";
    public final static String CLIENT_URL = "/client";

    public final static String TG_TOKEN_ADMIN = "7277396052:AAEIEaz200U8MXlRCy60aOsEkoFKC9Q2eds";
    public final static String YANDEX_API_KEY = "Api-Key AQVNxPqsNFPShdMRlrYdwJUtKTufys1WCVxeI99W";
    public final static String YANDEX_API_RECOGNIZE_URL = "https://stt.api.cloud.yandex.net/speech/v1/stt:recognize";
    public final static String AI_SEARCH_URL = "https://n8n-agent-emelnikov62.amvera.io/webhook/aif/ai/webhook";
    public final static String TG_LOG_ID = "-1002391679452L";
    public final static String EMPTY_PARAM = "empty";
    public final static String NULL_PARAM = "null";
    public final static int MESSAGE_ID_EMPTY = 0;
    public final static Integer COLUMNS_DAYS = 3;
    public final static Integer COLUMNS_STATS = 3;

    public static final Map<Long, String> MONTHS = Map.ofEntries(
            Map.entry(1L, "Январь"),
            Map.entry(2L, "Февраль"),
            Map.entry(3L, "Март"),
            Map.entry(4L, "Апрель"),
            Map.entry(5L, "Май"),
            Map.entry(6L, "Июнь"),
            Map.entry(7L, "Июль"),
            Map.entry(8L, "Август"),
            Map.entry(9L, "Сентябрь"),
            Map.entry(10L, "Октябрь"),
            Map.entry(11L, "Ноябрь"),
            Map.entry(12L, "Декабрь")
    );

    public static final Map<Integer, String> DAY_OF_WEEK = Map.ofEntries(
            Map.entry(1, "Пн"),
            Map.entry(2, "Вт"),
            Map.entry(3, "Ср"),
            Map.entry(4, "Чт"),
            Map.entry(5, "Пт"),
            Map.entry(6, "Сб"),
            Map.entry(7, "Вс")
    );

    private Constants() {
        throw new AssertionError("Utility class cannot be created");
    }

}
