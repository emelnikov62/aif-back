package ru.aif.aifback.model.requests;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Item request.
 * @author emelnikov
 */
@Builder
@ToString
@Data
@AllArgsConstructor
public class UserItemRequest {

    private Long id;

    private String name;

    private Long hours;

    private Long mins;

    private BigDecimal amount;

}
