package model;

public enum ActivityType {
    WALKING("Walking"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    STATIONARY("Stationary");

    private final String name;

    ActivityType(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return name;
    }
}