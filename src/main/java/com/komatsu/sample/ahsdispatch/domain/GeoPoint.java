package com.komatsu.sample.ahsdispatch.domain;

public record GeoPoint(double x, double y) {
    public double distanceTo(GeoPoint other) {
        double dx = x - other.x();
        double dy = y - other.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
