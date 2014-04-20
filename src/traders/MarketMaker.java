
package traders;

import java.util.HashMap;

import lob.OrderBook;
import lob.Trade;

/**
 * These agents simultaneously post an order on each side of the book, 
 * maintaining an approximately neutral position throughout the day. They make 
 * their income from the difference between their bids and offers. If one or 
 * both limit orders is executed, it will be replaced by a new one the next time 
 * the market maker is chosen to trade. Each round, the market maker updates 
 * a prediction for the sign of the next period’s order using a simple $w$
 * period rolling-mean estimate. When a market maker predicts that a buy order 
 * will arrive next, she will set her sell limit order volume to a uniformly 
 * distributed random number between $v_{min}$ and $v_{max}$ and her buy limit 
 * order volume to 1.
 * 
 * @author Ash Booth
 *
 */
public class MarketMaker extends Trader {

	private boolean bidInBook = false;
	private boolean offerInBook = false;
	
	public MarketMaker(int tId, double cash, int numAssets) {
		super(tId, cash, numAssets);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see traders.Trader#getOrder(int)
	 */
	@Override
	public HashMap<String, String> getOrder(int time) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see traders.Trader#update(lob.OrderBook, lob.Trade)
	 */
	@Override
	public void update(OrderBook lob, Trade trade) {
		// TODO Auto-generated method stub

	}

}
