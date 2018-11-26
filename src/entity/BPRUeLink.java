package entity;

import java.util.HashMap;

import exception.GraphException;

public class BPRUeLink  implements UserEquilibriumLink{
	protected float capacity;
	protected float fftt;
	protected float volume;
	protected float auxVolume;
	protected float travelTime;
	protected float surcharge;
	protected float lastSurcharge;

	public static BPRUeLink parseBPRUeLink(HashMap<String,String> map) throws GraphException{
		if(map.get("capacity")==null||map.get("fftt")==null){
			throw new GraphException("The target map (Graph<String,HashMap>) lacks attributes 'capacity' and 'fftt'");
		}else{
			float capacity = Float.parseFloat(map.get("capacity"));
			float fftt = Float.parseFloat(map.get("fftt"));
			return new BPRUeLink(capacity,fftt);
		}
	}
	
	public BPRUeLink(float capacity,float fftt){
		this.capacity = capacity;
		this.fftt = fftt;
		this.travelTime = 0;
		this.volume = 0;
		this.auxVolume = 0;
		this.surcharge = 0;
		this.lastSurcharge = 0;
		updateTravelTime();
	}
	

	public BPRUeLink(TNTPLink l) {
		this.capacity = l.getCapacity();
		this.fftt = l.getFtime();
		this.travelTime = 0;
		this.volume = 0;
		this.auxVolume = 0;
		this.surcharge = 0;
		this.lastSurcharge = 0;
		updateTravelTime();
	}

	public float getFftt(){
		return fftt;
	}

	public float getCapacity() {
		return capacity;
	}

	public float getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(float surcharge) {
		this.surcharge = surcharge;
	}

	@Override
	public void updateTravelTime() {
		this.travelTime = travelTimeFunction(volume);
	}

	@Override
	public float getWeight() {
		return getTravelTime();
	}

	@Override
	public void setWeight(float w) {
		this.travelTime = w;
	}

	@Override
	public float travelTimeFunction(double x) {
		float res = (float) (((Math.pow(this.volume / this.capacity, 4)) * 0.15 + 1) * this.fftt);
		res += this.surcharge;
		return res;
	}

	@Override
	public float travelTimeIntegrate(double x) {
		float C = (float) ((0.03 * fftt) / Math.pow(capacity, 4));
		return (float) (C * Math.pow(x, 5) + (fftt + surcharge) * x);
	}

	@Override
	public float getVolume() {
		return this.volume;
	}

	@Override
	public void setVolume(double volume) {
		this.volume = (float)volume;
	}

	@Override
	public float getAuxVolume() {
		return auxVolume;
	}

	@Override
	public void setAuxVolume(double auxVolume) {
		this.auxVolume = (float)auxVolume;
	}

	@Override
	public float getTravelTime() {
		return travelTime;
	}
}
