Work Sample
AHS Dispatch Engine – Java 

+ Overview
    This project implements a simplified Autonomous Haulage System (AHS) dispatch engine in Java. The goal is to simulate deterministic mission assignment for autonomous trucks while demonstrating:

    >Object-Oriented Design
    >Strategy Pattern
    >Deterministic behavior
    >Fairness handling
    >Defensive programming
    >Persistent logging
    >Unit testing with JUnit
    >Maven-based build system

+ Architecture
The system is divided into three main layers:

    .Domain Layer (domain)
    Represents core business entities:
        >AutonomousTruck
        >HaulMission
        >GeoPoint
        >MissionPriority
        >TruckStatus

        Encapsulation is enforced:
            >State transitions happen inside AutonomousTruck
            >Validation logic is embedded in domain objects

    .Dispatch Layer (dispatch)
    Implements the decision engine:
        >DispatchPolicy (Strategy interface)
        >DeterministicScorePolicy
        >EtaFirstPolicy
        >DispatchEngine
        >DispatchDecision

        The engine is policy-agnostic and supports strategy swapping without modification.

    .Utility Layer (util)

        >Logger
            Persistent structured logging to:
            logs/dispatch.log

        Includes:

        >Timestamped logs
        >Log rotation (size-based)
        >Fail-safe logging behavior
        >Dispatch Logic

        The default policy uses a weighted deterministic score:

        > score = (priority_weight × mission_priority) − (distance_weight × distance_to_pickup)
        > (battery_weight × battery_level) − (fairness_penalty × previous_assignments)

        This ensures:

        Higher priority missions are preferred
        Closer trucks are preferred
        Low battery trucks are filtered
        Repeated assignment to the same truck is penalized

+ Design Principles

    .Deterministic execution
    .Strategy Pattern
    .Immutability where appropriate
    .Defensive programming
    .Separation of concerns
    .Testability

+ Build

    .Requires:
        >Java 17+
        >Maven 3.9+
    
    .Build and Test:
    -mvn clean test
  
    -mvn package

+ Run

    -java -cp target/classes com.komatsu.sample.ahsdispatch.Run

+ Logs
    .Logs will be persisted in: logs/dispatch.log

+ Author
Nathália Cauás
nathaliacauas@gmail.com
 
