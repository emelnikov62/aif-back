package ru.aif.aifback.repository.client;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.client.Client;

/**
 * Client repository.
 * @author emelnikov
 */
@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {

    /**
     * Get client by tg id.
     * @param tgId tg id
     * @return client data
     */
    Optional<Client> findByTgId(String tgId);

}
