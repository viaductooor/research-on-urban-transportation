package functions;

import java.util.List;
import java.util.function.BiFunction;

import entity.BPRUeLink;
import entity.UserEquilibriumLink;
import graph.Graph;
import graph.Graph.Entry;
import graph.ShortestPath;
import graph.WeightedEdge;

public class UserEquilibrium {

	/**
	 * All-or-nothing assignment. When given a trip(or an origin-destination pair)
	 * of any demand, we first get the shortest path of the trip. Then we add the
	 * demand as volume onto every single link which composes the shortest path.For
	 * example, if we have a trip (a,d) with demand of n, We first get the shortest
	 * path (a,c,d), then we need to add n to the volume of link(a,c) and link(c,d).
	 * 
	 * @param links
	 * @param trips
	 */
	private static void allOrNothing(Graph<String, ? extends UserEquilibriumLink> links,
			Graph<String, ? extends WeightedEdge> trips) {
		ShortestPath<String, BPRUeLink> sp = new ShortestPath<String, BPRUeLink>();
		BiFunction<String, String, List<String>> getPath = (String begin, String end) -> sp.shortestPath(links, begin, end);

		clearAuxFlow(links);

		int count = 0;
		for (Entry<String, ? extends WeightedEdge> e : trips.entrySet()) {
			String begin = e.getBegin();
			String end = e.getEnd();
			float weight = e.getLink().getWeight();
			List<String> route = getPath.apply(begin, end);
			
			if (route != null) {
				//DEBUG
				count ++;
				System.out.println("all-or-nothing-assignment:"+count);
				
				for (int i = 0; i < route.size() - 1; i++) {
					String init = route.get(i);
					String term = route.get(i + 1);
					UserEquilibriumLink l = links.getEdge(init, term);
					l.setAuxVolume(l.getAuxVolume() + weight);
				}
			}
		}
	}

	/**
	 * Line search.Alpha is a parameter between 0 and 1. In this method, we get the
	 * optimal alpha which is going to minimize the total flow(by the step
	 * {@link move}) by trying incrementally)
	 * 
	 * @param graph
	 * @return
	 */
	private static float lineSearch(Graph<String, ? extends UserEquilibriumLink> graph) {
		float alpha = 1;
		float minSum = Float.POSITIVE_INFINITY;
		for (float al = 0; al < 1.00; al += 0.001) {
			float sum = 0;
			for (UserEquilibriumLink l : graph.edges()) {
				float vol = l.getVolume();
				float auxvol = l.getAuxVolume();
				float upper = vol + al * (auxvol - vol);
				sum += l.travelTimeIntegrate(upper);
			}
			if (sum < minSum) {
				alpha = al;
				minSum = sum;
			}
		}
		return alpha;
	}

	/**
	 * Change volume of every link of the graph to decrease the total flow. The
	 * basis of alpha is in {@link lineSearch}}
	 * 
	 * @param graph
	 * @param alpha
	 */
	private static void move(Graph<String, ? extends UserEquilibriumLink> graph, float alpha) {
		for (Graph.Entry<String, ? extends UserEquilibriumLink> e : graph.entrySet()) {
			UserEquilibriumLink l = e.getLink();
			float vol = l.getVolume() + alpha * (l.getAuxVolume() - l.getVolume());
			l.setVolume(vol);
		}
	}

	/**
	 * Set auxFlow(which is also known as y in the algorithm) of every link of the
	 * graph to zero.
	 * 
	 * @param graph
	 */
	private static void clearAuxFlow(Graph<String, ? extends UserEquilibriumLink> graph) {
		for (UserEquilibriumLink l : graph.edges()) {
			l.setAuxVolume(0);
		}
	}

	/**
	 * Set flow of every link with the value of auxFlow. In some of the methods (eg.
	 * allOrNothing) we don't directly change the flow of every link, we firstly
	 * change auxFlow of them and then change flow when needed.
	 * 
	 * @param graph
	 */
	private static void y2x(Graph<String, ? extends UserEquilibriumLink> graph) {
		for (UserEquilibriumLink l : graph.edges()) {
			l.setVolume(l.getAuxVolume());
		}
	}

	/**
	 * Update travel-time of every link according to volume, free-flow-travel-time,
	 * capacity etc.
	 * 
	 * @param graph
	 */
	private static void updateAllTraveltime(Graph<String, ? extends UserEquilibriumLink> graph) {
		for (UserEquilibriumLink l : graph.edges()) {
			l.updateTravelTime();
		}
	}

	/**
	 * Get the total flow of a graph composed by UeLinks.
	 * 
	 * @param graph
	 * @return
	 */
	public static float getTotalVolume(Graph<String, ? extends UserEquilibriumLink> graph) {
		float sum = 0;
		for (UserEquilibriumLink l : graph.edges()) {
			sum += l.getVolume();
		}
		return sum;
	}

	public static float getTotalTravelTime(Graph<String, UserEquilibriumLink> graph) {
		float sum = 0;
		for (UserEquilibriumLink l : graph.edges()) {
			sum += l.getTravelTime() * l.getVolume();
		}
		return sum;
	}

	/**
	 * 
	 * @param graph
	 * @param trips
	 * @param diff
	 * @return
	 */
	public static void ue(Graph<String, ? extends UserEquilibriumLink> workgraph, Graph<String, ? extends WeightedEdge> trips,
			float diff) {

		// step 0
		allOrNothing(workgraph, trips);
		y2x(workgraph);
		float alpha = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			updateAllTraveltime(workgraph); // step 1
			allOrNothing(workgraph, trips);// step 2
			alpha = lineSearch(workgraph);// step 3
			float beforemove = getTotalVolume(workgraph);
			move(workgraph, alpha);
			float aftermove = getTotalVolume(workgraph);
			step = aftermove - beforemove;
			System.out.println(step);
		}
		updateAllTraveltime(workgraph);
	}

}
