package ru.aif.aifback.model.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Ai record response.
 * @author emelnikov
 */
@Data
public class AiRecordResponse {

    private List<AiRecordStaffResponse> staffs;
    @JsonProperty("service_id")
    private String itemId;
    private String hours;
    private String mins;

}
