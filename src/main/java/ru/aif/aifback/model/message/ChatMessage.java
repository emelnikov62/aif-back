package ru.aif.aifback.model.message;

import java.util.List;

import com.pengrad.telegrambot.TelegramBot;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.enums.BotSource;

/**
 * Chat message model.
 * @author emelnikov
 */
@Slf4j
@Data
@ToString
@Builder
public class ChatMessage {

    @Data
    @Builder
    @ToString
    public static class Button {

        private String title;
        private String callback;
        private String url;

    }

    private String chatId;
    private String messageId;
    private String text;
    private BotSource source;
    private Boolean updated;
    private List<List<Button>> buttons;
    private TelegramBot telegramBot;
    private byte[] fileData;
}
