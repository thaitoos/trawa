package model;

import androidx.annotation.NonNull;

public enum ActivityType {
    WALKING("Walking"),
    RUNNING("Running"),
    CYCLING("Cycling"),
    STATIONARY("Stationary");

    private final String name;

    ActivityType(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}