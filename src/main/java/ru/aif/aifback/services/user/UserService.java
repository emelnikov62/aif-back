package ru.aif.aifback.services.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.User;
import ru.aif.aifback.repository.UserRepository;

/**
 * User API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user by tg id.
     * @param tgId tg id
     * @return user data
     */
    public Optional<User> getUserByTgId(String tgId) {
        return userRepository.findByTgId(tgId);
    }

    /**
     * Create user.
     * @param tgId tg id
     * @return user data
     */
    public Optional<Long> createUser(String tgId) {
        try {
            return Optional.of(userRepository.addUser(tgId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
