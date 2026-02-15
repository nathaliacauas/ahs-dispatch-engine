package com.komatsu.sample.ahsdispatch;

import com.komatsu.sample.ahsdispatch.domain.*;
import com.komatsu.sample.ahsdispatch.dispatch.*;
import com.komatsu.sample.ahsdispatch.util.Logger;

import java.util.ArrayList;
import java.util.List;

public class Run {

    public static void main(String[] args) {

        // 1) Criando frota de exemplo
        List<AutonomousTruck> fleet = new ArrayList<>(List.of(
                new AutonomousTruck("TR-01", new GeoPoint(0, 0), 80, TruckStatus.IDLE),
                new AutonomousTruck("TR-02", new GeoPoint(10, 3), 40, TruckStatus.IDLE),
                new AutonomousTruck("TR-03", new GeoPoint(5, 8), 15, TruckStatus.IDLE),     // bateria baixa
                new AutonomousTruck("TR-04", new GeoPoint(2, 1), 90, TruckStatus.OFFLINE)  // offline
        ));

        // 2) Criando missões
        List<HaulMission> missions = List.of(
                new HaulMission("MS-100", new GeoPoint(1, 1), new GeoPoint(7, 7), MissionPriority.HIGH),
                new HaulMission("MS-200", new GeoPoint(9, 3), new GeoPoint(0, 2), MissionPriority.CRITICAL),
                new HaulMission("MS-300", new GeoPoint(3, 3), new GeoPoint(4, 4), MissionPriority.MEDIUM),
                new HaulMission("MS-400", new GeoPoint(2, 2), new GeoPoint(9, 9), MissionPriority.MEDIUM)
        );

        Logger.info("DEMO_START", "policy=DeterministicScorePolicy");

        runDemo(missions, cloneFleet(fleet), new DeterministicScorePolicy(25));

        Logger.info("DEMO_START", "policy=EtaFirstPolicy");

        runDemo(missions, cloneFleet(fleet), new EtaFirstPolicy(25));

        Logger.info("DEMO_END", "logs=./logs/dispatch.log");

        System.out.println("\nCheck persisted logs at: logs/dispatch.log");
    }

    private static void runDemo(List<HaulMission> missions,
                                List<AutonomousTruck> fleet,
                                DispatchPolicy policy) {

        DispatchEngine engine = new DispatchEngine(policy);
        List<DispatchDecision> decisions = engine.dispatch(missions, fleet);

        System.out.println("\nFinal decisions (" + policy.getClass().getSimpleName() + "):");

        for (DispatchDecision d : decisions) {
            System.out.printf("Mission %s -> Truck %s (score=%.2f)%n",
                    d.missionId(),
                    d.truckId(),
                    d.score());
        }
    }

    /**
     * Clonamos a frota porque o DispatchEngine altera o estado dos caminhões.
     */
    private static List<AutonomousTruck> cloneFleet(List<AutonomousTruck> original) {
        List<AutonomousTruck> copy = new ArrayList<>();

        for (AutonomousTruck t : original) {
            copy.add(new AutonomousTruck(
                    t.id(),
                    t.position(),
                    t.batteryPercent(),
                    t.status()
            ));
        }

        return copy;
    }
}
