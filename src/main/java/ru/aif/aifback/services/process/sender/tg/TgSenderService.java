package ru.aif.aifback.services.process.sender.tg;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
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
        chatMessage.getTelegramBot().execute(new SendMessage(chatMessage.getChatId(),
                                                             chatMessage.getText())
                                                     .parseMode(ParseMode.HTML)
                                                     .replyMarkup(fillKeyboard(chatMessage.getButtons(), chatMessage.getColumns())));

        if (chatMessage.getUpdated()) {
            deleteMessage(chatMessage);
        }
    }

    /**
     * Fill keyboard.
     * @param buttons buttons
     * @param columns columns
     * @return keyboard
     */
    private InlineKeyboardMarkup fillKeyboard(List<ChatMessage.Button> buttons, Integer columns) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<ChatMessage.Button> backButtons = buttons.stream().filter(ChatMessage.Button::isBack).toList();
        List<ChatMessage.Button> workButtons = buttons.stream().filter(f -> !f.isBack()).toList();

        if (Objects.isNull(columns)) {
            workButtons.forEach(button -> {
                if (Objects.isNull(button.getUrl())) {
                    keyboardMarkup.addRow(new InlineKeyboardButton(button.getTitle()).callbackData(button.getCallback()));
                } else {
                    keyboardMarkup.addRow(new InlineKeyboardButton(button.getTitle()).webApp(new WebAppInfo(button.getUrl())));
                }
            });
        } else {
            List<InlineKeyboardButton> btns = new ArrayList<>();
            int num = 0;
            for (ChatMessage.Button button : workButtons) {
                if (Objects.isNull(button.getUrl())) {
                    btns.add(new InlineKeyboardButton(button.getTitle()).callbackData(button.getCallback()));
                } else {
                    btns.add(new InlineKeyboardButton(button.getTitle()).webApp(new WebAppInfo(button.getUrl())));
                }

                num++;

                if (num % columns == 0) {
                    keyboardMarkup.addRow(btns.toArray(new InlineKeyboardButton[0]));
                    btns.clear();
                }
            }

            if (!btns.isEmpty()) {
                keyboardMarkup.addRow(btns.toArray(new InlineKeyboardButton[0]));
            }
        }

        if (!backButtons.isEmpty()) {
            keyboardMarkup.addRow(backButtons.stream()
                                             .map(m -> new InlineKeyboardButton(m.getTitle()).callbackData(m.getCallback()))
                                             .toList()
                                             .toArray(new InlineKeyboardButton[0]));
        }

        return keyboardMarkup;
    }
}
