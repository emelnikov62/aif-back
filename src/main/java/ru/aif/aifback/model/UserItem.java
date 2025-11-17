package ru.aif.aifback.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * User item model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_user_items")
@RequiredArgsConstructor
public class UserItem {

    @Id
    private Long id;
    private Long aifUserItemGroupId;
    private String name;
    private Long hours;
    private Long mins;
    private BigDecimal amount;
    private String fileData;
    private boolean active;
    private LocalDateTime created;

}
