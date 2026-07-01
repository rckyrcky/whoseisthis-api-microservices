package com.whoseisthis.reports.infrastructure.repository;

import com.whoseisthis.reports.core.Reporter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReporterRepository extends JpaRepository<Reporter, Long> {
}
