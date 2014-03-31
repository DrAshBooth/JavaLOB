package lob;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;

/*
 * TODO:
 * - Assign qid and update next qid in an addToBook method.
 * -
 */

public class OrderBook {
	ArrayList<String> tape = new ArrayList<String>();
	OrderTree bids = new OrderTree();
	OrderTree asks = new OrderTree();
	double tickSize;
	int time;
	int nextQuoteID = 0;
	
	public OrderBook(double tickSize) {
		this.tickSize = tickSize;
	}
	
	private double clipPrice(double price) {
		/*
		 * Clips price according to tickSize
		 */
		int numDecPlaces = (int)Math.log10(1 / this.tickSize);
		BigDecimal bd = new BigDecimal(price);
		BigDecimal rounded = bd.setScale(numDecPlaces, BigDecimal.ROUND_HALF_UP);
		return rounded.doubleValue();
	}
	
	public OrderReport processOrder(HashMap<String, String> quote, boolean verbose) {
		String orderType = quote.get("type");
		boolean orderInBook = false;
		
		// Update time
		this.time = Integer.parseInt(quote.get("time"));
		
		if (Integer.parseInt(quote.get("quantity")) <= 0 ) {
			System.out.println("processOrder() given qty <= 0");
			System.exit(0);
		}
		
		if (orderType == "market") {
			OrderReport oReport = processMarketOrder(quote);
		} else if (orderType == "market") {
			double clippedPrice = clipPrice(Double.parseDouble(quote.get("price")));
			quote.put("price", String.valueOf(clippedPrice));
			OrderReport oReport = processLimitOrder(quote);
		} else {
			System.out.println("processOrder() given neither 'market' nor 'limit'");
			System.exit(0);
		}
		return oReport;
	}
}
