package lob;

import java.util.HashMap;

public class Simulation {
	
	public static void run() {
		System.out.println("Beginning simulation...\n");
		
		// create quotes
		HashMap<String, String> quote = new HashMap<String, String>();
		quote.put("timestamp", "1");
		quote.put("type", "limit");
		quote.put("side", "offer");
		quote.put("quantity", "2");
		quote.put("price", "17.4");
		quote.put("tId", "100");
		
		HashMap<String, String> quote1 = new HashMap<String, String>();
		quote1.put("timestamp", "2");
		quote1.put("type", "limit");
		quote1.put("side", "offer");
		quote1.put("quantity", "10");
		quote1.put("price", "17.4");
		quote1.put("tId", "101");
		
		HashMap<String, String> quote2 = new HashMap<String, String>();
		quote2.put("timestamp", "3");
		quote2.put("type", "limit");
		quote2.put("side", "offer");
		quote2.put("quantity", "15");
		quote2.put("price", "17.6");
		quote2.put("tId", "102");
		
		HashMap<String, String> quote3 = new HashMap<String, String>();
		quote3.put("timestamp", "4");
		quote3.put("type", "limit");
		quote3.put("side", "bid");
		quote3.put("quantity", "5");
		quote3.put("price", "16.899999");
		quote3.put("tId", "103");
		
		HashMap<String, String> quote4 = new HashMap<String, String>();
		quote4.put("timestamp", "5");
		quote4.put("type", "limit");
		quote4.put("side", "bid");
		quote4.put("quantity", "5");
		quote4.put("price", "16.9");
		quote4.put("tId", "104");
		
		// instantiate order book
		OrderBook lob = new OrderBook(0.01);
		
		// View empty book
		print("\n...empty book...\n");
		print(lob.toString());
		
		// Add non-crossing orders
		print("\n...adding limit orders...\n");
		lob.processOrder(quote, false);
		lob.processOrder(quote1, false);
		lob.processOrder(quote2, false);
		lob.processOrder(quote3, false);
		lob.processOrder(quote4, false);
				
		// View the book
		print("\n...populated book...\n");
		print(lob.toString());
		
		// Market order
		print("\n...submitting market order...\n");
		HashMap<String, String> quote5 = new HashMap<String, String>();
		quote5.put("timestamp", "6");
		quote5.put("type", "market");
		quote5.put("side", "bid");
		quote5.put("quantity", "1");
		quote5.put("tId", "105");
		
		lob.processOrder(quote5, false);
		
		// View the book
		print("\n...book after MO...\n");
		print(lob.toString());
		
		
		// Crossing limit order
		print("\n...submitting limit order that crosses the spread...\n");
		HashMap<String, String> quote6 = new HashMap<String, String>();
		quote6.put("timestamp", "7");
		quote6.put("type", "limit");
		quote6.put("side", "bid");
		quote6.put("price", "100000");
		quote6.put("quantity", "3");
		quote6.put("tId", "106");
		
		lob.processOrder(quote6, false);
		
		// View the book
		print("\n...book after crossing limit order...\n");
		print(lob.toString());

		System.out.println("\nFinished simulation...");
	}
	
	public static void print(String string) {
		System.out.println(string);
	}
	
	public static void main(String[] args) {
		run();
	}
}
