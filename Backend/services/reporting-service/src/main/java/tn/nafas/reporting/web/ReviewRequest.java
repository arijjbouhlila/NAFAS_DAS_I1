package tn.nafas.reporting.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewRequest(@Schema(example = "Confirmed by field team") String comment) {}
