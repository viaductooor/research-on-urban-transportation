package functions;

public class BasicFunctions {
	public static float BPR(float capacity,float volume,float fftt) {
		return (float) (((Math.pow(volume / capacity, 4)) * 0.15 + 1) * fftt);
	}
	
	public static float marginalCost(float volume, float fftt, float cap) {
		float result = (float) (volume * fftt * (4 * Math.pow(volume, 3) * 0.15 / Math.pow(cap, 4)));
		return result;
	}
}
