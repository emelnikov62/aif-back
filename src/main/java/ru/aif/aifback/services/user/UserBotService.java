package ru.aif.aifback.services.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.repository.UserBotRepository;

/**
 * User bot API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserBotService {

    private final UserBotRepository userBotRepository;

    /**
     * Get user bot by id.
     * @param id id
     * @return user bot data
     */
    public Optional<UserBot> getUserBot(Long id) {
        return userBotRepository.findById(id);
    }

}
