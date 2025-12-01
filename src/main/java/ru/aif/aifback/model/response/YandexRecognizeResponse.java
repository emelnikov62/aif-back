package ru.aif.aifback.model.response;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Yandex recognize response.
 * @author emelnikov
 */
@Slf4j
@Data
public class YandexRecognizeResponse {

    private String result;
}
