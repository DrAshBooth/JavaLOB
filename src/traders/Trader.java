
package traders;

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
	public HashMap<Integer, HashMap<String, String>> orders = 
								new HashMap<Integer, HashMap<String, String>>();
	protected Random generator = new Random();
	
	public Trader(int tId, double cash, int numAssets) {
		super();
		this.tId = tId;
		this.cash = cash;
		this.numAssets = numAssets;
	}
	
	public void addOrder(int qId, HashMap<String, String> order) {
		orders.put(qId, order);
	}
	
	public void delOrder(int qId) {
		orders.remove(qId);
	}
	
	public void bookkeep(Trade t) {
		if (this.tId == t.getProvider()) { // if my order was sat in the book
			int orderID = t.getOrderHit(); // Which order was affected 
			if (orders.containsKey(orderID)) {
				int originalQty = Integer.parseInt(orders.get(orderID).get("quantity"));
				if (originalQty < t.getQty()) { // whole order hit
					orders.remove(orderID);
				} else { // smaller order remains
					int newQty = originalQty - t.getQty();
					orders.get(orderID).put("quatity", Integer.toString(newQty));
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
	
	protected HashMap<String, String> oldestOrder() {
		int oldestID = -1;
		int oldestTime = Integer.MAX_VALUE;
		for (Map.Entry<Integer, HashMap<String, String>> entry : orders.entrySet()) {
			int quoteTime = Integer.parseInt(entry.getValue().get("timestamp"));
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
	
	public abstract ArrayList<HashMap<String, String>> getOrders(OrderBook lob, int time);
	
	/**
	 * Update the internal parameters of the trader given changes in the lob
	 * 
	 * @param lob	// the limit order book
	 * @param trade	// did a 
	 */
	public abstract void update(OrderBook lob, Trade trade);
	
	protected boolean noOrdersInBook() {
		return this.orders.isEmpty();
	}

	@Override
	public String toString() {
		return "Trader [tId=" + tId + ", cash=" + cash + ", numAssets="
				+ numAssets + ", blotter=" + blotter + ", orders=" + orders
				+ "]";
	}
	
}
