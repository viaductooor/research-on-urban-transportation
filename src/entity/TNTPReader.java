package entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import graph.Graph;

/**
 * This class is aimed to read graphs from files of type .tntp, which is a basic
 * formmat of the files on https://github.com/bstabler/TransportationNetworks
 */
public class TNTPReader {

	public static String ANAHEIM_TRIP = "files/Anaheim_trips.tntp";
	public static String ANAHEIM_NET = "files/Anaheim_net.tntp";
	public static String CHICAGO_TRIP = "files/ChicagoRegional_trips.tntp";
	public static String CHICAGO_NET = "files/ChicagoRegional_net.tntp";
	public static String SIOUXFALLS_TRIP = "files/SiouxFalls_trips.tntp";
	public static String SIOUXFALLS_NET = "files/SiouxFalls_net.tntp";
	public static String WINNIPEG_ASYM_TRIP = "files/Winnipeg-Asym_trips.tntp";
	public static String WINNIPEG_ASYM_NET = "files/Winnipeg-Asym_net.tntp";

	/**
	 * Read net data from File(url).
	 * 
	 * @param url
	 * @return
	 */
	public static Graph<String, TNTPLink> readGraph(String url) {
		Graph<String, TNTPLink> graph = new Graph<String, TNTPLink>();
		File netFile = new File(url);
		FileInputStream fis = null;
		BufferedReader reader = null;
		String line = "";

		// read from net file
		try {
			fis = new FileInputStream(netFile);
			reader = new BufferedReader(new InputStreamReader(fis));
			boolean isLink = false;

			while ((line = reader.readLine()) != null & line != "") {
				if (isLink == true) {
					line = " " + line;
					String[] items = line.split("\\s+");
					String from = items[1];
					String to = items[2];
					float capacity = Float.parseFloat(items[3]);
					float length = Float.parseFloat(items[4]);
					float ftime = Float.parseFloat(items[5]);
					float B = Float.parseFloat(items[6]);
					float power = Float.parseFloat(items[7]);
					float speed = Float.parseFloat(items[8]);
					float toll = Float.parseFloat(items[9]);
					int type = Integer.parseInt(items[10].substring(0, 1));
					TNTPLink link = new TNTPLink(capacity, length, ftime, B, power, speed, toll, type);
					graph.addDiEdge(from, to, link);
				}
				if (line.contains("~")) {
					isLink = true;
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			// last line was counted in, which is not supposed
		} catch (java.lang.NumberFormatException e) {
			e.printStackTrace();
		}
		return graph;
	}

	/**
	 * Read trip(demand) data from File(url).
	 * 
	 * @param url
	 * @return
	 */
	public static Graph<String, DemandLink> readTrips(String url) {
		Graph<String, DemandLink> trips = new Graph<String, DemandLink>();
		File tripFile = new File(url);
		FileInputStream fis = null;
		BufferedReader reader = null;
		String line = "";
		Matcher m = null;
		try {
			fis = new FileInputStream(tripFile);
			reader = new BufferedReader(new InputStreamReader(fis));
			boolean isTrip = false;
			String origin = "null";
			String destination;
			float demand;
			while ((line = reader.readLine()) != null) {
				if (line.contains("Origin")) {
					isTrip = true;
					Pattern pOrigin = Pattern.compile("Origin\\s+(\\d+)");
					if ((m = pOrigin.matcher(line)).find()) {
						origin = m.group(1);
					}
				} else if (isTrip) {
					Pattern pItem = Pattern.compile("\\s*(\\d+)\\s*:\\s+(\\S+);");
					m = pItem.matcher(line);
					while (m.find()) {
						destination = m.group(1);
						demand = Float.parseFloat(m.group(2));
						trips.addEdge(origin, destination, new DemandLink(demand));
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return trips;
	}
}
