package ru.aif.aifback.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
     * @param tgId tg id
     * @return user data
     */
    Optional<User> findByTgId(String tgId);

    @Query(value = "insert into aif_users(tg_id) values(:tg_id) returning id")
    @Modifying
    Long addUser(@Param("tg_id") String tgId);

}
