package ru.aif.aifback.services.user;

import java.util.List;

import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.Bot;
import ru.aif.aifback.repository.BotRepository;

/**
 * Bot API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class BotService {

    private final BotRepository botRepository;

    /**
     * Get bots.
     * @return bots data
     */
    public List<Bot> getBots() {
        return Lists.newArrayList(botRepository.findAll());
    }

}
