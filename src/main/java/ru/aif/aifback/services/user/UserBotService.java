package ru.aif.aifback.services.user;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.dictionary.Bot;
import ru.aif.aifback.model.user.User;
import ru.aif.aifback.model.user.UserBot;
import ru.aif.aifback.repository.user.UserBotRepository;

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
        Optional<UserBot> userBot = userBotRepository.findById(id);
        if (userBot.isEmpty()) {
            return Optional.empty();
        }

        Optional<Bot> botType = botService.findById(userBot.get().getAifBotId());
        if (botType.isEmpty()) {
            return Optional.empty();
        }

        Optional<User> user = userService.findById(userBot.get().getAifUserId());
        if (user.isEmpty()) {
            return Optional.empty();
        }

        userBot.get().setUser(user.get());
        userBot.get().setBot(botType.get());
        return userBot;
    }

    /**
     * Get user bots by tg id.
     * @param tgId tg id
     * @return list user bots
     */
    public List<UserBot> getUserBotsByTgId(String tgId) {
        List<UserBot> userBots = new ArrayList<>();

        userService.getUserByTgIdOrCreate(tgId).ifPresent(user -> {
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
     * @return true/false
     */
    public Boolean deleteUserBot(Long id) {
        try {
            userBotRepository.deleteById(id);
            return TRUE;
        } catch (Exception e) {
            return FALSE;
        }
    }

    /**
     * Create user bot.
     * @param tgId tg id
     * @param botId bot id
     * @return answer
     */
    public Boolean createUserBot(String tgId, Long botId) {
        try {
            Optional<User> user = userService.getUserByTgIdOrCreate(tgId);
            if (user.isEmpty()) {
                return FALSE;
            }

            UserBot userBot = new UserBot(user.get().getId(), botId, FALSE, null, LocalDateTime.now());
            userBotRepository.save(userBot);

            return Objects.nonNull(userBot.getId()) ? TRUE : FALSE;
        } catch (Exception e) {
            return FALSE;
        }
    }

    /**
     * Link bot.
     * @param id id
     * @param token token
     * @return true/false
     */
    public boolean linkBot(Long id, String token) {
        try {
            userBotRepository.linkBot(id, token);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(String.format(
                                              "https://api.telegram.org/bot%s/setwebhook?url=https://n8n-agent-emelnikov62.amvera.io/webhook/aif/client/webhook?id=%s",
                                              token,
                                              id),
                                      String.class);
            return TRUE;
        } catch (Exception e) {
            return FALSE;
        }
    }
}
