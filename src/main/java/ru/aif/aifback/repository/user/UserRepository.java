package ru.aif.aifback.repository.user;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.user.User;

/**
 * User repository.
 * @author emelnikov
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Get user by source id.
     * @param sourceId source id
     * @return user data
     */
    @Query(value = "select u.* from aif_users u where u.source_id = :source_id and u.source = :source")
    Optional<User> findBySource(@Param("source_id") String sourceId, @Param("source") String source);

}
