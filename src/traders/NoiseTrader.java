package traders;

import java.util.HashMap;

import lob.OrderBook;
import lob.Trade;

/**
 * The noise agents randomly decide on whether to buy or sell in each period 
 * with equal probability. 
 * 
 * Once decided, they randomly choose to place a market or limit order or to 
 * cancel an existing order. 
 * 
 * If a limit order is chosen they face 4 further options:
 *  - 
 * 
 * @author Ash Booth
 *
 */
public class NoiseTrader extends Trader {

	// Event probabilities
	double prob_market;
	double prob_limit;
	double prob_cancel;
	
	// Limit order action Probabilities
	double prob_cross;
	double prob_inside;
	double prob_at;
	double prob_deeper;
	
	// deep limit price power law params
	double xmin;
	double beta;

	public NoiseTrader(int tId, double cash, int numAssets, double prob_market,
			double prob_limit, double prob_cancel, double prob_cross,
			double prob_inside, double prob_at, double prob_deeper,
			double xmin, double beta) {
		super(tId, cash, numAssets);
		this.prob_market = prob_market;
		this.prob_limit = prob_limit;
		this.prob_cancel = prob_cancel;
		this.prob_cross = prob_cross;
		this.prob_inside = prob_inside;
		this.prob_at = prob_at;
		this.prob_deeper = prob_deeper;
		this.xmin = xmin;
		this.beta = beta;
	}

	@Override
	protected void iTraded(boolean bought, double price, int qty) {
		// TODO Auto-generated method stub

	}

	@Override
	public void submitOrders(OrderBook lob, int time) {
		// TODO Auto-generated method stub
		HashMap<String, String> quote = new HashMap<String, String>();
		String side = ((generator.nextBoolean()) ? "bid" : "offer");
		quote.put("side", side);
		double p = generator.nextDouble();
		if (p < prob_market) {
			// market order
			quote.put("type", "market");
			
		} else if ((p < (prob_market+prob_limit)) || noOrdersInBook()) {
			// limit order
			quote.put("type", "limit");
			double p2 = generator.nextDouble();
			if (p2 < prob_cross) {
				// Crossing limit order
				double oppositeBest = ((side == "bid") ? 
									   lob.getBestOffer() : lob.getBestBid());
				quote.put("price", Double.toString(oppositeBest));
			} else if (p2 < (prob_cross+prob_inside)) {
				// Limit order inside the spread (pennying)
				// TODO this should be U distributed
				double oneTick = lob.getTickSize();
				double quotePrice = ((side=="bid") ?
						lob.getBestBid()+oneTick : lob.getBestOffer()-oneTick);
				quote.put("price", Double.toString(quotePrice));
			} else if (p2 < (prob_cross+prob_inside+prob_at)) {
				// Limit order at best
				double bestPrice = ((side=="bid") ?
									lob.getBestBid() : lob.getBestOffer());
				quote.put("price", Double.toString(bestPrice));
			} else {
				// Limit order deep in book
				double deviate = xmin*Math.pow((1-generator.nextDouble()), (-1/(beta-1)));
				double quotePrice = ((side=="bid") ?
						lob.getBestBid()-deviate : lob.getBestOffer()+deviate);
				quote.put("price", Double.toString(quotePrice));
			}
			// TODO qty
			
		} else {
			// cancel order
			
		}
		
	}

	@Override
	public void update(OrderBook lob, Trade trade) {
		// TODO Auto-generated method stub
		

	}

}
