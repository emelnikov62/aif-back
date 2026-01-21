package ru.aif.aifback.repository.client;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.aif.aifback.model.client.ClientStar;

/**
 * Client star repository.
 * @author emelnikov
 */
@Repository
public interface ClientStarRepository extends CrudRepository<ClientStar, Long> {

    /**
     * Find client star by user item and staff.
     * @param clientId client id
     * @param userItemId user item id
     * @param staffId staff id
     * @return client star
     */
    @Query(value = "select s.*" +
                   "  from aif_client_stars s" +
                   " where s.aif_user_item_id = :user_item_id" +
                   "   and s.aif_staff_id = :staff_id" +
                   "   and s.aif_client_id = :client_id")
    Optional<ClientStar> findByClientAndUserItemAndStaff(@Param("client_id") Long clientId,
                                                         @Param("user_item_id") Long userItemId,
                                                         @Param("staff_id") Long staffId);

    /**
     * Stars by staff and user item.
     * @param userBotId user bot id
     * @param staffId staff id
     * @param userItemId user item id
     * @return stars
     */
    @Query(value = "select s.value" +
                   "  from aif_client_stars s" +
                   " where s.aif_user_bot_id = :user_bot_id" +
                   "   and s.aif_user_item_id = :user_item_id" +
                   "   and s.aif_staff_id = :staff_id")
    List<Integer> calcByStaffAndUserItem(@Param("user_bot_id") Long userBotId,
                                         @Param("staff_id") Long staffId,
                                         @Param("user_item_id") Long userItemId);

    /**
     * Stars by user item.
     * @param userBotId user bot id
     * @param userItemId user item id
     * @return stars
     */
    @Query(value = "select s.value" +
                   "  from aif_client_stars s" +
                   " where s.aif_user_bot_id = :user_bot_id" +
                   "   and s.aif_user_item_id = :user_item_id")
    List<Integer> calcByUserItem(@Param("user_bot_id") Long userBotId,
                                 @Param("user_item_id") Long userItemId);
}
