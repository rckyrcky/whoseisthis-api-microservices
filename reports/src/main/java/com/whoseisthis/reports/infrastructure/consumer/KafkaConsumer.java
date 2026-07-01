package com.whoseisthis.reports.infrastructure.consumer;

import com.whoseisthis.reports.application.UserUpdatedEvent;
import com.whoseisthis.reports.common.exception.NotFoundError;
import com.whoseisthis.reports.core.Reporter;
import com.whoseisthis.reports.infrastructure.repository.ReporterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final ReporterRepository reporterRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user-updated")
    @Transactional
    public void consume(String payload)
    {
        UserUpdatedEvent event = objectMapper.readValue(payload, UserUpdatedEvent.class);
        Reporter reporter = reporterRepository.findById(event.id()).orElseThrow(NotFoundError::new);
        if (reporter.getUpdatedAt() == null || event.updatedAt().isAfter(reporter.getUpdatedAt())) {
            reporter.setName(event.name());
            reporter.setEmail(event.email());
            reporter.setUpdatedAt(event.updatedAt());

            log.info("Reporter updated via event user-updated | id={} | updatedAt={}",
                    event.id(),
                    event.updatedAt()
                    );
        }
    }

    @KafkaListener(topics = "user-updated-dlt")
    public void consumeDlt(ConsumerRecord<String, UserUpdatedEvent> record)
    {
        log.error("Dead event | key={} | topic={} | partition={} | offset={} | event={}",
                record.key(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.value());
    }
}
