package ru.aif.aifback.services.ai.recognize;

import static ru.aif.aifback.constants.Constants.YANDEX_API_KEY;
import static ru.aif.aifback.constants.Constants.YANDEX_API_RECOGNIZE_URL;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.TgWebhookRequest;
import ru.aif.aifback.model.response.YandexRecognizeResponse;
import ru.aif.aifback.model.user.UserBot;

/**
 * Voice recognize service.
 * @author emelnikov
 */
@Slf4j
@AllArgsConstructor
@Service
public class VoiceRecognizeService {

    private final ObjectMapper mapper;

    /**
     * Recognize input voice.
     * @param webhookRequest request
     * @param userBot user bot
     * @return text
     */
    public String recognize(TgWebhookRequest webhookRequest, UserBot userBot) {
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
                throw new Exception(Strings.EMPTY);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int bytesRead;
            InputStream stream = new URL(fullPath).openStream();

            while ((bytesRead = stream.read(chunk)) > 0) {
                outputStream.write(chunk, 0, bytesRead);
            }

            if (outputStream.toByteArray().length == 0) {
                throw new Exception(Strings.EMPTY);
            }

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set("Authorization", YANDEX_API_KEY);

            HttpEntity<byte[]> entity = new HttpEntity<>(outputStream.toByteArray(), headers);
            ResponseEntity<String> response = restTemplate.exchange(YANDEX_API_RECOGNIZE_URL, HttpMethod.POST, entity, String.class);
            YandexRecognizeResponse result = mapper.readValue(response.getBody(), YandexRecognizeResponse.class);
            if (Objects.isNull(result)) {
                throw new Exception(Strings.EMPTY);
            }

            return result.getResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
