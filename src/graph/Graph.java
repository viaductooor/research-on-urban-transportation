package graph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.opencsv.CSVWriter;

import exception.GraphIOException;

/**
 * An implementation of graph with structure of adjacency list.
 * 
 * @author John Smith
 *
 * @param <V>
 * @param <E>
 * 
 */
public class Graph<V, E> {
	private HashMap<V, HashMap<V, E>> map;

	public Graph() {
		map = new HashMap<V, HashMap<V, E>>();
	}

	/**
	 * 
	 * @param vertex
	 * @return the set of the node's adjacent nodes
	 */
	public HashMap<V, E> addVertex(V vertex) {
		return map.put(vertex, new HashMap<V, E>());
	}

	/**
	 * 
	 * @param begin
	 * @param end
	 * @param edge
	 * @return the added edge
	 */
	public E addDiEdge(V begin, V end, E edge) {
		if (map.containsKey(begin)) {
			return map.get(begin).put(end, edge);
		} else {
			addVertex(begin);
			return addDiEdge(begin, end, edge);
		}
	}

	public void addEdge(V begin, V end, E edge) {
		addDiEdge(begin, end, edge);
		addDiEdge(end, begin, edge);
	}

	/**
	 * Remove the edge without changing any vertex.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public boolean removeDiEdge(V begin, V end) {
		if (!this.containsEdge(begin, end)) {
			return false;
		} else {
			HashMap<V, E> nodes = map.get(begin);
			nodes.remove(end);
		}
		return true;
	}

	/**
	 * Remove the edge. If the action makes any vertex degree of 0, remove it.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public boolean removeDiEdgeWithVertices(V begin, V end) {
		if (!this.containsEdge(begin, end)) {
			return false;
		} else {
			HashMap<V, E> nodes = map.get(begin);
			nodes.remove(end);
			if (degreeAll(begin) == 0) {
				map.remove(begin);
			}
		}
		return true;
	}

	/**
	 * Degree out.
	 * 
	 * @param vertex
	 * @return
	 */
	public int degreeOut(V vertex) {
		HashMap<V, E> nodes = map.get(vertex);
		return nodes.size();
	}

	/**
	 * Degree in.
	 * 
	 * @param vertex
	 * @return
	 */
	public int degreeIn(V vertex) {
		int degreeIn = 0;
		for (Map.Entry<V, HashMap<V, E>> nodesEntry : map.entrySet()) {
			HashMap<V, E> nodes = nodesEntry.getValue();
			for (Map.Entry<V, E> e : nodes.entrySet()) {
				if (e.getKey() == vertex) {
					degreeIn++;
				}
			}
		}
		return degreeIn;
	}

	/**
	 * Sum up degree in and degree out.
	 * 
	 * @param vertex
	 * @return
	 */
	public int degreeAll(V vertex) {
		return degreeIn(vertex) + degreeOut(vertex);
	}

	/**
	 * Remove the edge (bidirectional) without changing any vertex.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public boolean removeEdge(V begin, V end) {
		boolean b1 = removeDiEdge(begin, end);
		boolean b2 = removeDiEdge(end, begin);
		return b1 & b2;
	}

	/**
	 * Remove the edge (bidirectional). If the action makes any vertex degree of 0,
	 * remove it.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public boolean removeEdgeWithVertices(V begin, V end) {
		boolean b1 = removeDiEdgeWithVertices(begin, end);
		boolean b2 = removeDiEdgeWithVertices(end, begin);
		return b1 & b2;
	}

	/**
	 * 
	 * @param begin
	 * @param end
	 * @return edge
	 */
	public E getEdge(V begin, V end) {
		if (map.containsKey(begin)) {
			HashMap<V, E> adjs = map.get(begin);
			if (adjs.containsKey(end)) {
				return adjs.get(end);
			}
		}
		return null;
	}

	/**
	 * 
	 * @param node
	 * @return the map of adjacent nodes of this node
	 */
	public HashMap<V, E> getAdjNodes(V node) {
		return map.get(node);
	}

	public static class Entry<V, E> {
		V begin;
		V end;
		E edge;

		public Entry(V begin, V end, E link) {
			this.begin = begin;
			this.end = end;
			this.edge = link;
		}

		public V getBegin() {
			return begin;
		}

		public V getEnd() {
			return end;
		}

		public E getLink() {
			return edge;
		}
	}

	/**
	 * An entrySet is a set of entries of type {@link Entry}. Use function to visit
	 * all the links of the graph.
	 * 
	 * @return
	 */
	public List<Graph.Entry<V, E>> entrySet() {
		List<Entry<V, E>> l = new LinkedList<Graph.Entry<V, E>>();
		for (Map.Entry<V, HashMap<V, E>> _m : map.entrySet()) {
			V init = _m.getKey();
			for (Map.Entry<V, E> _l : _m.getValue().entrySet()) {
				V end = _l.getKey();
				E link = _l.getValue();
				l.add(new Graph.Entry<V, E>(init, end, link));
			}
		}
		return l;
	}

	/**
	 * Return all the distinct nodes of the graph
	 * 
	 * @return
	 */
	public Set<V> vertices() {
		HashSet<V> set = new HashSet<V>();
		for (Map.Entry<V, HashMap<V, E>> entry : map.entrySet()) {
			set.add(entry.getKey());
		}
		return set;
	}

	public Set<E> edges() {
		HashSet<E> set = new HashSet<E>();
		for (Map.Entry<V, HashMap<V, E>> _m : map.entrySet()) {
			for (Map.Entry<V, E> _l : _m.getValue().entrySet()) {
				E link = _l.getValue();
				set.add(link);
			}
		}
		return set;
	}

	/**
	 * Return true if the graph contains the edge which begins, else return false.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	public boolean containsEdge(V begin, V end) {
		if (getEdge(begin, end) != null)
			return true;
		return false;
	}

	/**
	 * Return true if the graph contains the vertex, else return false.
	 * 
	 * @param vertex
	 * @return
	 */
	public boolean containsVertex(V vertex) {
		if (map.containsKey(vertex))
			return true;
		return false;
	}

	/**
	 * Simple description of edges and vertices.
	 * 
	 * @return
	 */
	public String describe() {
		int numVertices = vertices().size();
		int numEdges = edges().size();
		return numVertices + " vertices; " + numEdges + " edges.";
	}

	/**
	 * Write the graph into a specific file of format csv. By default every field of
	 * the link should be written down, except the ones with tag
	 * {@link GraphOutputIgnore}.
	 * 
	 * @param url output file url
	 * @throws GraphIOException
	 */
	public void writeToCsv(String url) throws GraphIOException {
		File file = new File(url);
		File parent = new File(file.getParent());
		if (!parent.exists()) {
			parent.mkdirs();
		}

		CSVWriter writer = null;
		Function<List<String>, String[]> list2array = (List<String> list) -> {
			String[] arr = new String[list.size()];
			list.toArray(arr);
			return arr;
		};
		try {
			writer = new CSVWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (entrySet().size() < 1) {
			throw new GraphIOException("The graph is empty thus cannot be written into a csv file.");
		}
		List<String> headerList = new ArrayList<String>();
		headerList.add("begin");
		headerList.add("end");
		List<Field> fieldList = new ArrayList<Field>();
		E firstLink = entrySet().get(0).getLink();
		for (Field f : firstLink.getClass().getDeclaredFields()) {
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			GraphOutputIgnore goi = f.getAnnotation(GraphOutputIgnore.class);
			if (goi == null) {
				headerList.add(f.getName());
				fieldList.add(f);
			}
		}

		// write header
		String[] headerArr = list2array.apply(headerList);
		writer.writeNext(headerArr);

		// Write content
		try {
			for (Graph.Entry<V, E> e : entrySet()) {
				E edge = e.getLink();
				List<String> list = new ArrayList<>();
				list.add(e.getBegin().toString());
				list.add(e.getEnd().toString());
				for (Field f : fieldList) {
					Object value = f.get(edge);
					if (value != null) {
						list.add(value.toString());
					} else {
						list.add("null");
					}
				}
				String[] arr = list2array.apply(list);
				writer.writeNext(arr);
			}
			writer.close();
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
