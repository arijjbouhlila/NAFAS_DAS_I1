package tn.nafas.reporting.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reports")
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private double latitude;
    private double longitude;
    private Long locationId;          // station id, resolved via the Location Service later

    private String reporterName;      // will come from the User Management Service later

    @JsonIgnore
    private String photoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.SUBMITTED;

    private String adminComment;
    private Instant createdAt = Instant.now();
    private Instant reviewedAt;

    public String getPhotoUrl() { return photoPath == null ? null : "/api/reports/" + id + "/photo"; }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public Category getCategory() { return category; }
    public void setCategory(Category c) { this.category = c; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double v) { this.latitude = v; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double v) { this.longitude = v; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long l) { this.locationId = l; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String n) { this.reporterName = n; }
    @JsonIgnore public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String p) { this.photoPath = p; }
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus s) { this.status = s; }
    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String c) { this.adminComment = c; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant t) { this.reviewedAt = t; }
}
