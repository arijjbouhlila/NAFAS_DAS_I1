package com.gabesecowatch.locationservice.station;

public record StationRequest(
        String name,
        Double latitude,
        Double longitude
) {
}
