package traders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import lob.*;
/**
 * These agents are either buying or selling a large order of stock over the 
 * course of a day. Whether these agents are buying or selling is assigned with
 * equal probability. The initial volume h0 of a large order is drawn from a 
 * uniform distribution between h_min and h_max. To execute the large order, 
 * the fundamental agent looks at the current volume available at the opposite 
 * best price, Φt. If the remaining volume of his large order, ht, is less than 
 * Φt the agent sets this periods volume to vt = ht, otherwise he takes all 
 * available volume at the best price vt = Φt. For simplicity fundamental 
 * agents are assumed to only utilise market orders.
 * 
 *  MUST RUN UPDATE BEFORE SUBMITORDERS()!!!
 * 
 * @author Ash Booth
 *
 */
public class FundamentalTrader extends Trader {
	
	private Random generator = new Random();
	private boolean buying;
	
	private final int orderMin;
	private final int orderMax;
	
	private int orderSize;
	
	/**
	 * @param tId
	 * @param cash
	 * @param numAssets
	 */
	public FundamentalTrader(int tId, double cash, 
						     int numAssets, int orderMin, 
						     int orderMax) {
		super(tId, cash, numAssets);
		this.orderMin = orderMin;
		this.orderMax = orderMax;
		
		refreshOrder();
	}

	/* (non-Javadoc)
	 * @see traders.Trader#submitOrders(lob.OrderBook, int)
	 */
	@Override
	public ArrayList<Order> getOrders(OrderBook lob, int time) {
		ArrayList<Order> ordersToGo = new ArrayList<Order>();
		int volAtBest;
		String side;
		int orderQty;
		if (buying) {
			volAtBest = lob.getVolumeAtPrice("offer", lob.getBestOffer());
			orderQty = (orderSize > volAtBest) ? volAtBest : orderSize;
			side = "bid";
		} else {
			volAtBest = lob.getVolumeAtPrice("bid", lob.getBestBid());
			orderQty = (orderSize > volAtBest) ? volAtBest : orderSize;
			side = "offer";
		}
		ordersToGo.add(new Order(time, false, orderQty, tId, side));
		return ordersToGo;
	}

	/* (non-Javadoc)
	 * @see traders.Trader#update(lob.OrderBook, lob.Trade)
	 */
	@Override
	public void update(OrderBook lob) {
		// no parameters to update for fundamental trader
	}
	
	private void refreshOrder() {
		this.buying = generator.nextBoolean(); // randomly assign buy or sell
		this.orderSize = (orderMin + generator.nextInt(orderMax - orderMin+1));
	}

	@Override
	protected void iTraded(boolean bought, double price, int qty) {
		// TODO Auto-generated method stub
		this.orderSize -= qty;
		if (orderSize <=0) {
			refreshOrder();
		}
	}

}
