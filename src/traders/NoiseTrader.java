package traders;

import java.util.ArrayList;
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
 *  - Cross the spread.
 *  - Improve the best
 *  - At the best
 *  - Deep in the book
 *  
 * The noise traders are not strictly zero-intelligent in the implementation,
 * since this can lead to undesirable price processes. For example, market 
 * orders are limited in volume such that they cannot consume more than half of 
 * the total opposing side's available volume. Another restriction is that 
 * noise traders will make sure that no side of the order book is empty and 
 * place limit orders appropriately.
 * 
 * @author Ash Booth
 *
 */
public class NoiseTrader extends Trader {

	// Event probabilities
	private final double prob_market;
	private final double prob_limit;
	private final double prob_cancel;
	
	// Limit order action Probabilities
	private final double prob_cross;
	private final double prob_inside;
	private final double prob_at;
	private final double prob_deeper;
	
	// deep limit price power-law params
	private final double xmin;
	private final double beta;
	
	// Order size params (log-normal distribution)
	private final double market_mu;
	private final double market_sigma;
	private final double limit_mu;
	private final double limit_sigma;
	
	private final double default_spread;
	private final double default_price;

	public NoiseTrader(int tId, double cash, int numAssets, double prob_market,
			double prob_limit, double prob_cancel, double prob_cross,
			double prob_inside, double prob_at, double prob_deeper,
			double xmin, double beta, double market_mu, double market_sigma,
			double limit_mu, double limit_sigma, double default_spread,
			double default_price) {
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
		this.market_mu = market_mu;
		this.market_sigma = market_sigma;
		this.limit_mu = limit_mu;
		this.limit_sigma = limit_sigma;
		this.default_spread = default_spread;
		this.default_price = default_price;
	}

	@Override
	protected void iTraded(boolean bought, double price, int qty) {

	}

	@Override
	public ArrayList<HashMap<String, String>> getOrders(OrderBook lob, int time) {
		ArrayList<HashMap<String, String>> ordersToGo = 
				new ArrayList<HashMap<String, String>>();
		if ((lob.volumeOnSide("bid")==0) || (lob.volumeOnSide("offer")==0) ) {
			populateBook(lob, ordersToGo, time);
		} else {
			// Usual NT logic
			HashMap<String, String> quote = new HashMap<String, String>();
			quote.put("timestamp", Integer.toString(time));
			quote.put("tId", Integer.toString(this.tId));
			String side = ((generator.nextBoolean()) ? "bid" : "offer");
			quote.put("side", side);
			double p = generator.nextDouble();
			if (p < prob_market) {
				// market order
				int vol;
				quote.put("type", "market");
				vol = (int)Math.round(Math.exp(market_mu+
										(market_sigma*generator.nextDouble())));
				// Order does not consume more than half opposing vol
				String opposingSide = (side=="bid") ? "offer" : "bid";
				int halfOppVol = lob.volumeOnSide(opposingSide)/2;
				vol = ( (vol < halfOppVol) ? vol : halfOppVol );
				quote.put("quantity", Integer.toString(vol));
				ordersToGo.add(quote);
			} else if ((p < (prob_market+prob_limit)) || noOrdersInBook()) {
				// limit order
				limitOrder(lob,quote);
				ordersToGo.add(quote);
			} else {
				// cancel order
				HashMap<String, String> oldestOrder = oldestOrder();
				lob.cancelOrder(oldestOrder.get("side"), 
								Integer.parseInt(oldestOrder.get("qId")));
				this.orders.remove(oldestOrder.get("qId"));
			}
		}
		return ordersToGo;
	}
	
	private void populateBook(OrderBook lob, 
							  ArrayList<HashMap<String, String>> ordersToGo,
							  int time) {
		HashMap<String, String> bid = new HashMap<String, String>();
		bid.put("timestamp", Integer.toString(time));
		bid.put("tId", Integer.toString(this.tId));
		HashMap<String, String> offer = new HashMap<String, String>();
		offer.put("timestamp", Integer.toString(time));
		offer.put("tId", Integer.toString(this.tId));
		if ((lob.volumeOnSide("bid")==0) && (lob.volumeOnSide("offer")==0)) {
			// bid
			bid.put("side", "bid");
			bid.put("price", Double.toString(default_price));
			int vol = (int)Math.round(Math.exp(limit_mu+
										(limit_sigma*generator.nextDouble())));
			bid.put("quantity", Integer.toString(vol));
			// offer
			offer.put("side", "offer");
			offer.put("price", Double.toString(default_price+default_spread));
			int vol2 = (int)Math.round(Math.exp(limit_mu+
										(limit_sigma*generator.nextDouble())));
			offer.put("quantity", Integer.toString(vol2));
			ordersToGo.add(bid);
			ordersToGo.add(offer);
		} else if (lob.volumeOnSide("bid")==0) {
			// submit a bid
			bid.put("side", "bid");
			bid.put("price", Double.toString(lob.getBestOffer()-default_spread));
			int vol = (int)Math.round(Math.exp(limit_mu+
										(limit_sigma*generator.nextDouble())));
			bid.put("quantity", Integer.toString(vol));
			ordersToGo.add(bid);
		} else if (lob.volumeOnSide("offer")==0){
			// submit an ask
			offer.put("side", "offer");
			offer.put("price", Double.toString(lob.getBestBid()+default_spread));
			int vol = (int)Math.round(Math.exp(limit_mu+
										(limit_sigma*generator.nextDouble())));
			offer.put("quantity", Integer.toString(vol));
			ordersToGo.add(offer);
		} 
	}
	
	private void limitOrder(OrderBook lob, HashMap<String, String> quote) {
		int vol;
		String side = quote.get("side");
		double bestBid = lob.getBestBid();
		double bestOffer = lob.getBestOffer();
		quote.put("type", "limit");
		double p2 = generator.nextDouble();
		if (p2 < prob_cross) {
			// Crossing limit order
			double oppositeBest = ((side == "bid") ? 
								   bestOffer : bestBid);
			quote.put("price", Double.toString(oppositeBest));
		} else if (p2 < (prob_cross+prob_inside)) {
			// Limit order inside the spread (U distributed)
			double increment = generator.nextDouble()*(bestOffer-bestBid);
			quote.put("price", Double.toString(bestBid+increment));
		} else if (p2 < (prob_cross+prob_inside+prob_at)) {
			// Limit order at best
			double bestPrice = ((side=="bid") ?
								bestBid : bestOffer);
			quote.put("price", Double.toString(bestPrice));
		} else {
			// Limit order deep in book
			double deviate = xmin*Math.pow( (1-generator.nextDouble()), 
										   (-1/(beta-1)) );
			double quotePrice = ((side=="bid") ?
					bestBid-deviate : bestOffer+deviate);
			quote.put("price", Double.toString(quotePrice));
		}
		vol = (int)Math.round(Math.exp(limit_mu+
										(limit_sigma*generator.nextDouble())));
		quote.put("quantity", Integer.toString(vol));
	}

	@Override
	public void update(OrderBook lob, Trade trade) {
		
	}

}
