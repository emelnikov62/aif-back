package ru.aif.aifback.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Ai record staff response..
 * @author emelnikov
 */
@Data
public class AiRecordStaffResponse {

    private String id;
    private String name;
    @JsonProperty("calendar_id")
    private String calendarId;

}
