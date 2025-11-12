package ru.aif.aifback.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.ToString;

/**
 * Bot type model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_bots")
public class Bot {

    @Id
    private Long id;
    private String type;
    private String description;
    private boolean active;
    private LocalDateTime created;
}
