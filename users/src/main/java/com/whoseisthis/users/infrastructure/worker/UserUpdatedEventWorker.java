package com.whoseisthis.users.infrastructure.worker;

import com.whoseisthis.users.core.OutboxStatus;
import com.whoseisthis.users.infrastructure.EventService;
import com.whoseisthis.users.infrastructure.repository.UserOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserUpdatedEventWorker {
    private final EventService eventService;
    private final UserOutboxRepository userOutboxRepository;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void process()
    {
        var events = userOutboxRepository.findEligibleEvent();
        for (var event : events) {
            try {
                eventService.publish(event.getEventType(), "user-" + event.getAggregateId(), event.getPayload());
                event.setStatus(OutboxStatus.SENT);
            } catch (Exception ex) {
                log.error("Failed publish outbox event | id={} | aggregateId={} | eventType={}",
                        event.getId(),
                        event.getAggregateId(),
                        event.getEventType(),
                        ex);
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 3) {
                    event.setStatus(OutboxStatus.FAILED);
                }
            }
        }
    }
}
