package lob;

import java.util.HashMap;

public class Simulation {
	
	public static void run() {
		System.out.println("Beginning simulation...\n");
		
		// create a quote (as a HashMap)
		HashMap<String, String> quote = new HashMap<String, String>();
		quote.put("timestamp", "1");
		quote.put("quantity", "10");
		quote.put("price", "17.4");
		quote.put("qId", "0");
		quote.put("tId", "100");
		
		// generate order from HashMap
		Order testOrder = new Order(quote);
		System.out.println(testOrder.toString());
		
		System.out.println("\nFinished simulation...");
	}
	
	public static void main(String[] args) {
		run();
	}
}
