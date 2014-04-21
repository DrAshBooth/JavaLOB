package traders;

import lob.OrderBook;
import lob.Trade;

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
 * @author Ash Booth
 *
 */
public class FundamentalTrader extends Trader {

	/**
	 * @param tId
	 * @param cash
	 * @param numAssets
	 */
	public FundamentalTrader(int tId, double cash, int numAssets) {
		super(tId, cash, numAssets);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see traders.Trader#submitOrders(lob.OrderBook, int)
	 */
	@Override
	public void submitOrders(OrderBook lob, int time) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see traders.Trader#update(lob.OrderBook, lob.Trade)
	 */
	@Override
	public void update(OrderBook lob, Trade trade) {
		// TODO Auto-generated method stub

	}

}
