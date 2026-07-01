package com.whoseisthis.reports.application;

import java.time.OffsetDateTime;

public record UserUpdatedEvent(Long id, String name, String email, OffsetDateTime updatedAt) {
}
