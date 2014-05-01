
package traders;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lob.*;

/**
 * @author Ash Booth
 *
 */
public abstract class Trader {
	
	protected int tId;
	private double cash;
	private int numAssets;
	private ArrayList<Trade> blotter;
	// key: qId, value: order currently in the book
	public HashMap<Integer, Order> orders = new HashMap<Integer, Order>();
	protected Random generator = new Random();
	
	public Trader(int tId, double cash, int numAssets) {
		super();
		this.tId = tId;
		this.cash = cash;
		this.numAssets = numAssets;
	}
	
	public void addOrder(Order order) {
		int qId = order.getqId();
		orders.put(qId, order);
	}
	
	public void delOrder(int qId) {
		orders.remove(qId);
	}
	
	public void bookkeep(Trade t) {
		if (this.tId == t.getProvider()) { // if my order was sat in the book
			int orderID = t.getOrderHit(); // Which order was affected 
			if (orders.containsKey(orderID)) {
				int originalQty = orders.get(orderID).getQuantity();
				if (originalQty <= t.getQty()) { // whole order hit
					orders.remove(orderID);
				}
			} else {
				throw new IllegalStateException("Trader told his order was hit but he has no record of the order!");
			}
		}
		boolean bought;
		double price = t.getPrice();
		int qty = t.getQty();
		if (this.tId==t.getBuyer()) { // am i the buyer?
			bought = true;
			this.cash -= (qty*price);
			this.numAssets += qty;
		} else if (this.tId == t.getSeller()) { // am I the seller?
			bought = false;
			this.cash += (qty*price);
			this.numAssets -= qty;
		} else { // WTF?!?!
			bought = false;
			System.out.println("Trader has received a trade report " + 
							   "that he was not part of!!!");
			System.exit(0);
		}
		blotter.add(t);
		iTraded(bought, price, qty);
	}
	
	protected Order oldestOrder() {
		int oldestID = -1;
		int oldestTime = Integer.MAX_VALUE;
		for (Map.Entry<Integer, Order> entry : orders.entrySet()) {
			int quoteTime = entry.getValue().getTimestamp();
			if (quoteTime< oldestTime) {
				oldestTime = quoteTime;
				oldestID = entry.getKey();
			}
		}
		return orders.get(oldestID);
	}
	
	/**
	 * Called as part of bookkeep, some agents need to update specific internal
	 * Parameters in response to their trades executing.
	 * @param t
	 */
	protected abstract void iTraded(boolean bought, double price, int qty);
	
	public abstract ArrayList<Order> getOrders(OrderBook lob, int time);
	
	/**
	 * Update the internal parameters of the trader given changes in the lob
	 * 
	 * @param lob	// the limit order book
	 * @param trade	// did a 
	 */
	public abstract void update(OrderBook lob);
	
	protected boolean noOrdersInBook() {
		return this.orders.isEmpty();
	}

	@Override
	public String toString() {
		StringWriter fileStr = new StringWriter();
		fileStr.write(" -------- Trader " + tId + "--------\n");
		fileStr.write(" Cash = " + cash + "\n");
		fileStr.write(" Number of assets = " + numAssets + "\n");
		fileStr.write(" Blotter:\n" + blotter + "\n");
		fileStr.write(" Orders currently in book:\n" + orders);
		fileStr.write("\n --------------------------------\n");
		return fileStr.toString();
	}
	
}
