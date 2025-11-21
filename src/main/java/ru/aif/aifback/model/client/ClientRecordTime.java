package ru.aif.aifback.model.client;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Client record time model.
 * @author emelnikov
 */
@ToString
@Data
@Builder
public class ClientRecordTime {

    private int hours;
    private int mins;
    private Long staffId;
    private Long itemId;
    private Long calendarId;
}
