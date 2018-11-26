package functions;

import java.util.HashMap;
import java.util.function.Function;

import changeDemandWithLaneNum.LaneUeLink;
import entity.BPRUeLink;
import entity.TNTPLink;
import graph.Graph;

public class GraphConvertor{
    public static Graph<String,BPRUeLink> tntp2Bpr(Graph<String,? extends TNTPLink> target){
        Graph<String, BPRUeLink> newGraph = new Graph<String, BPRUeLink>();
		for (Graph.Entry<String, ? extends TNTPLink> e : target.entrySet()) {
			String begin = e.getBegin();
			String end = e.getEnd();
			TNTPLink link = e.getLink();
			newGraph.addDiEdge(begin, end, new BPRUeLink(link));
		}
		return newGraph;
    }

    public static Graph<String,LaneUeLink> transp2Lane(Graph<String,HashMap<String,String>> target){
        Function<HashMap<String,String>,Boolean> filter = (HashMap<String,String> map)->{
            if(map.get("fftt").equals("#N/A") || map.get("length").equals("#N/A")||map.get("numLanes").equals("#N/A")){
                return false;
            }else{
                return true;
            }
        };
        Graph<String,LaneUeLink> graph = new Graph<String,LaneUeLink>();
        for(Graph.Entry<String,HashMap<String,String>> e: target.entrySet()){
            HashMap<String,String> link = e.getLink();
            if(filter.apply(link)){
                String begin = e.getBegin();
                String end = e.getEnd();
                int numLanes = Integer.parseInt(link.get("numLanes"));
                float length = Float.parseFloat(link.get("length"));
                float fftt =Float.parseFloat(link.get("fftt"));
                graph.addDiEdge(begin, end, new LaneUeLink(numLanes,length,fftt));
            }
        }
        return graph;
    }
}