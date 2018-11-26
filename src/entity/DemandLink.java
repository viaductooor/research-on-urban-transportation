package entity;

import graph.WeightedEdge;

public class DemandLink implements WeightedEdge {
	private float demand;

	public DemandLink(float d) {
		this.demand = d;
	}

	@Override
	public float getWeight() {
		return demand;
	}

	@Override
	public void setWeight(float w) {
		this.demand = w;
	}

	/**
	 * @return the demand
	 */
	public float getDemand() {
		return demand;
	}

	/**
	 * @param demand the demand to set
	 */
	public void setDemand(float demand) {
		this.demand = demand;
	}

}
