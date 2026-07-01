package com.whoseisthis.users.interfaces.utils;

import com.whoseisthis.users.core.UserOutboxEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class OutboxEventsFactory {
    private final ObjectMapper objectMapper;

    public UserOutboxEvents create(Long userId, String eventType, Object event)
    {
        UserOutboxEvents e = new UserOutboxEvents();
        e.setAggregateId(userId);
        e.setEventType(eventType);
        e.setPayload(objectMapper.writeValueAsString(event));
        return e;
    }
}
