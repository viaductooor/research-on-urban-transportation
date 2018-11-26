package graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Get shortest path by Dijkstra Algorithm
 * 
 * @author John Smith
 *
 * @param <V> the type of vertex
 * @param <E> the type of edge
 */
public class ShortestPath<V, E> {

	/**
	 * Standard Dijkstra Algorithm which to get shortest paths between one source
	 * vertex and all other vertices
	 * 
	 * @param g
	 *            graph to be performed
	 * @param begin
	 *            source vertex
	 * @return
	 */
	public HashMap<V, ShortestPath.Node<V>> one2all(Graph<V, ? extends WeightedEdge> g, V begin) {
		HashMap<V, Node<V>> Q = new HashMap<V, ShortestPath.Node<V>>();
		HashMap<V, Node<V>> S = new HashMap<V, ShortestPath.Node<V>>();
		for (V k : g.vertices()) {
			if (k.equals(begin)) {
				Q.put(begin, new ShortestPath.Node<V>(0));
			} else {
				Q.put(k, new ShortestPath.Node<V>());
			}
		}
		while (!Q.isEmpty()) {
			// get the vertex of minimal weight from set Q, and remove it from Q
			V minK = null;
			Node<V> minNode = null;
			float minValue = Float.POSITIVE_INFINITY;
			for (Map.Entry<V, Node<V>> entry : Q.entrySet()) {
				if (entry.getValue().getWeight() < minValue) {
					minK = entry.getKey();
					minValue = entry.getValue().getWeight();
					minNode = entry.getValue();
				}
			}

			if (minK == null) {
				/**
				 * minK is key of the vertex which takes minimal weight from the begin vertex.
				 * When the value of it is null(and Q is not empty), it means there are at least
				 * one vertex with minimal weight of positive_infinity. Therefore we have to
				 * break the loop and leave the left vertices unchanged
				 */
				break;
			}

			Q.remove(minK);
			S.put(minK, minNode);

			for (Entry<V, ? extends WeightedEdge> entry : g.getAdjNodes(minK).entrySet()) {
				V _key = entry.getKey();
				float _weight = entry.getValue().getWeight();
				float _sum = minNode.getWeight() + _weight;
				if (Q.containsKey(_key)) {
					Node<V> _node = Q.get(_key);
					if (_node.getWeight() > _sum) {
						_node.setWeight(_sum);
						_node.setPreK(minK);
					}
				}

			}

		}
		return S;
	}

	/**
	 * Get the shortest path between two vertices immediately without computing all
	 * the shortest paths
	 * 
	 * @param g
	 *            the graph to be performed
	 * @param begin
	 *            source vertex
	 * @param end
	 *            terminate vertex
	 * @return
	 */
	public List<V> shortestPath(Graph<V, ? extends WeightedEdge> g, V begin, V end) {
		HashMap<V, Node<V>> map = one2all(g, begin);
		if (begin.equals(end)) {
			return null;
		}
		if (map.get(end) == null) {
			return null;
		} else {
			List<V> route = new LinkedList<V>();
			route.add(end);
			Node<V> node = map.get(end);
			while (node.getPreK() != begin) {
				route.add(0, node.getPreK());
				node = map.get(node.getPreK());
				if (node == null) {
					break;
				}
			}
			route.add(0, begin);
			return route;
		}
	}

	/**
	 * Get the shortest path after computing all the shortest paths, hence need a
	 * map of the shortest paths which is the result of {@link allPaths()}}
	 * 
	 * @param allShortestPaths
	 * @param begin
	 * @param end
	 * @return
	 */
	public List<V> shortestPath(HashMap<V, HashMap<V, Node<V>>> allShortestPaths, V begin, V end) {
		if (begin.equals(end)) {
			return null;
		}
		HashMap<V, Node<V>> adjs = null;
		Node<V> node = null;
		List<V> route = new LinkedList<V>();
		if ((adjs = allShortestPaths.get(begin)) != null) {
			if ((node = adjs.get(end)) != null) {
				route.add(end);
				while (!node.getPreK().equals(begin)) {
					route.add(0, node.getPreK());
					node = adjs.get(node.getPreK());
				}
				route.add(0, begin);
				return route;
			}
		}
		return null;
	}

	public float shortestPathLength(HashMap<V, HashMap<V, Node<V>>> allShortestPaths, V begin, V end) {
		if (begin.equals(end)) {
			return 0;
		}
		HashMap<V, Node<V>> adjs = null;
		Node<V> node = null;
		if ((adjs = allShortestPaths.get(begin)) != null) {
			if ((node = adjs.get(end)) != null) {
				return node.weight;
			}
		}
		return Float.POSITIVE_INFINITY;
	}

	/**
	 * Get all shortest paths
	 * 
	 * @param g
	 *            the graph to be performed
	 * @return
	 */
	public HashMap<V, HashMap<V, Node<V>>> allPaths(Graph<V, ? extends WeightedEdge> g) {
		Set<V> nodes = g.vertices();
		HashMap<V, HashMap<V, ShortestPath.Node<V>>> map = new HashMap<V, HashMap<V, ShortestPath.Node<V>>>();
		for (V k : nodes) {
			map.put(k, one2all(g, k));
		}
		return map;
	}

	/**
	 * Node is a structure during the shortest path computing. Every vertex has a
	 * Node object, and its two fields separately stand for the minimal weight from
	 * the original vertex and the precursor vertex of the shortest path.
	 * 
	 * @author John Smith
	 *
	 * @param <K>
	 */
	public static class Node<K> {
		float weight;
		K preK;

		public Node() {
			this.weight = Float.POSITIVE_INFINITY;
			this.preK = null;
		}

		public Node(float weight) {
			this.weight = weight;
		}

		public float getWeight() {
			return weight;
		}

		public void setWeight(float weight) {
			this.weight = weight;
		}

		public K getPreK() {
			return preK;
		}

		public void setPreK(K preK) {
			this.preK = preK;
		}

	}
}
