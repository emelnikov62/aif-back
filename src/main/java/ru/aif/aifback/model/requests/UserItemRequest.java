package ru.aif.aifback.model.requests;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    private Boolean active;
    private MultipartFile file;
    private String surname;
    private String third;
    private List<Long> services;

}
