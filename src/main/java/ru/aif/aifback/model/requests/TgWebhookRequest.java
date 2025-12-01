package ru.aif.aifback.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Webhook request.
 * @author emelnikov
 */
@Builder
@ToString
@Data
@AllArgsConstructor
public class TgWebhookRequest {

    @JsonProperty("chat_id")
    private String chatId;

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("file_id")
    private String fileId;

    private String text;

    private boolean callback;

    private String id;

}
