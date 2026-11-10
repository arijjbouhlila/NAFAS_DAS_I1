package tn.nafas.reporting.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import tn.nafas.reporting.model.*;
import tn.nafas.reporting.repository.ReportRepository;

import java.time.Instant;

/** Inserts a few sample reports so the demo is not empty. Disable with nafas.seed-demo-data=false. */
@Component
@ConditionalOnProperty(name = "nafas.seed-demo-data", havingValue = "true")
public class DemoDataLoader implements CommandLineRunner {
    private final ReportRepository repo;

    public DemoDataLoader(ReportRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        add("Black smoke near the port", "Thick smoke visible since this morning.", Category.AIR,
                33.8815, 10.0982, "Ali", ReportStatus.SUBMITTED, null);
        add("Cloudy water at the beach", "The water is brownish and smells bad.", Category.WATER,
                33.8930, 10.1170, "Sara", ReportStatus.VALIDATED, "Confirmed by the field team");
        add("Illegal dumping", "Waste piled up along the road.", Category.WASTE,
                33.8700, 10.0850, "Ali", ReportStatus.VALIDATED, "Clear photo, location confirmed");
        add("Night-time noise", "Machines running all night.", Category.NOISE,
                33.8800, 10.1000, "Mohamed", ReportStatus.REJECTED, "Outside the monitored area");
    }

    private void add(String title, String desc, Category cat, double lat, double lon,
                     String by, ReportStatus status, String comment) {
        Report r = new Report();
        r.setTitle(title); r.setDescription(desc); r.setCategory(cat);
        r.setLatitude(lat); r.setLongitude(lon); r.setReporterName(by);
        r.setStatus(status); r.setAdminComment(comment);
        if (status != ReportStatus.SUBMITTED) r.setReviewedAt(Instant.now());
        repo.save(r);
    }
}
