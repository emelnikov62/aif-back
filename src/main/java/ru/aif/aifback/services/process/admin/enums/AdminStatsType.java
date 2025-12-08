package ru.aif.aifback.services.process.admin.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin stats type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum AdminStatsType {

    ALL("all", "Общая", "\uD83D\uDCC5"),
    MONTH("month", "Месяц", "📅"),
    YEAR("year", "Год", "📅");

    private final String type;
    private final String name;
    private final String icon;

    public static AdminStatsType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(ALL);
    }

}
