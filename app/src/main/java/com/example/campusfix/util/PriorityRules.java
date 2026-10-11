package com.example.campusfix.util;

public class PriorityRules {

    public static class AssessmentResult {
        private final String priority;
        private final String reason;

        public AssessmentResult(String priority, String reason) {
            this.priority = priority;
            this.reason = reason;
        }

        public String getPriority() {
            return priority;
        }

        public String getReason() {
            return reason;
        }
    }

    public static AssessmentResult evaluatePriority(String category, String location, boolean isHazard, String description) {
        if (isHazard) {
            return new AssessmentResult("CRITICAL", "Flagged as immediate safety hazard.");
        }

        String catLower = category != null ? category.toLowerCase() : "";
        String locLower = location != null ? location.toLowerCase() : "";
        String descLower = description != null ? description.toLowerCase() : "";

        if (descLower.contains("fire") || descLower.contains("spill") || descLower.contains("smoke") || descLower.contains("gas")) {
            return new AssessmentResult("CRITICAL", "Keywords indicate severe safety hazard.");
        }

        if (catLower.contains("electrical") || catLower.contains("hvac") || catLower.contains("ac") || catLower.contains("plumbing")) {
            if (locLower.contains("lab") || locLower.contains("server") || locLower.contains("library") || locLower.contains("auditorium")) {
                return new AssessmentResult("HIGH", "Critical infrastructure issue in high-occupancy/key facility.");
            }
            return new AssessmentResult("HIGH", "Infrastructure utility issue requiring urgent maintenance.");
        }

        if (locLower.contains("lab") || locLower.contains("exam") || locLower.contains("hall")) {
            return new AssessmentResult("MEDIUM", "Affects academic or administrative space.");
        }

        if (catLower.contains("furniture") || catLower.contains("clean") || catLower.contains("cosmetic")) {
            return new AssessmentResult("LOW", "Routine maintenance request.");
        }

        return new AssessmentResult("MEDIUM", "Standard priority assigned.");
    }
}
