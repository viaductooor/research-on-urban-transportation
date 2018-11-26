package entity;

import graph.WeightedEdge;

public interface UserEquilibriumLink extends WeightedEdge {
    /**
     * Update travelTime.
     */
    public void updateTravelTime();

    /**
     * Get travelTime of the link. Notice that this function should directly return
     * the value rather than compute then return.
     * 
     * @return travelTime
     */
    public float getTravelTime();

    /**
     * A function of volume based on which we need to calculate travelTime.
     * 
     * @param x
     * @return travelTime
     */
    public float travelTimeFunction(double x);

    /**
     * Integrate of the travelTime function. It's samely a function of volume.
     * 
     * @param x
     * @return travelTime
     */
    public float travelTimeIntegrate(double x);

    /**
     * Get volume.
     * 
     * @return
     */
    public float getVolume();

    /**
     * Set volume.
     * 
     * @param volume
     */
    public void setVolume(double volume);

    /**
     * Get auxliary volume, which is necessary in the process of User Equilibrium
     * Algorithm.
     * 
     * @return auxliaryVolume
     */
    public float getAuxVolume();

    /**
     * Set auxliary volume, which is necessary in the process of User Equilibrium
     * Algorithm.
     * 
     * @param auxVolume
     */
    public void setAuxVolume(double auxVolume);
}