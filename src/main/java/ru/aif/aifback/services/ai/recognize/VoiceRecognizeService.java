package ru.aif.aifback.services.ai.recognize;

import static ru.aif.aifback.constants.Constants.YANDEX_API_KEY;
import static ru.aif.aifback.constants.Constants.YANDEX_API_RECOGNIZE_URL;
import static ru.aif.aifback.constants.Constants.YANDEX_RESULT_KEY;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.user.UserBot;

/**
 * Voice recognize service.
 * @author emelnikov
 */
@Slf4j
@AllArgsConstructor
@Service
public class VoiceRecognizeService {

    /**
     * Recognize input voice.
     * @param webhookRequest request
     * @param successMessage success message
     * @param emptyMessage empty message
     * @param userBot user bot
     * @return text
     */
    public String recognize(TgWebhookRequest webhookRequest, String successMessage, String emptyMessage, UserBot userBot) {
        String answer;

        try {
            TelegramBot bot = new TelegramBot(userBot.getToken());
            GetFile request = new GetFile(webhookRequest.getFileId());
            GetFileResponse getFileResponse = bot.execute(request);

            File file = getFileResponse.file();
            file.fileId();
            file.filePath();
            file.fileSize();

            String fullPath = bot.getFullFilePath(file);
            if (Objects.isNull(fullPath)) {
                throw new Exception(emptyMessage);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", YANDEX_API_KEY);

            HttpEntity<byte[]> entity = new HttpEntity<>(outputStream.toByteArray(), headers);
            ResponseEntity<String> response = restTemplate.exchange(YANDEX_API_RECOGNIZE_URL, HttpMethod.POST, entity, String.class);
            String result = (new JSONObject(response.getBody())).getString(YANDEX_RESULT_KEY);
            answer = String.format(successMessage, result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            answer = emptyMessage;
        }

        return answer;
    }
}
