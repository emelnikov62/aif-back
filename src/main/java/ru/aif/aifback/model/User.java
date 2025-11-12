package ru.aif.aifback.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * User model.
 * @author emelnikov
 */
@ToString
@Builder
@Data
public class User {

    private Long id;
    private String tgId;
    private boolean active;
    private LocalDateTime created;
}
