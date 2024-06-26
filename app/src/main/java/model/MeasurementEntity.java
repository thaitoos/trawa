package model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "measurement")
public class MeasurementEntity {
    private double longitude;
    private double latitude;
    private double altitude;
    private float speed;
    private float accuracy;
    @PrimaryKey
    private long time;
    private long activityStartTime;
    private float distance;
    private long duration;

    public MeasurementEntity(double latitude, double longitude, double altitude, float speed, float accuracy, long time, long activityStartTime, float distance, long duration) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.speed = speed;
        this.accuracy = accuracy;
        this.time = time;
        this.activityStartTime = activityStartTime;
        this.distance = distance;
        this.duration = duration;
    }

    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getAltitude() {
        return altitude;
    }
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
    public float getSpeed() {
        return speed;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    public float getAccuracy() {
        return accuracy;
    }
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }
    public long getActivityStartTime() {
        return activityStartTime;
    }
    public void setActivityStartTime(long activityStartTime) {
        this.activityStartTime = activityStartTime;
    }
    public float getDistance() {
        return distance;
    }
    public void setDistance(float distance) {
        this.distance = distance;
    }
    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }
}


