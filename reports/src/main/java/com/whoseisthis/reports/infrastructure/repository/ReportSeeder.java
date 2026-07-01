package com.whoseisthis.reports.infrastructure.repository;

import com.whoseisthis.reports.common.exception.NotFoundError;
import com.whoseisthis.reports.core.Report;
import com.whoseisthis.reports.core.ReportStatus;
import com.whoseisthis.reports.core.ReportType;
import com.whoseisthis.reports.core.Reporter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportSeeder {
    private final ReportRepository reportRepository;
    private final ReporterRepository reporterRepository;

    public void seed()
    {
        if (reportRepository.count() > 0) {
            System.out.println("Reports is already seeded!");
            return;
        }
        createReporter(1L,"admin@test.com", "admin");
        createReporter(2L,"john@test.com", "john");
        createReporter(3L,"alice@test.com", "alice");
        for (int i = 4; i < 204; i++) {
            var _i = i - 3;
            createReporter((long) i, "user-" + _i + "@test.com", "user " + _i);
        }

        var report = getDummyReport();

        for (int i = 0; i <= 200; i++) {
            var data = report.get(i % report.size());
            createReport((Long) data.get("user"),
                    (String) data.get("title"),
                    (String) data.get("location"),
                    (String) data.get("description"),
                    (ReportStatus) data.get("status"),
                    (ReportType) data.get("type"));
        }
        System.out.println("Report is seeded!");
    }

    private void createReport(
            Long reporterId, String title, String location, String description, ReportStatus status,
            ReportType type)
    {
        Reporter reporter = reporterRepository.findById(reporterId).orElseThrow(NotFoundError::new);
        Report report = new Report();
        report.setReporter(reporter);
        report.setTitle(title);
        report.setLocation(location);
        report.setDescription(description);
        report.setStatus(status);
        report.setType(type);
        reportRepository.save(report);
    }

    private void createReporter(Long userId, String email, String name)
    {
        Reporter reporter = new Reporter();
        reporter.setUserId(userId);
        reporter.setEmail(email);
        reporter.setName(name);
        reporterRepository.save(reporter);
    }

    private List<Map<String, Object>> getDummyReport()
    {
        return List.of(Map.of("title",
                        "Found a Book",
                        "type",
                        ReportType.FOUND,
                        "status",
                        ReportStatus.OPEN,
                        "description",
                        "Found a programming book near the study area.",
                        "location",
                        "Library",
                        "user",
                        2L),
                Map.of("title",
                        "Lost a Laptop",
                        "type",
                        ReportType.LOST,
                        "status",
                        ReportStatus.OPEN,
                        "description",
                        "Lost a silver laptop after class ended.",
                        "location",
                        "Class 303",
                        "user",
                        2L),
                Map.of("title",
                        "Found a Tumblr",
                        "type",
                        ReportType.FOUND,
                        "status",
                        ReportStatus.OPEN,
                        "description",
                        "Found a black tumblr bottle on a bench.",
                        "location",
                        "Campus Park",
                        "user",
                        3L),
                Map.of("title",
                        "Lost a Keychain",
                        "type",
                        ReportType.LOST,
                        "status",
                        ReportStatus.OPEN,
                        "description",
                        "Lost a keychain with a small anime figure.",
                        "location",
                        "Parking Area",
                        "user",
                        3L),
                Map.of("title",
                        "Found a Jacket",
                        "type",
                        ReportType.FOUND,
                        "status",
                        ReportStatus.OPEN,
                        "description",
                        "Found a blue hoodie left in the classroom.",
                        "location",
                        "Class 101",
                        "user",
                        2L),
                Map.of("title",
                        "Lost a Charger",
                        "type",
                        ReportType.LOST,
                        "status",
                        ReportStatus.RESOLVED,
                        "description",
                        "Lost a white laptop charger during group study.",
                        "location",
                        "Computer Lab",
                        "user",
                        3L),
                Map.of("title",
                        "Found a Hat",
                        "type",
                        ReportType.FOUND,
                        "status",
                        ReportStatus.RESOLVED,
                        "description",
                        "Found a black cap near the cafeteria.",
                        "location",
                        "Cafeteria",
                        "user",
                        2L));
    }
}

