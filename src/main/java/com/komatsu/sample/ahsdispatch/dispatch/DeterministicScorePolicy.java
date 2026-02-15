package com.komatsu.sample.ahsdispatch.dispatch;

import com.komatsu.sample.ahsdispatch.domain.AutonomousTruck;
import com.komatsu.sample.ahsdispatch.domain.HaulMission;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class DeterministicScorePolicy implements DispatchPolicy {

    private final int minBattery;

    private final double wPriority = 10.0;
    private final double wDistance = 1.0;
    private final double wBattery  = 0.2;
    private final double wLoadPenalty = 3.0;

    public DeterministicScorePolicy(int minBattery) {
        this.minBattery = minBattery;
    }

    @Override
    public Optional<DispatchDecision> chooseTruck(HaulMission mission, List<AutonomousTruck> fleet) {
        return fleet.stream()
                .filter(t -> t.canAccept(minBattery))
                .map(t -> new DispatchDecision(mission.id(), t.id(), score(mission, t)))
                .max(Comparator.comparingDouble(DispatchDecision::score));
    }

    private double score(HaulMission mission, AutonomousTruck t) {
        double distance = t.position().distanceTo(mission.pickup());
        double loadPenalty = wLoadPenalty * t.loadCount();

        return (wPriority * mission.priority().weight())
                - (wDistance * distance)
                + (wBattery * t.batteryPercent())
                - loadPenalty;
    }
}