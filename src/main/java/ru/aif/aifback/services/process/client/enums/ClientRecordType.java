package ru.aif.aifback.services.process.client.enums;

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
public enum ClientRecordType {

    ACTIVE("active", "Активная", "\uD83D\uDD35", "Активных", "Активные"),
    CANCEL("cancel", "Отменена", "❌", "Отмененных", "Отмененные"),
    FINISHED("finished", "Завершена", "✅", "Завершенных", "Завершенные"),
    NO_ACTIVE("no_active", null, null, null, null);

    private final String type;
    private final String name;
    private final String icon;
    private final String nameStats;
    private final String names;

    public static ClientRecordType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(FINISHED);
    }

}
