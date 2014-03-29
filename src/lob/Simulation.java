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
		
		//Create another quote
		HashMap<String, String> quoteNew = new HashMap<String, String>();
		quoteNew.put("timestamp", "2");
		quoteNew.put("quantity", "15");
		quoteNew.put("price", "17.8");
		quoteNew.put("qId", "1");
		quoteNew.put("tId", "101");
		
		//Create another quote
		HashMap<String, String> quoteNewAg = new HashMap<String, String>();
		quoteNewAg.put("timestamp", "3");
		quoteNewAg.put("quantity", "15");
		quoteNewAg.put("price", "17.4");
		quoteNewAg.put("qId", "2");
		quoteNewAg.put("tId", "107");
		
		// Create an asks book
		OrderTree asks = new OrderTree();
		
		// insert
		System.out.println("Adding three orders...");
		asks.insertOrder(quote);
		asks.insertOrder(quoteNew);
		asks.insertOrder(quoteNewAg);
		
		printState(asks);
		
		// update
		System.out.println("Updating order...");
		quote.put("quantity", "123");
		quote.put("price", "17");
		asks.updateOrder(quote);
		
		printState(asks);
		
		System.out.println("\nFinished simulation...");
	}
	
	public static void printState(OrderTree book) {
		System.out.println("Max price = " + book.maxPrice());
		System.out.println("Min price = " + book.minPrice());
		System.out.println("Volume in book = " + book.getVolume());
		System.out.println("Depth of book = " + book.getDepth());
		System.out.println("Orders in book = " + book.getnOrders());
		System.out.println("Length of tree = " + book.length());
		System.out.println(book);
	}
	
	public static void main(String[] args) {
		run();
	}
}
