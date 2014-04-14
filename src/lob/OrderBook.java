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
	
	public OrderReport processMarketOrder(HashMap<String, String> quote, boolean verbose) {
		ArrayList<Trade> trades = new ArrayList<Trade>();
		String side = quote.get("side");
		int qtyRemaining = Integer.parseInt(quote.get("quantity"));
		if (side =="bid") {
			while ((qtyRemaining>0) && (this.asks.getnOrders()>0)) {
				OrderList ordersAtBest = this.asks.minPriceList();
				qtyRemaining = processOrderList()
			}
		}else if(side=="offer") {
			
		}else {
			System.out.println("processMarketOrder() given neither bid nor offer");
			System.exit(0);
		}
	}
	
	public int processOrderList(ArrayList<Trade> trades, OrderList orders,
								int qtyRemaining, HashMap<String, String> quote,
								boolean verbose) {
		String side = quote.get("side");
		int buyer, seller;
		int takerId = Integer.parseInt(quote.get("tid"));
		int time = Integer.parseInt(quote.get("timestamp"));
		while ((orders.getLength()>0) && (qtyRemaining>0)) {
			int qtyTraded = 0;
			Order headOrder = orders.getHeadOrder();
			if (qtyRemaining < headOrder.getQuantity()) {
				qtyTraded = qtyRemaining;
				headOrder.updateQty(headOrder.getQuantity()-qtyRemaining, 
									headOrder.getTimestamp());
				qtyRemaining = 0;
			} else {
				qtyTraded = headOrder.getQuantity();
				if (side=="offer") {
					this.asks.removeOrderByID(headOrder.getqId());
				} else {
					this.bids.removeOrderByID(headOrder.getqId());
				}
				qtyRemaining -= qtyTraded;
			}
			if (side=="ask") {
				buyer = headOrder.gettId();
				seller = takerId;
			} else {
				buyer = takerId;
				seller = headOrder.gettId();
			}
			Trade trade = new Trade(time, headOrder.getPrice(), qtyTraded, 
									headOrder.gettId(),takerId, buyer, seller);
			trades.add(trade);
			if (verbose) {
				System.out.println(trade);
			}
		}
		return qtyRemaining;
	}
	
}
