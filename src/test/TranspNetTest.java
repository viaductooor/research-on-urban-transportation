package test;

import java.util.HashMap;

import entity.DemandLink;
import changeDemandWithLaneNum.LaneUeLink;
import exception.GraphIOException;
import functions.GraphConvertor;
import functions.UserEquilibrium;
import graph.Graph;
import graph.TranspNet;

public class TranspNetTest{
    public static void main(String args[]){
        
        TranspNet net = new TranspNet();
        String[] initAttrs = {"fftt"};
        try{
            net.initFromCsv("files/change-demand-with-lane-num/FFTT.csv", initAttrs, 1);
            net.addAttrFromCsv("files/change-demand-with-lane-num/Link length.csv", "length", 1);
            net.addAttrFromCsv("files/change-demand-with-lane-num/Number of Lanes.csv", "numLanes", 1);
        }catch(GraphIOException e){
            e.printStackTrace();
        }
        
        TranspNet trips = new TranspNet();
        String[] tripsAttrs = {"demand"};
        try {
            trips.initFromCsv("files/change-demand-with-lane-num/Other Volume.csv", tripsAttrs, 1);
        } catch (GraphIOException e) {
            e.printStackTrace();
        }
        // try {
        //     net.writeToCsv("log/net.csv");
        //     trips.writeToCsv("log/trips.csv");
        // } catch (GraphIOException e) {
        //     e.printStackTrace();
        // }
        Graph<String,DemandLink> tripGraph = new Graph<String,DemandLink>();
        for(Graph.Entry<String,HashMap<String,String>> e:trips.entrySet()){
            String initNode = e.getBegin();
            String endNode = e.getEnd();
            float demand = Float.parseFloat(e.getLink().get("otherVolume"));
            tripGraph.addDiEdge(initNode, endNode, new DemandLink(demand));
        }

        Graph<String,LaneUeLink> graph = GraphConvertor.transp2Lane(net);
		// ExcelUtils.writeGraph(graph, "log/before_all_or_nothing_assignment.xls");        
        UserEquilibrium.ue(graph, tripGraph, 100f);
    }

}