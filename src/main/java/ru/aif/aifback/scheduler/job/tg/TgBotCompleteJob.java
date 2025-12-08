package ru.aif.aifback.scheduler.job.tg;

import static ru.aif.aifback.services.process.client.enums.ClientRecordType.ACTIVE;
import static ru.aif.aifback.services.process.client.enums.ClientRecordType.FINISHED;

import java.util.List;
import java.util.Objects;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.aif.aifback.model.client.ClientRecord;
import ru.aif.aifback.scheduler.BotJob;
import ru.aif.aifback.services.client.ClientRecordService;
import ru.aif.aifback.services.process.admin.notification.TgAdminNotificationService;
import ru.aif.aifback.services.process.tg.client.bot.record.TgClientBotRecordNotificationService;

/**
 * TG job service for complete services by date.
 * @author emelnikov
 */
@Slf4j
@RequiredArgsConstructor
@Service
@ConditionalOnProperty(prefix = "scheduler.telegram", name = "enabled", havingValue = "true")
public class TgBotCompleteJob implements BotJob {

    private final ClientRecordService clientRecordService;
    private final TgAdminNotificationService tgAdminNotificationService;
    private final TgClientBotRecordNotificationService tgClientBotRecordNotificationService;

    /**
     * Completed services by date.
     */
    @Override
    @Scheduled(cron = "${scheduler.telegram.completed-services}")
    public void process() {
        List<ClientRecord> records = clientRecordService.findAllForCompleted(ACTIVE.getType());
        if (records.isEmpty()) {
            return;
        }

        records.forEach(record -> {
            if (clientRecordService.completeService(record.getId())) {
                log.info("Complete service: {}", record.getId());

                if (Objects.nonNull(record.getUserBot()) && Objects.nonNull(record.getUserBot().getUser()) && Objects.nonNull(record.getClient())) {
                    tgAdminNotificationService.recordCompleteNotification(record);
                    tgClientBotRecordNotificationService.recordNotification(record, FINISHED);
                }
            } else {
                log.info("Error complete service: {}", record.getId());
            }
        });
    }
}
