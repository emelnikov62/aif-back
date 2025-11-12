package ru.aif.aifback.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Webhook admin request.
 * @author emelnikov
 */
@Builder
@ToString
@Data
@AllArgsConstructor
public class WebhookAdminRequest {

    @JsonProperty("chat_id")
    private String chatId;

    private String text;

    private boolean callback;

}
