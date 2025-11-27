package ru.aif.aifback.services.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.user.User;
import ru.aif.aifback.repository.user.UserRepository;

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
     * Get user by tg id or create new.
     * @param tgId tg id
     * @return user data
     */
    public Optional<User> getUserByTgIdOrCreate(String tgId) {
        Optional<User> user = userRepository.findByTgId(tgId);
        if (user.isPresent()) {
            return user;
        }

        return createUser(tgId).flatMap(userRepository::findById);

    }

    /**
     * Create user.
     * @param tgId tg id
     * @return user data
     */
    public Optional<Long> createUser(String tgId) {
        try {
            User user = new User(tgId);
            userRepository.save(user);

            return Optional.of(user.getId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Find by id.
     * @param id id
     * @return user
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

}
