package tn.nafas.reporting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.nafas.reporting.model.*;
import tn.nafas.reporting.repository.ReportRepository;
import tn.nafas.reporting.web.ReportRequest;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ReportService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png");

    private final ReportRepository repo;
    private final Path uploadDir;

    public ReportService(ReportRepository repo, @Value("${nafas.upload-dir}") String dir) throws IOException {
        this.repo = repo;
        this.uploadDir = Files.createDirectories(Paths.get(dir).toAbsolutePath().normalize());
    }

    public Report create(ReportRequest r) {
        if (Math.abs(r.latitude()) > 90 || Math.abs(r.longitude()) > 180)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid GPS coordinates");
        Report rep = new Report();
        rep.setTitle(r.title().trim());
        rep.setDescription(r.description().trim());
        rep.setCategory(r.category());
        rep.setLatitude(r.latitude());
        rep.setLongitude(r.longitude());
        rep.setLocationId(r.locationId());
        rep.setReporterName(r.reporterName());
        return repo.save(rep);   // initial status = SUBMITTED
    }

    public List<Report> list(ReportStatus status, Category category) {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .filter(r -> status == null || r.getStatus() == status)
                .filter(r -> category == null || r.getCategory() == category)
                .toList();
    }

    public Report get(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Report " + id + " not found"));
    }

    public Report review(Long id, ReportStatus target, String comment) {
        Report rep = get(id);
        if (rep.getStatus() != ReportStatus.SUBMITTED)
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Report already " + rep.getStatus() + ", it cannot be reviewed again");
        rep.setStatus(target);
        rep.setAdminComment(comment);
        rep.setReviewedAt(Instant.now());
        return repo.save(rep);
    }

    public void delete(Long id) {
        Report rep = get(id);
        if (rep.getPhotoPath() != null) {
            try { Files.deleteIfExists(uploadDir.resolve(rep.getPhotoPath())); } catch (IOException ignored) { }
        }
        repo.delete(rep);
    }

    public Map<String, Long> stats() {
        Map<String, Long> m = new LinkedHashMap<>();
        m.put("TOTAL", repo.count());
        for (ReportStatus s : ReportStatus.values()) m.put(s.name(), repo.countByStatus(s));
        return m;
    }

    public Report attachPhoto(Long id, MultipartFile file) throws IOException {
        Report rep = get(id);
        if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Photo must be a JPEG or PNG image");
        String ext = "image/png".equals(file.getContentType()) ? ".png" : ".jpg";
        Path target = uploadDir.resolve("report-" + id + ext);   // generated name: no path traversal
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        rep.setPhotoPath(target.getFileName().toString());
        return repo.save(rep);
    }

    public Path photoPath(Long id) {
        Report rep = get(id);
        if (rep.getPhotoPath() == null || !Files.exists(uploadDir.resolve(rep.getPhotoPath())))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No photo for report " + id);
        return uploadDir.resolve(rep.getPhotoPath());
    }
}
