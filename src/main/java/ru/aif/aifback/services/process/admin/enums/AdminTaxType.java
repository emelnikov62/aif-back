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

    BASE("base", "Базовая", "\uD83D\uDD36", "1 месяц: 999\uD83D\uDCB2", "3 месяца:2 699\uD83D\uDCB2", "1 год: 9 999\uD83D\uDCB2"),
    ADDITIONAL("additional", "Расширенная", "\uD83D\uDD37", "1 месяц: 1 999\uD83D\uDCB2", "3 месяца:5 399\uD83D\uDCB2", "1 год: 19 999\uD83D\uDCB2"),
    PREMIUM("premium", "Премиум", "⭐", "1 месяц: 3 999\uD83D\uDCB2", "3 месяца: 10 999\uD83D\uDCB2", "1 год: 40 999\uD83D\uDCB2");

    private final String type;
    private final String name;
    private final String icon;
    private final String onePrice;
    private final String threePrice;
    private final String twelvePrice;

    public static AdminTaxType findByType(String type) {
        return Arrays.stream(values()).filter(v -> Objects.equals(type, v.getType())).findFirst().orElse(BASE);
    }

}
