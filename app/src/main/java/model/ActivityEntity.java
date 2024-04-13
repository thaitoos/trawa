package model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "activity")
public class ActivityEntity {
    @PrimaryKey
    private long startTime;
    private long duration;
    private String name;
    private String description;
    private boolean isStationary;
    private ActivityType type;
    private double distance;

    public ActivityEntity(long startTime,long duration,
                          String name, String description, boolean isStationary,
                          ActivityType type, double distance) {
        this.startTime = startTime;
        this.duration = duration;
        this.name = name;
        this.description = description;
        this.isStationary = isStationary;
        this.type = type;
        this.distance = distance;
    }

    @Ignore
    public ActivityEntity(long startTime){
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isStationary() {
        return isStationary;
    }
    public void setStationary(boolean stationary) {
        isStationary = stationary;
    }
    public ActivityType getType() {
        return type;
    }
    public void setType(ActivityType type) {
        this.type = type;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void fillInData(long duration, String name, String description, boolean isStationary, ActivityType type, double distance){
        this.duration = duration;
        this.name = name;
        this.description = description;
        this.isStationary = isStationary;
        this.type = type;
        this.distance = distance;
    }



}
