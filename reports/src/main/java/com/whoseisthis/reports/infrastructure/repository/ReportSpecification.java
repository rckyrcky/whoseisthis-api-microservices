package com.whoseisthis.reports.infrastructure.repository;

import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import com.whoseisthis.reports.core.Reporter;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class ReportSpecification {
    public static Specification<Report> titleContains(String title)
    {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase().trim() + "%");
        };
    }

    public static Specification<Report> locationContains(String location)
    {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase().trim() + "%");
        };
    }

    public static Specification<Report> reporter(String name)
    {
        return (root, query, cb) -> {
            if (name == null || name.isBlank()) {
                return cb.conjunction();
            }
            Join<Report, Reporter> reporter = root.join("reporter");
            return cb.like(cb.lower(reporter.get("name")), "%" + name
                    .toLowerCase().trim() + "%");
        };
    }

    public static Specification<Report> hasStatus(ReportStatus status)
    {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status.name());
        };
    }

    public static Specification<Report> hasType(ReportType type)
    {
        return (root, query, cb) -> {
            if (type == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("type"), type.name());
        };
    }

    public static Specification<Report> from(LocalDate from)
    {
        return (root, query, cb) -> {
            if (from == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("createdAt"), from.atStartOfDay().atOffset(ZoneOffset.UTC));
        };
    }

    public static Specification<Report> to(LocalDate to)
    {
        return (root, query, cb) -> {
            if (to == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("createdAt"),
                    to.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).minusNanos(1));
        };
    }
}
