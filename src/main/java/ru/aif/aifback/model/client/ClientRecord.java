package ru.aif.aifback.model.client;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Client record model.
 * @author emelnikov
 */
@ToString
@Data
@Table("aif_client_records")
@RequiredArgsConstructor
public class ClientRecord {

    @Id
    private Long id;
    private Long aifClientId;
    private Long aifUserBotId;
    private Long aifUserItemId;
    private Long aifUserCalendarId;
    private Long hours;
    private Long mins;
    private String status;
    private LocalDateTime created;

}
