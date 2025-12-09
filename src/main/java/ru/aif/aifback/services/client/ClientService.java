package ru.aif.aifback.services.client;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.Client;
import ru.aif.aifback.repository.client.ClientRepository;

/**
 * Client API service.
 * @author emelnikov
 */
@Slf4j
@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    /**
     * Get client by source id.
     * @param sourceId source id
     * @return client data
     */
    public Optional<Client> getClientBySourceId(String sourceId) {
        return clientRepository.findBySourceId(sourceId);
    }

    /**
     * Get client id or create new.
     * @param sourceId source id
     * @return id
     */
    public Long getClientIdOrCreate(String sourceId) {
        Long id;

        Optional<Client> client = getClientBySourceId(sourceId);
        if (client.isEmpty()) {
            id = createClient(sourceId);
        } else {
            id = client.get().getId();
        }

        return id;
    }

    /**
     * Create client.
     * @param sourceId source id
     * @return client data
     */
    public Long createClient(String sourceId) {
        try {
            Client client = new Client(sourceId, Boolean.TRUE, LocalDateTime.now());
            clientRepository.save(client);

            return client.getId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Find by id.
     * @param id id
     * @return client
     */
    public Client findById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

}
