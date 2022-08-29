package com.mkoyu.bluetoothdemo;

import androidx.annotation.NonNull;

public class VirbrationInfo {
    private double acc;
    private double speed;
    private double distance;

    public double getAcc() {
        return acc;
    }

    public void setAcc(double acc) {
        this.acc = acc;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @NonNull
    public String toString(){
        return "{acc:"+this.acc+",speed:"+this.speed+",distance:"+this.distance+"}";
    }
}
