package ru.aif.aifback.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.User;

/**
 * User repository.
 * @author emelnikov
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
