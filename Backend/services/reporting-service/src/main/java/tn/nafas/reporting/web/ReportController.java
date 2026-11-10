package tn.nafas.reporting.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.nafas.reporting.model.*;
import tn.nafas.reporting.service.ReportService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Submit environmental observations and moderate them")
public class ReportController {
    private static final String ROLE_DESC = "Simulated role: CITIZEN or ADMIN (real auth comes from User Management + Gateway)";

    private final ReportService service;

    public ReportController(ReportService service) { this.service = service; }

    // ---------- Citizen + admin ----------

    @Operation(summary = "Submit a report", description = "Starts in status SUBMITTED.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Report create(@Valid @RequestBody ReportRequest req) { return service.create(req); }

    @Operation(summary = "Attach a photo (JPEG/PNG, max 5 MB)")
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Report upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return service.attachPhoto(id, file);
    }

    @Operation(summary = "List reports", description = "Newest first. Optional filters: status, category.")
    @GetMapping
    public List<Report> list(@RequestParam(required = false) ReportStatus status,
                             @RequestParam(required = false) Category category) {
        return service.list(status, category);
    }

    @Operation(summary = "Counts per status (for dashboards / the Analytics Service)")
    @GetMapping("/stats")
    public Map<String, Long> stats() { return service.stats(); }

    @Operation(summary = "Get one report")
    @GetMapping("/{id}")
    public Report get(@PathVariable Long id) { return service.get(id); }

    @Operation(summary = "Download the report photo")
    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> photo(@PathVariable Long id) {
        var path = service.photoPath(id);
        MediaType type = path.toString().endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
        return ResponseEntity.ok().contentType(type).body(new PathResource(path));
    }

    // ---------- Admin only ----------

    @Operation(summary = "Validate a report (ADMIN)", description = "Only SUBMITTED reports can be reviewed (409 otherwise).")
    @PatchMapping("/{id}/validate")
    public Report validate(@PathVariable Long id,
                           @Parameter(description = ROLE_DESC, example = "ADMIN")
                           @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
                           @RequestBody(required = false) ReviewRequest body) {
        requireAdmin(role);
        return service.review(id, ReportStatus.VALIDATED, body == null ? null : body.comment());
    }

    @Operation(summary = "Reject a report (ADMIN)")
    @PatchMapping("/{id}/reject")
    public Report reject(@PathVariable Long id,
                         @Parameter(description = ROLE_DESC, example = "ADMIN")
                         @RequestHeader(value = "X-User-Role", defaultValue = "") String role,
                         @RequestBody(required = false) ReviewRequest body) {
        requireAdmin(role);
        return service.review(id, ReportStatus.REJECTED, body == null ? null : body.comment());
    }

    @Operation(summary = "Delete a report (ADMIN)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @Parameter(description = ROLE_DESC, example = "ADMIN")
                       @RequestHeader(value = "X-User-Role", defaultValue = "") String role) {
        requireAdmin(role);
        service.delete(id);
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equalsIgnoreCase(role))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ADMIN role required (send header X-User-Role: ADMIN)");
    }
}
