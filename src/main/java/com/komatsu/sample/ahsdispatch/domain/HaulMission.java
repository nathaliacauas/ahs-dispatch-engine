package com.komatsu.sample.ahsdispatch.domain;

import java.util.Objects;

public final class HaulMission {

    private final String id;
    private final GeoPoint pickup;
    private final GeoPoint dropoff;
    private final MissionPriority priority;

    public HaulMission(String id, GeoPoint pickup, GeoPoint dropoff, MissionPriority priority) {
        this.id = Objects.requireNonNull(id);
        this.pickup = Objects.requireNonNull(pickup);
        this.dropoff = Objects.requireNonNull(dropoff);
        this.priority = Objects.requireNonNull(priority);
    }

    public String id() { return id; }
    public GeoPoint pickup() { return pickup; }
    public GeoPoint dropoff() { return dropoff; }
    public MissionPriority priority() { return priority; }
}
