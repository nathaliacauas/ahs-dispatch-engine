package com.komatsu.sample.ahsdispatch.dispatch;

import com.komatsu.sample.ahsdispatch.domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PoliciesSwapTest {

    @Test
    void shouldAllowSwappingPoliciesWithoutChangingEngine() {

        List<AutonomousTruck> fleet = new ArrayList<>(List.of(
                new AutonomousTruck("TR-01", new GeoPoint(0, 0), 90, TruckStatus.IDLE),
                new AutonomousTruck("TR-02", new GeoPoint(100, 100), 90, TruckStatus.IDLE)
        ));

        List<HaulMission> missions = List.of(
                new HaulMission("MS-1", new GeoPoint(1, 1), new GeoPoint(2, 2), MissionPriority.HIGH)
        );

        DispatchEngine engine = new DispatchEngine(new EtaFirstPolicy(25));
        var decisions = engine.dispatch(missions, fleet);

        assertEquals(1, decisions.size());
        assertEquals("TR-01", decisions.get(0).truckId());
    }
}
