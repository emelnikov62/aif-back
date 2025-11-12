package ru.aif.aifback.services.user;

import static ru.aif.aifback.services.tg.TgButtons.CREATE_BOT_ERROR_ANSWER;
import static ru.aif.aifback.services.tg.TgButtons.CREATE_BOT_SUCCESS_ANSWER;
import static ru.aif.aifback.services.tg.TgButtons.DELETE_BOT_ERROR_ANSWER;
import static ru.aif.aifback.services.tg.TgButtons.DELETE_BOT_SUCCESS_ANSWER;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.User;
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
    private final UserService userService;
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
     * @param tgId tg id
     * @return list user bots
     */
    public List<UserBot> getUserBotsByTgId(String tgId) {
        List<UserBot> userBots = new ArrayList<>();

        userService.getUserByTgId(tgId).ifPresent(user -> {
            userBots.addAll(userBotRepository.findAllByAifUserId(user.getId()));

            if (!userBots.isEmpty()) {
                userBots.forEach(userBot -> {
                    botService.findById(userBot.getAifBotId()).ifPresent(userBot::setBot);
                });
            }
        });

        return userBots;
    }

    /**
     * Delete user bot.
     * @param id id
     * @return answer
     */
    public String deleteUserBot(Long id) {
        try {
            userBotRepository.deleteById(id);
            return DELETE_BOT_SUCCESS_ANSWER;
        } catch (Exception e) {
            return DELETE_BOT_ERROR_ANSWER;
        }
    }

    /**
     * Create user bot.
     * @param tgId tg id
     * @param botId bot id
     * @return answer
     */
    public String createUserBot(String tgId, Long botId) {
        try {
            Optional<User> user = userService.getUserByTgId(tgId);
            Long userId = null;

            if (user.isEmpty()) {
                Optional<User> saved = userService.createUser(tgId);
                if (saved.isPresent()) {
                    userId = saved.get().getId();
                }
            } else {
                userId = user.get().getId();
            }

            if (Objects.isNull(userId)) {
                throw new Exception(CREATE_BOT_ERROR_ANSWER);
            }

            UserBot userBot = new UserBot(userId, botId);
            userBotRepository.save(userBot);

            return CREATE_BOT_SUCCESS_ANSWER;
        } catch (Exception e) {
            return CREATE_BOT_ERROR_ANSWER;
        }
    }

}
