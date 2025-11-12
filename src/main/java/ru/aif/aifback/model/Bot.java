package ru.aif.aifback.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Bot type model.
 * @author emelnikov
 */
@ToString
@Builder
@Data
public class Bot {

    private Long id;
    private String type;
    private String description;
    private boolean active;
    private LocalDateTime created;
}
