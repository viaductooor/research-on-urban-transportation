package graph;

/**
 * If you want to perform some shortest path like algorithms on a graph, the
 * links of it must implement this interface.
 *
 * @author John Smith
 *
 */
public interface WeightedEdge {
	public float getWeight();

	public void setWeight(float w);
}
