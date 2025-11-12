package ru.aif.aifback.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.User;

/**
 * User repository.
 * @author emelnikov
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Get user by tg id.
     * @param id tg id
     * @return user data
     */
    Optional<User> findByTgId(Long id);

}
