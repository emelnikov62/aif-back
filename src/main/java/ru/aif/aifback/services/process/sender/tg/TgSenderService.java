package ru.aif.aifback.services.process.sender.tg;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.message.ChatMessage;
import ru.aif.aifback.services.process.sender.SenderService;

/**
 * Telegram sender service.
 * @author emelnikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TgSenderService implements SenderService {

    /**
     * Delete message.
     * @param chatMessage chat message
     */
    @Override
    public void deleteMessage(ChatMessage chatMessage) {
        try {
            chatMessage.getTelegramBot().execute(new DeleteMessage(chatMessage.getChatId(), Integer.parseInt(chatMessage.getMessageId())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Send message.
     * @param chatMessage chat message
     */
    @Override
    public void sendMessage(ChatMessage chatMessage) {
        if (Objects.nonNull(chatMessage.getFileData())) {
            sendMessageWithFile(chatMessage);
            return;
        }

        if (Objects.isNull(chatMessage.getButtons()) || chatMessage.getButtons().isEmpty()) {
            chatMessage.getTelegramBot().execute(new SendMessage(chatMessage.getChatId(), chatMessage.getText()).parseMode(ParseMode.HTML));
        } else {
            chatMessage.getTelegramBot().execute(new SendMessage(chatMessage.getChatId(), chatMessage.getText())
                                                         .parseMode(ParseMode.HTML)
                                                         .replyMarkup(fillKeyboard(chatMessage.getButtons())));
        }

        if (chatMessage.getUpdated()) {
            deleteMessage(chatMessage);
        }
    }

    /**
     * Fill keyboard.
     * @param buttons buttons
     * @return keyboard
     */
    private InlineKeyboardMarkup fillKeyboard(List<List<ChatMessage.Button>> buttons) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        buttons.forEach(
                row -> keyboardMarkup.addRow(
                        row.stream()
                           .map(r -> {
                               if (Objects.isNull(r.getUrl())) {
                                   return new InlineKeyboardButton(r.getTitle()).callbackData(r.getCallback());
                               } else {
                                   return new InlineKeyboardButton(r.getTitle()).webApp(new WebAppInfo(r.getUrl()));
                               }
                           })
                           .toList()
                           .toArray(new InlineKeyboardButton[0])));

        return keyboardMarkup;
    }

    /**
     * Send message with file.
     * @param chatMessage chat message
     */
    private void sendMessageWithFile(ChatMessage chatMessage) {
        chatMessage.getTelegramBot().execute(new SendPhoto(chatMessage.getChatId(), chatMessage.getFileData())
                                                     .parseMode(ParseMode.HTML)
                                                     .caption(chatMessage.getText())
                                                     .replyMarkup(fillKeyboard(chatMessage.getButtons())));
        if (chatMessage.getUpdated()) {
            deleteMessage(chatMessage);
        }
    }
}
