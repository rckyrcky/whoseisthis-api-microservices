package com.whoseisthis.users.infrastructure.repository;

import com.whoseisthis.users.core.UserOutboxEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserOutboxRepository extends JpaRepository<UserOutboxEvents, Long> {
    @Query(value = """
            select *
            from user_outbox_events
            where status = 'PENDING' and retry_count < 3
            order by created_at asc, id asc
            limit 100
            for update skip locked
            """, nativeQuery = true)
    List<UserOutboxEvents> findEligibleEvent();
}
