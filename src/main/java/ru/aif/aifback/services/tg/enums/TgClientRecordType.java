package ru.aif.aifback.services.tg.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Client record type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum TgClientRecordType {

    ACTIVE("active", "активная", "\uD83D\uDD35"),
    CANCEL("cancel", "отменена", "❌"),
    FINISHED("finished", "завершена", "✅"),
    NO_ACTIVE("no_active", null, null);

    private final String type;
    private final String name;
    private final String icon;

    public static TgClientRecordType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(FINISHED);
    }

}
