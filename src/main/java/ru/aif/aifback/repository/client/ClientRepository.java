package ru.aif.aifback.repository.client;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    /**
     * Add client.
     * @param tgId tg id
     * @return id
     */
    @Query(value = "insert into aif_clients(tg_id) values(:tg_id)")
    @Modifying
    Long addClient(@Param("tg_id") String tgId);

}
