package ru.aif.aifback.services.ai.record;

import static ru.aif.aifback.constants.Constants.AI_SEARCH_URL;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.requests.AiRecordRequest;
import ru.aif.aifback.model.response.AiRecordResponse;
import ru.aif.aifback.model.user.UserBot;

/**
 * Ai record search service.
 * @author emelnikov
 */
@Slf4j
@AllArgsConstructor
@Service
public class RecordSearchService {

    private final ObjectMapper mapper;

    /**
     * Ai record search voice.
     * @param prompt prompt
     * @param userBot user bot
     * @param tgId tg id
     * @return text
     */
    public AiRecordResponse search(String prompt, UserBot userBot, String tgId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String request = new ObjectMapper().writeValueAsString(AiRecordRequest.builder().tgId(tgId).prompt(prompt).id(userBot.getId()).build());
            HttpEntity<String> entity = new HttpEntity<>(request, headers);
            ResponseEntity<AiRecordResponse> response = restTemplate.exchange(AI_SEARCH_URL, HttpMethod.POST, entity, AiRecordResponse.class);

            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
