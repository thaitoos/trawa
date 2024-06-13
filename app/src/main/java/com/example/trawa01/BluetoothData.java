package com.example.trawa01;

import java.io.Serializable;

public class BluetoothData implements Serializable {
    private int DistanceRunning;
    private int DistanceCycling;
    private int DistanceWalking;
    private int TimeStationary;

    public BluetoothData(int DistanceRunning, int DistanceCycling, int DistanceWalking, int TimeStationary) {
        this.DistanceRunning = DistanceRunning;
        this.DistanceCycling = DistanceCycling;
        this.DistanceWalking = DistanceWalking;
        this.TimeStationary = TimeStationary;
    }

    public BluetoothData(String data) {
        String[] splitData = data.split(",");
        this.DistanceRunning = Integer.parseInt(splitData[0]);
        this.DistanceCycling = Integer.parseInt(splitData[1]);
        this.DistanceWalking = Integer.parseInt(splitData[2]);
        this.TimeStationary = Integer.parseInt(splitData[3]);
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

    @Override
    public String toString() {
        return DistanceRunning + "," + DistanceCycling + "," + DistanceWalking + "," + TimeStationary;
    }

}
