package com.example.trawa01;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class BluetoothData implements Serializable {
    private int DistanceRunning;
    private int DistanceCycling;
    private int DistanceWalking;
    private int TimeStationary;
    private long totalMeters;
    private long totalMillis;

    public BluetoothData(int DistanceRunning, int DistanceCycling, int DistanceWalking, int TimeStationary, int totalMeters, int totalMillis) {
        this.DistanceRunning = DistanceRunning;
        this.DistanceCycling = DistanceCycling;
        this.DistanceWalking = DistanceWalking;
        this.TimeStationary = TimeStationary;
        this.totalMeters = totalMeters;
        this.totalMillis = totalMillis;
    }

    public BluetoothData(String data) {
        // deserializing
        String[] split = data.split(",");
        this.DistanceRunning = Integer.parseInt(split[0]);
        this.DistanceCycling = Integer.parseInt(split[1]);
        this.DistanceWalking = Integer.parseInt(split[2]);
        this.TimeStationary = Integer.parseInt(split[3]);
        this.totalMeters = Integer.parseInt(split[4]);
        this.totalMillis = Integer.parseInt(split[5]);
    }

    public int getDistanceRunning() {
        return DistanceRunning;
    }

    public void setDistanceRunning(int distanceRunning) {
        DistanceRunning = distanceRunning;
    }

    public int getDistanceCycling() {
        return DistanceCycling;
    }

    public void setDistanceCycling(int distanceCycling) {
        DistanceCycling = distanceCycling;
    }

    public int getDistanceWalking() {
        return DistanceWalking;
    }

    public void setDistanceWalking(int distanceWalking) {
        DistanceWalking = distanceWalking;
    }

    public int getTimeStationary() {
        return TimeStationary;
    }

    public void setTimeStationary(int timeStationary) {
        TimeStationary = timeStationary;
    }

    public long getTotalMeters() {
        return totalMeters;
    }

    public void setTotalMeters(long totalMeters) {
        this.totalMeters = totalMeters;
    }

    public long getTotalMillis() {
        return totalMillis;
    }

    public void setTotalMillis(long totalMillis) {
        this.totalMillis = totalMillis;
    }

    @NonNull
    @Override
    public String toString() {
        return DistanceRunning + "," + DistanceCycling + "," + DistanceWalking + "," + TimeStationary + "," + totalMeters + "," + totalMillis;
    }

}
