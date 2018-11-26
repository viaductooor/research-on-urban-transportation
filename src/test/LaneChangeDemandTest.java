package test;

import changeDemandWithLaneNum.LaneChangeDemand;
import exception.GraphIOException;
import graph.TranspNet;

public class LaneChangeDemandTest {
    public static void main(String args[]) {
        String home = "files/change-demand-with-lane-num/";
        TranspNet net = new TranspNet();
        TranspNet trips = new TranspNet();
        String[] attrsTrips = { "demand" };
        String[] attrsNet = { "fftt" };
        try {
            net.initFromCsv(home + "FFTT.csv", attrsNet, 1);
            net.addAttrFromCsv(home + "Link length.csv", "length", 1);
            net.addAttrFromCsv(home + "Number of Lanes.csv", "numLanes", 1);
            net.addAttrFromCsv(home + "Other Volume.csv", "otherVolume", 1);
            trips.initFromCsv(home + "Sum_OD_Count.csv", attrsTrips, 1);
        } catch (GraphIOException e) {
            e.printStackTrace();
        }

        LaneChangeDemand lcd = new LaneChangeDemand(net, trips, 1000);
        lcd.run2();

    }
}