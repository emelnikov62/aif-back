package ru.aif.aifback.services.user;

import static java.lang.Boolean.TRUE;

import java.time.LocalDateTime;
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
     * Get user by source id and source or create new.
     * @param sourceId source id
     * @param source source
     * @return user data
     */
    public Optional<User> getUserBySourceOrCreate(String sourceId, String source) {
        Optional<User> user = userRepository.findBySource(sourceId, source);
        if (user.isPresent()) {
            return user;
        }

        return createUser(sourceId, source).flatMap(userRepository::findById);

    }

    /**
     * Create user.
     * @param sourceId source id
     * @param source source
     * @return user data
     */
    public Optional<Long> createUser(String sourceId, String source) {
        try {
            User user = new User(sourceId, TRUE, LocalDateTime.now(), source);
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
