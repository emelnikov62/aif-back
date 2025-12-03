package ru.aif.aifback.model.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Ai record request.
 * @author emelnikov
 */
@Builder
@ToString
@Data
@AllArgsConstructor
public class AiRecordRequest {

    private Long id;
    private String prompt;
    private String tgId;

}
