package ru.aif.aifback.services.tg.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Client record type.
 * @author emelnikov
 */
@Getter
@RequiredArgsConstructor
public enum TgClientRecordType {

    ACTIVE("active"),
    CANCEL("cancel"),
    FINISHED("finished");

    private final String type;

}
