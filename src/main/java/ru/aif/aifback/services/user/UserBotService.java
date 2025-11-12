package ru.aif.aifback.services.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.UserBot;
import ru.aif.aifback.repository.UserBotRepository;
import ru.aif.aifback.repository.UserRepository;

/**
 * User bot API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserBotService {

    private final UserBotRepository userBotRepository;
    private final UserRepository userRepository;
    private final BotService botService;

    /**
     * Get user bot by id.
     * @param id id
     * @return user bot data
     */
    public Optional<UserBot> getUserBot(Long id) {
        return userBotRepository.findById(id);
    }

    /**
     * Get user bots by tg id.
     * @param id id
     * @return list user bots
     */
    public List<UserBot> getUserBotsByTgId(Long id) {
        List<UserBot> userBots = new ArrayList<>();

        userRepository.findByTgId(id).ifPresent(user -> {
            userBotRepository.findAllByAifUserId(user.getId()).forEachRemaining(userBots::add);

            if (!userBots.isEmpty()) {
                userBots.forEach(userBot -> {
                    botService.findById(userBot.getAifBotId()).ifPresent(userBot::setBot);
                });
            }
        });

        return userBots;
    }

}
