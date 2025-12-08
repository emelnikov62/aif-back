package ru.aif.aifback.services.process.sender;

import ru.aif.aifback.model.message.ChatMessage;

/**
 * Sender to messangers interface.
 * @author emelnikov
 */
public interface SenderService {

    /**
     * Send message.
     * @param message chat message
     */
    void sendMessage(ChatMessage message);

    /**
     * Delete message.
     * @param message chat message
     */
    void deleteMessage(ChatMessage message);
}
