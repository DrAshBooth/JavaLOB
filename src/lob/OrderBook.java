package lob;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigDecimal;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

/*
 * TODO:
 * - Assign qid and update next qid in an addToBook method.
 * -
 */

public class OrderBook {
	private ArrayList<Trade> tape = new ArrayList<Trade>();
	private OrderTree bids = new OrderTree();
	private OrderTree asks = new OrderTree();
	private double tickSize;
	private int time;
	private int nextQuoteID = 0;
	
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
		OrderReport oReport = null;
		// Update time
		this.time = Integer.parseInt(quote.get("timestamp"));
		
		if (Integer.parseInt(quote.get("quantity")) <= 0 ) {
			System.out.println("processOrder() given qty <= 0");
			System.exit(0);
		}
		
		if (orderType == "market") {
			oReport = processMarketOrder(quote, verbose);
		} else if (orderType == "limit") {
			double clippedPrice = clipPrice(Double.parseDouble(quote.get("price")));
			quote.put("price", String.valueOf(clippedPrice));
			oReport = processLimitOrder(quote, verbose);
		} else {
			System.out.println("processOrder() given neither 'market' nor 'limit'");
			System.exit(0);
		}
		return oReport;
	}
	
	
	private OrderReport processMarketOrder(HashMap<String, String> quote, 
										  boolean verbose) {
		ArrayList<Trade> trades = new ArrayList<Trade>();
		String side = quote.get("side");
		int qtyRemaining = Integer.parseInt(quote.get("quantity"));
		if (side =="bid") {
			while ((qtyRemaining > 0) && (this.asks.getnOrders() > 0)) {
				OrderList ordersAtBest = this.asks.minPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
												quote, verbose);
			}
		}else if(side=="offer") {
			while ((qtyRemaining > 0) && (this.bids.getnOrders() > 0)) {
				OrderList ordersAtBest = this.bids.maxPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
												quote, verbose);
			}
		}else {
			System.out.println("processMarketOrder() given neither bid nor offer");
			System.exit(0);
		}
		OrderReport report = new OrderReport(trades, false);
		return  report;
	}
	
	
	private OrderReport processLimitOrder(HashMap<String, String> quote, 
										  boolean verbose) {
		boolean orderInBook = false;
		ArrayList<Trade> trades = new ArrayList<Trade>();
		String side = quote.get("side");
		int qtyRemaining = Integer.parseInt(quote.get("quantity"));
		double price = Double.parseDouble(quote.get("price"));
		if (side=="bid") {
			while ((this.asks.getnOrders() > 0) && 
					(qtyRemaining > 0) && 
					(price > asks.minPrice())) {
				OrderList ordersAtBest = asks.minPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
												quote, verbose);
			}
			// If volume remains, add order to book
			if (qtyRemaining > 0) {
				quote.put("qId", Integer.toString(this.nextQuoteID));
				quote.put("quantity", Integer.toString(qtyRemaining));
				this.bids.insertOrder(quote);
				orderInBook = true;
				this.nextQuoteID+=1;
			} else {
				orderInBook = false;
			}
		} else if (side=="offer") {
			while ((this.bids.getnOrders() > 0) && 
					(qtyRemaining > 0) && 
					(price < bids.maxPrice())) {
				OrderList ordersAtBest = bids.maxPriceList();
				qtyRemaining = processOrderList(trades, ordersAtBest, qtyRemaining,
												quote, verbose);
			}
			// If volume remains, add to book
			if (qtyRemaining > 0) {
				quote.put("qId", Integer.toString(this.nextQuoteID));
				quote.put("quantity", Integer.toString(qtyRemaining));
				this.asks.insertOrder(quote);
				orderInBook = true;
				this.nextQuoteID+=1;
			} else {
				orderInBook = false;
			}
		} else {
			System.out.println("processLimitOrder() given neither bid nor offer");
			System.exit(0);
		}
		OrderReport report = new OrderReport(trades, orderInBook);
		if (orderInBook) {
			report.setOrder(quote);
		}
		return report;
	}
	
	
	private int processOrderList(ArrayList<Trade> trades, OrderList orders,
								int qtyRemaining, HashMap<String, String> quote,
								boolean verbose) {
		String side = quote.get("side");
		int buyer, seller;
		int takerId = Integer.parseInt(quote.get("tId"));
		int time = Integer.parseInt(quote.get("timestamp"));
		while ((orders.getLength()>0) && (qtyRemaining>0)) {
			int qtyTraded = 0;
			Order headOrder = orders.getHeadOrder();
			if (qtyRemaining < headOrder.getQuantity()) {
				qtyTraded = qtyRemaining;
				if (side=="offer") {
					this.bids.updateOrderQty(headOrder.getQuantity()-qtyRemaining, 
											 headOrder.getqId());
				} else {
					this.asks.updateOrderQty(headOrder.getQuantity()-qtyRemaining, 
											 headOrder.getqId());
				}
				qtyRemaining = 0;
			} else {
				qtyTraded = headOrder.getQuantity();
				if (side=="offer") {
					this.bids.removeOrderByID(headOrder.getqId());
				} else {
					this.asks.removeOrderByID(headOrder.getqId());
				}
				qtyRemaining -= qtyTraded;
			}
			if (side=="offer") {
				buyer = headOrder.gettId();
				seller = takerId;
			} else {
				buyer = takerId;
				seller = headOrder.gettId();
			}
			Trade trade = new Trade(time, headOrder.getPrice(), qtyTraded, 
									headOrder.gettId(),takerId, buyer, seller);
			trades.add(trade);
			this.tape.add(trade);
			if (verbose) {
				System.out.println(trade);
			}
		}
		return qtyRemaining;
	}
	
	
	public void cancelOrder(String side, int qId) {
		if (side=="bid") {
			if (bids.orderExists(qId)) {
				bids.removeOrderByID(qId);
			}
		} else if (side=="offer") {
			if (asks.orderExists(qId)) {
				asks.removeOrderByID(qId);
			}
		} else {
			System.out.println("cancelOrder() given neither 'bid' nor 'offer'");
			System.exit(0);
		}
	}
	
	
	public void modifyOrder(int qId, HashMap<String, String> quote) {
		// TODO
		// Remember if price is changed must check for clearing.
	}
	
	
	public int getVolumeAtPrice(String side, double price) {
		price = clipPrice(price);
		int vol = 0;
		if(side=="bid") {
			if (bids.priceExists(price)) {
				vol = bids.getPriceList(price).getVolume();
			}
		} else if (side=="offer") {
			if (asks.priceExists(price)) {
				vol = asks.getPriceList(price).getVolume();
			}
		} else {
			System.out.println("modifyOrder() given neither 'bid' nor 'offer'");
			System.exit(0);
		}
		return vol;
		
	}
	
	public double getBestBid() {
		return bids.maxPrice();
	}
	
	public double getWorstBid() {
		return bids.minPrice();
	}
	
	public double getBestOffer() {
		return asks.minPrice();
	}
	
	public double getWorstOffer() {
		return asks.maxPrice();
	}
	
	
	public void dumpTape(String fName, String tMode) {
		try {
			File dumpFile = new File(fName);
			BufferedWriter output = new BufferedWriter(new FileWriter(dumpFile));
			for (Trade t : tape) {
				output.write(t.toString());
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String toString() {
		StringWriter fileStr = new StringWriter();
		fileStr.write(" -------- The Order Book --------\n");
		fileStr.write("|                                |\n");
		fileStr.write("|   ------- Bid  Book --------   |\n");
		if (bids.getnOrders() > 0) {
			fileStr.write(bids.toString());
		}
		fileStr.write("|   ------ Offer  Book -------   |\n");
		if (asks.getnOrders() > 0) {
			fileStr.write(asks.toString());
		}
		fileStr.write("|   -------- Trades  ---------   |");
		if (!tape.isEmpty()) {
			for (Trade t : tape) {
				fileStr.write(t.toString());
			}
		}
		fileStr.write("\n --------------------------------\n");
		return fileStr.toString();
	}
}
