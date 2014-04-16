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
	ArrayList<Trade> tape = new ArrayList<Trade>();
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
		OrderReport oReport = null;
		// Update time
		this.time = Integer.parseInt(quote.get("time"));
		
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
	
	
	public OrderReport processMarketOrder(HashMap<String, String> quote, 
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
	
	
	public OrderReport processLimitOrder(HashMap<String, String> quote, 
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
				quote.put("qid", Integer.toString(this.nextQuoteID));
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
				quote.put("qid", Integer.toString(this.nextQuoteID));
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
			this.tape.add(trade);
			if (verbose) {
				System.out.println(trade);
			}
		}
		return qtyRemaining;
	}
	
	
	public void cancelOrder(String side, int qid) {
		if (side=="bid") {
			if (bids.orderExists(qid)) {
				bids.removeOrderByID(qid);
			}
		} else if (side=="offer") {
			if (asks.orderExists(qid)) {
				asks.removeOrderByID(qid);
			}
		} else {
			System.out.println("cancelOrder() given neither 'bid' nor 'offer'");
			System.exit(0);
		}
	}
	
	
	public void modifyOrder(int qid, HashMap<String, String> quote) {
		// TODO
		// Remember if price is change must check for clearing.
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
		fileStr.write("------ Bids -------\n");
		if (bids.getnOrders() > 0) {
			fileStr.write(bids.toString());
		}
		fileStr.write("\n------ Offers -------\n");
		if (asks.getnOrders() > 0) {
			fileStr.write(asks.toString());
		}
		fileStr.write("\n------ Trades -------\n");
		if (!tape.isEmpty()) {
			for (Trade t : tape) {
				fileStr.write(t.toString());
			}
		}
		fileStr.write("\n");
		return fileStr.toString();
	}
}
