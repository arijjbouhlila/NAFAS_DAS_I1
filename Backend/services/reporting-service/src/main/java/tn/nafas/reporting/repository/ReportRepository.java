package tn.nafas.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.nafas.reporting.model.Report;
import tn.nafas.reporting.model.ReportStatus;

public interface ReportRepository extends JpaRepository<Report, Long> {
    long countByStatus(ReportStatus status);
}
