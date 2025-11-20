package ru.aif.aifback.services.client;

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
     * Get client by tg id.
     * @param tgId tg id
     * @return client data
     */
    public Optional<Client> getClientByTgId(String tgId) {
        return clientRepository.findByTgId(tgId);
    }

    /**
     * Get client id or create new.
     * @param tgId tg id
     * @return id
     */
    public Long getClientIdOrCreate(String tgId) {
        Long id;

        Optional<Client> client = getClientByTgId(tgId);
        if (client.isEmpty()) {
            id = createClient(tgId);
        } else {
            id = client.get().getId();
        }

        return id;
    }

    /**
     * Create client.
     * @param tgId tg id
     * @return client data
     */
    public Long createClient(String tgId) {
        try {
            return clientRepository.addClient(tgId);
        } catch (Exception e) {
            return null;
        }
    }

}
