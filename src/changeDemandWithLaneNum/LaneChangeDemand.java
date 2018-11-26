package changeDemandWithLaneNum;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import entity.DemandLink;
import exception.GraphIOException;
import graph.Graph;
import graph.GraphOutputIgnore;
import graph.ShortestPath;
import graph.ShortestPath.Node;
import graph.TranspNet;
import graph.WeightedEdge;

public class LaneChangeDemand {
    private Graph<String, MyLink> net;
    private Graph<String, MyDemandLink> trips;
    private float convergeThreshold;

    public LaneChangeDemand(TranspNet transnet, TranspNet transtrips, float threashold) {
        this.convergeThreshold = threashold;
        net = new Graph<String, MyLink>();
        trips = new Graph<String, MyDemandLink>();

        Function<HashMap<String, String>, Boolean> filter = (HashMap<String, String> map) -> {
            if (map.get("fftt").equals("#N/A") || map.get("length").equals("#N/A")
                    || map.get("numLanes").equals("#N/A")) {
                return false;
            } else {
                return true;
            }
        };
        transnet.validityCheck(filter);
        for (Graph.Entry<String, HashMap<String, String>> e : transnet.entrySet()) {
            if (e.getLink().get("valid").equals("1")) {
                String begin = e.getBegin();
                String end = e.getEnd();
                MyLink link = MyLink.parseMyLink(e.getLink());
                net.addDiEdge(begin, end, link);
            }
        }
        for (Graph.Entry<String, HashMap<String, String>> e : transtrips.entrySet()) {
            String begin = e.getBegin();
            String end = e.getEnd();
            MyDemandLink link = MyDemandLink.parseDemandLink(e.getLink());
            trips.addDiEdge(begin, end, link);
        }
    }

    public boolean converge() {
        float sum = 0;
        for (Graph.Entry<String, MyLink> e : net.entrySet()) {
            sum += Math.abs(e.getLink().getLastSurcharge() - e.getLink().getSurcharge());
        }
        System.out.println("sum of changed surcharge: " + sum);

        if (sum < convergeThreshold) {
            return true;
        }
        return false;
    }

    public void run(float loadStep) {
        int n = 1;
        ShortestPath<String, MyLink> sp = new ShortestPath<String, MyLink>();

        // Set all demand to 0, remaining originDemand value of demand
        for (Graph.Entry<String, MyDemandLink> e : trips.entrySet()) {
            e.getLink().setDemand(0);
        }

        do {
            for (Graph.Entry<String, MyLink> e : net.entrySet()) {
                MyLink link = e.getLink();
                // Calculate and update link Marginal cost
                link.updateMarginalCost();
                // Calculate and update link surcharge
                link.updateSurcharge(n);
            }

            // Traffic Assignment
            for (Graph.Entry<String, MyDemandLink> e : trips.entrySet()) {
                MyDemandLink mdl = e.getLink();
                String begin = e.getBegin();
                String end = e.getEnd();
                mdl.setDemand(mdl.getDemand() + loadStep * mdl.getOriginDemand());
                List<String> route = sp.shortestPath(net, begin, end);
                if (route != null) {
                    for (int i = 0; i < route.size() - 1; i++) {
                        MyLink l = net.getEdge(route.get(i), route.get(i + 1));
                        l.setVolume(l.getVolume() + mdl.getDemand());
                        l.updateTravelTime();
                    }
                }
            }
            n++;
        } while (!converge());
    }

    public void run2() {
        int n = 1;
        ShortestPath<String, MyLink> sp = new ShortestPath<String, MyLink>();
        do {
            try {
                net.writeToCsv("log/net_run2_" + n + ".csv");
            } catch (GraphIOException e) {
                e.printStackTrace();
            }

            for (Graph.Entry<String, MyLink> e : net.entrySet()) {
                MyLink link = e.getLink();
                // Calculate and update link Marginal cost
                link.updateMarginalCost();
                // Calculate and update link surcharge
                link.updateSurcharge(n);
                // Calculate and update link travelTime according to surcharge
                link.updateTravelTime();
            }
            System.out.println("Begin to compute all shortest paths...");
            HashMap<String, HashMap<String, Node<String>>> paths = sp.allPaths(net);
            System.out.println("Shortest paths computing done.");
            for (Graph.Entry<String, MyDemandLink> e : trips.entrySet()) {
                List<String> route = sp.shortestPath(paths, e.getBegin(), e.getEnd());
                if (route != null) {
                    for (int i = 0; i < route.size() - 1; i++) {
                        MyLink mlink = net.getEdge(route.get(i), route.get(i + 1));
                        mlink.setVolume(mlink.getVolume() + e.getLink().getDemand());
                    }
                }
            }
            n++;
        } while (!converge());

    }
}

class MyDemandLink extends DemandLink {
    private float originDemand;

    public MyDemandLink(float demand) {
        super(demand);
        this.originDemand = demand;
    }

    /**
     * @return the originDemand
     */
    public float getOriginDemand() {
        return originDemand;
    }

    public static MyDemandLink parseDemandLink(HashMap<String, String> map) {
        float d = Float.parseFloat(map.get("demand"));
        return new MyDemandLink(d);
    }
}

class MyLink implements WeightedEdge {
    private float length;
    private float fftt;
    private int numLanes;
    private float marginalCost;
    private float volume;
    private float travelTime;
    private float otherVolume;
    @GraphOutputIgnore
    private float lastSurcharge;
    private float surcharge;

    public static MyLink parseMyLink(HashMap<String, String> map) {
        float _fftt = Float.parseFloat(map.get("fftt"));
        int _numLanes = Integer.parseInt(map.get("numLanes"));
        float _length = Float.parseFloat(map.get("length"));
        float _otherVolume = Float.parseFloat(map.get("otherVolume"));
        return new MyLink(_fftt, _numLanes, _length, _otherVolume);

    }

    public MyLink(float fftt, int numLanes, float length, float otherVolume) {
        this.fftt = fftt;
        this.numLanes = numLanes;
        this.length = length;
        this.otherVolume = otherVolume;
        this.surcharge = 0;
        this.lastSurcharge = 0;
        this.volume = 0;
        this.marginalCost = 0;
        updateTravelTime();
    }

    /**
     * @return the numLanes
     */
    public int getNumLanes() {
        return numLanes;
    }

    /**
     * @return the fftt
     */
    public float getFftt() {
        return fftt;
    }

    /**
     * @param volume the volume to set
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }

    /**
     * @return the volume
     */
    public float getVolume() {
        return volume;
    }

    public float travelTimeFunction(double x) {
        if (x < 1) {
            return fftt;
        } else {
            double t = Math.sqrt(5f) * Math.sqrt(-length * length * numLanes * (3021 * x - 8000000000L * numLanes));
            return (float) (2000 * t / (3021 * x) + surcharge);
        }

    }

    public void updateMarginalCost() {
        float vol = 1f;
        if (volume > 0) {
            vol = volume;
        }
        double t1 = length * Math.sqrt(-numLanes * (3021 * vol - 8000000000L * numLanes)) * 9252186839L;
        double t2 = 827540000000000L * length * numLanes;
        double t = t1 + t2;
        marginalCost = (float) (t / (6250000000L * vol));
        if (Float.isInfinite(marginalCost)) {
            marginalCost = 999999999f;
        }
    }

    public float getMarinalCost() {
        return marginalCost;
    }

    public void updateTravelTime() {
        travelTime = travelTimeFunction(volume + otherVolume);
    }

    public float getTravelTime() {
        return travelTime;
    }

    public float getSurcharge() {
        return surcharge;
    }

    public void updateSurcharge(int iter) {
        float t = surcharge;
        surcharge = (1f / iter) * marginalCost + (1 - 1f / iter) * lastSurcharge;
        lastSurcharge = t;
    }

    /**
     * @param surcharge the surcharge to set
     */
    public void setSurcharge(float surcharge) {
        this.surcharge = surcharge;
    }

    /**
     * @param lastSurcharge the lastSurcharge to set
     */
    public void setLastSurcharge(float lastSurcharge) {
        this.lastSurcharge = lastSurcharge;
    }

    /**
     * @return the lastSurcharge
     */
    public float getLastSurcharge() {
        return lastSurcharge;
    }

    @Override
    public float getWeight() {
        return travelTime;
    }

    @Override
    public void setWeight(float w) {

    }

}