package tn.nafas.reporting.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import tn.nafas.reporting.model.Category;

public record ReportRequest(
        @Schema(example = "Black smoke near the factory") @NotBlank @Size(max = 120) String title,
        @Schema(example = "Thick black smoke visible since this morning, strong smell.") @NotBlank @Size(max = 1000) String description,
        @Schema(example = "AIR") @NotNull Category category,
        @Schema(example = "33.8815") @NotNull Double latitude,
        @Schema(example = "10.0982") @NotNull Double longitude,
        @Schema(description = "Optional monitoring station id (Location Service)", example = "1") Long locationId,
        @Schema(example = "Ali") @Size(max = 80) String reporterName) {}
