
package traders;

import java.util.ArrayList;
import java.util.HashMap;

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
	public HashMap<Integer, HashMap<String, String>> orders = new HashMap<Integer, HashMap<String, String>>();
	
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
				System.out.println("Trader told his order was hit but he has no record of the order!");
				System.exit(0);
			}
		}
		double price = t.getPrice();
		int qty = t.getQty();
		if (this.tId==t.getBuyer()) { // am i the buyer?
			this.cash -= (qty*price);
			this.numAssets += qty;
		} else if (this.tId == t.getSeller()) { // am I the seller?
			this.cash += (qty*price);
			this.numAssets -= qty;
		} else { // WTF?!?!
			System.out.println("Trader has received a trade report " + 
							   "that he was not part of!!!");
			System.exit(0);
		}
		blotter.add(t);
	}
	
	public abstract void submitOrders(OrderBook lob, int time);
	
	public abstract void update(OrderBook lob, Trade trade);

	@Override
	public String toString() {
		return "Trader [tId=" + tId + ", cash=" + cash + ", numAssets="
				+ numAssets + ", blotter=" + blotter + ", orders=" + orders
				+ "]";
	}
	
}
