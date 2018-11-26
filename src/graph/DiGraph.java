package graph;

/**
 * The main difference between Graph and DiGraph is their method addEdge(). In
 * Graph, addEdge(begin,end,edge) will add both Edge(begin,end,edge) and
 * Edge(end,begin,edge),while in DiGraph, only Edge(begin,end,edge) will be
 * added.
 * 
 * @author John Smith
 *
 * @param <V>
 * @param <E>
 */
public class DiGraph<V, E> extends Graph<V, E> {
	@Override
	public void addEdge(V begin, V end, E edge) {
		addDiEdge(begin, end, edge);
	}

}
