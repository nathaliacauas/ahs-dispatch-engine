package com.komatsu.sample.ahsdispatch.dispatch;

import com.komatsu.sample.ahsdispatch.domain.AutonomousTruck;
import com.komatsu.sample.ahsdispatch.domain.HaulMission;

import java.util.List;
import java.util.Optional;

public interface DispatchPolicy {
    Optional<DispatchDecision> chooseTruck(HaulMission mission, List<AutonomousTruck> fleet);
}
