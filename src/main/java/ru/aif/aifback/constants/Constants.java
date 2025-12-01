package ru.aif.aifback.constants;

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
    public final static String TG_LOG_ID = "-1002391679452L";
    public final static String EMPTY_PARAM = "empty";
    public final static int MESSAGE_ID_EMPTY = 0;

    private Constants() {
        throw new AssertionError("Utility class cannot be created");
    }

}
