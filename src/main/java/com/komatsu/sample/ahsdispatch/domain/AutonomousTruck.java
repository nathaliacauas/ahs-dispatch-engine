package com.komatsu.sample.ahsdispatch.domain;

import java.util.Objects;

public final class AutonomousTruck {

    private final String id;
    private GeoPoint position;
    private int batteryPercent;     // 0..100
    private TruckStatus status;

    // fairness: how many missions this truck received in this run
    private int loadCount;

    public AutonomousTruck(String id, GeoPoint position, int batteryPercent, TruckStatus status) {
        this.id = Objects.requireNonNull(id);
        this.position = Objects.requireNonNull(position);
        this.batteryPercent = clamp(batteryPercent);
        this.status = Objects.requireNonNull(status);
        this.loadCount = 0;
    }

    public String id() { return id; }
    public GeoPoint position() { return position; }
    public int batteryPercent() { return batteryPercent; }
    public TruckStatus status() { return status; }
    public int loadCount() { return loadCount; }

    public boolean canAccept(int minBattery) {
        return status == TruckStatus.IDLE && batteryPercent >= minBattery;
    }

    public void dispatchToMission(String missionId) {
        if (status != TruckStatus.IDLE) {
            throw new IllegalStateException("Truck not available for dispatch: " + id);
        }
        this.status = TruckStatus.DISPATCHED;
        this.loadCount += 1;
        // NOTE: In a real AHS system, we'd also create a mission lifecycle entry here.
    }

    private int clamp(int v) { return Math.max(0, Math.min(100, v)); }
}
