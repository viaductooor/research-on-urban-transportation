package changeDemandWithLaneNum;

import java.lang.Math;
import java.util.HashMap;

import entity.UserEquilibriumLink;
import exception.GraphException;

public class LaneUeLink implements UserEquilibriumLink {
    public int numLanes;
    public float length;
    public float volume;
    public float auxVolume;
    public float surcharge;
    public float lastSurcharge;
    public float fftt;
    public float travelTime;

    public LaneUeLink() {
        numLanes = 1;
        length = 0;
        volume = 0;
        fftt = 0;
        travelTime = 0;
    }

    public LaneUeLink(int numLanes, float length, float fftt) {
        this.numLanes = numLanes;
        this.length = length;
        this.volume = 0;
        this.auxVolume = 0;
        this.surcharge = 0;
        this.lastSurcharge = 0;
        this.fftt = fftt;
        this.travelTime = fftt;
    }

    public static LaneUeLink parseLaneUeLink(HashMap<String, String> map) throws GraphException {
        if (map.get("capacity") == null || map.get("fftt") == null) {
            throw new GraphException("The target map (Graph<String,HashMap>) lacks attributes 'capacity' and 'fftt'");
        } else {
            float length = Float.parseFloat(map.get("length"));
            float fftt = Float.parseFloat(map.get("fftt"));
            int numLanes = Integer.parseInt(map.get("numLanes"));
            return new LaneUeLink(numLanes, length, fftt);
        }
    }

    public float getMarginalCost() {
        double t1 = length * Math.sqrt(-numLanes * (3021 * volume - 8000000000L * numLanes)) * 9252186839L;
        double t2 = 827540000000000L * length * numLanes;
        return (float) ((t1 + t2) / (6250000000L * volume));
    }

    @Override
    public float getTravelTime() {
        return travelTime;
    }

    @Override
    public float travelTimeFunction(double x) {
        if (x == 0) {
            return fftt;
        } else {
            double t = Math.sqrt(5f) * Math.sqrt(-length * length * numLanes * (3021 * x - 8000000000L * numLanes));
            return (float) (2000 * t / (3021 * x) + surcharge);
        }

    }

    @Override
    public float getWeight() {
        return getTravelTime();
    }

    @Override
    public void setWeight(float w) {
    }

    @Override
    public float travelTimeIntegrate(double x) {
        double fact = 2000 * Math.sqrt((Double) 5.0) / 3021;
        double A = 8000000000L * Math.pow(length * numLanes, 2);
        double B = -3021 * Math.pow(length, 2) * numLanes * x;
        double C = Math.sqrt(A)
                * Math.log(Math.abs((Math.sqrt(A + B * x) - Math.sqrt(A)) / (Math.sqrt(A + B * x) + Math.sqrt(A))));
        double tail = 40000000L * Math.log(x) / 3021;
        return (float) (fact * (2 * Math.sqrt(A + B * x) + C) - tail + surcharge * x);
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolume(double volume) {
        this.volume = (float) volume;
    }

    @Override
    public float getAuxVolume() {
        return auxVolume;
    }

    @Override
    public void setAuxVolume(double auxVolume) {
        this.auxVolume = (float) auxVolume;
    }

    @Override
    public void updateTravelTime() {
        this.travelTime = travelTimeFunction(volume);
    }

}