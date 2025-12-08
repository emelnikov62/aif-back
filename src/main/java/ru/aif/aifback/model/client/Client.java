package ru.aif.aifback.model.client;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Client model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_clients")
@RequiredArgsConstructor
public class Client {

    @Id
    private Long id;
    private String sourceId;
    private boolean active;
    private LocalDateTime created;

    public Client(String sourceId) {
        this.sourceId = sourceId;
    }

    public Client(String sourceId, boolean active, LocalDateTime created) {
        this.sourceId = sourceId;
        this.active = active;
        this.created = created;
    }
}
