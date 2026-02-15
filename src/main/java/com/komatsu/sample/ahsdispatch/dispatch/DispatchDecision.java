package com.komatsu.sample.ahsdispatch.dispatch;

public record DispatchDecision(String missionId, String truckId, double score) {}
