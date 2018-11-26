package test;

import functions.GraphConvertor;
import functions.UserEquilibrium;
import graph.Graph;
import entity.DemandLink;
import entity.TNTPLink;
import entity.TNTPReader;
import exception.GraphIOException;
import entity.BPRUeLink;

public class UserEquilibriumTest {
    public static void main(String args[]) {
        Graph<String, TNTPLink> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
        Graph<String, BPRUeLink> newGraph = GraphConvertor.tntp2Bpr(graph);
        Graph<String, DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);
        UserEquilibrium.ue(newGraph, trips, 50);
        try {
            graph.writeToCsv("log/graph.csv");
        } catch (GraphIOException e) {
            e.printStackTrace();
        }
    }

}