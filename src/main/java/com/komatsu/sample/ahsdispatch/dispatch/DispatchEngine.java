package com.komatsu.sample.ahsdispatch.dispatch;

import com.komatsu.sample.ahsdispatch.domain.AutonomousTruck;
import com.komatsu.sample.ahsdispatch.domain.HaulMission;
import com.komatsu.sample.ahsdispatch.util.Logger;

import java.util.*;

public final class DispatchEngine {

    private final DispatchPolicy policy;

    public DispatchEngine(DispatchPolicy policy) {
        this.policy = Objects.requireNonNull(policy, "policy must not be null");
    }

    /**
     * Dispatches missions to trucks using the injected policy.
     * Design goals:
     * - deterministic behavior (stable ordering)
     * - defensive checks (do not crash for minor data issues)
     * - logs suitable for later inspection (persisted by Logger)
     */
    public List<DispatchDecision> dispatch(List<HaulMission> missions, List<AutonomousTruck> fleet) {
        Objects.requireNonNull(missions, "missions must not be null");
        Objects.requireNonNull(fleet, "fleet must not be null");

        Logger.info("DISPATCH_REQUEST",
                "missions=" + missions.size() +
                " fleet=" + fleet.size() +
                " policy=" + policy.getClass().getSimpleName());

        // deterministic ordering: higher priority first, then mission id (tie-break)
        List<HaulMission> ordered = new ArrayList<>(missions);
        ordered.sort(Comparator
                .comparingInt((HaulMission m) -> -m.priority().weight())
                .thenComparing(HaulMission::id));

        // index trucks by id for clarity and O(1) lookup when applying side effects
        Map<String, AutonomousTruck> fleetById = new HashMap<>();
        for (AutonomousTruck t : fleet) {
            if (t == null) {
                Logger.warn("FLEET_DATA_ISSUE", "reason=null_truck_entry");
                continue;
            }
            if (fleetById.putIfAbsent(t.id(), t) != null) {
                Logger.warn("FLEET_DATA_ISSUE", "reason=duplicate_truck_id truck=" + t.id());
            }
        }

        List<DispatchDecision> decisions = new ArrayList<>();

        for (HaulMission mission : ordered) {
            if (mission == null) {
                Logger.warn("MISSION_DATA_ISSUE", "reason=null_mission_entry");
                continue;
            }

            Optional<DispatchDecision> chosen;
            try {
                chosen = policy.chooseTruck(mission, fleet);
            } catch (RuntimeException e) {
                // policy should not bring down the engine; log and continue
                Logger.error("POLICY_FAILURE",
                        "mission=" + mission.id() +
                        " policy=" + policy.getClass().getSimpleName() +
                        " error=" + e.getClass().getSimpleName() +
                        " msg=" + safeMsg(e.getMessage()));
                continue;
            }

            if (chosen.isEmpty()) {
                Logger.warn("DISPATCH_REJECTED",
                        "mission=" + mission.id() +
                        " priority=" + mission.priority() +
                        " reason=no_eligible_truck");
                continue;
            }

            DispatchDecision decision = chosen.get();

            // apply side effect with explicit checks
            AutonomousTruck truck = fleetById.get(decision.truckId());
            if (truck == null) {
                Logger.error("DISPATCH_INCONSISTENT",
                        "mission=" + mission.id() +
                        " reason=truck_not_found truck=" + decision.truckId());
                continue;
            }

            try {
                truck.dispatchToMission(mission.id());
            } catch (RuntimeException e) {
                Logger.error("DISPATCH_APPLY_FAILED",
                        "mission=" + mission.id() +
                        " truck=" + truck.id() +
                        " error=" + e.getClass().getSimpleName() +
                        " msg=" + safeMsg(e.getMessage()));
                continue;
            }

            Logger.info("DISPATCH_DECISION",
                    "mission=" + decision.missionId() +
                    " truck=" + decision.truckId() +
                    " score=" + String.format("%.2f", decision.score()));

            decisions.add(decision);
        }

        Logger.info("DISPATCH_SUMMARY",
                "assigned=" + decisions.size() +
                " rejected=" + (missions.size() - decisions.size()));

        return decisions;
    }

    private String safeMsg(String msg) {
        if (msg == null) return "null";
        // avoid breaking log format
        return msg.replace("\n", " ").replace("\r", " ");
    }
}
