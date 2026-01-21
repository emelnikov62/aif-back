package ru.aif.aifback.services.process.admin.enums;

import java.util.Arrays;
import java.util.Objects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Admin tax type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum AdminTaxType {

    BASE("base", "Базовая", "\uD83D\uDD36"),
    ADDITIONAL("additional", "Расширенная", "\uD83D\uDD37"),
    PREMIUM("premium", "Премиум", "⭐");

    private final String type;
    private final String name;
    private final String icon;

    public static AdminTaxType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(BASE);
    }

}
