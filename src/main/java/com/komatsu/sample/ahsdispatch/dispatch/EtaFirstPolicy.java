package com.komatsu.sample.ahsdispatch.dispatch;

import com.komatsu.sample.ahsdispatch.domain.AutonomousTruck;
import com.komatsu.sample.ahsdispatch.domain.HaulMission;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public final class EtaFirstPolicy implements DispatchPolicy {

    private final int minBattery;

    public EtaFirstPolicy(int minBattery) {
        this.minBattery = minBattery;
    }

    @Override
    public Optional<DispatchDecision> chooseTruck(HaulMission mission, List<AutonomousTruck> fleet) {
        return fleet.stream()
                .filter(t -> t.canAccept(minBattery))
                .map(t -> new DispatchDecision(
                        mission.id(),
                        t.id(),
                        -t.position().distanceTo(mission.pickup())))
                .max(Comparator.comparingDouble(DispatchDecision::score));
    }
}
