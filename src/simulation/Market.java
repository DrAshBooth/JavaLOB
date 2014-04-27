package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import lob.*;
import traders.*;

public class Market {

	HashMap<Integer, Trader> tradersById = new HashMap<Integer, Trader>();
	HashMap<String, ArrayList<Trader>> tradersByType = 
								new HashMap<String, ArrayList<Trader>>();
	
	private final int n_NTs;
	private final int n_MMs;
	private final int n_FTs;
	
	private OrderBook lob;
	
	public Market(Properties prop) {
		super();
		this.n_NTs = Integer.valueOf(prop.getProperty("n_NTs"));
		this.n_MMs = Integer.valueOf(prop.getProperty("n_MMs"));
		this.n_FTs = Integer.valueOf(prop.getProperty("n_FTs"));
		
		lob = new OrderBook(Double.valueOf(prop.getProperty("tick_size")));
		
		populateMarket(Double.valueOf(prop.getProperty("starting_cash")),
					   Integer.valueOf(prop.getProperty("starting_assets")),
					   Double.valueOf(prop.getProperty("prob_market")),
					   Double.valueOf(prop.getProperty("prob_limit")),
					   Double.valueOf(prop.getProperty("prob_cancel")),
					   Double.valueOf(prop.getProperty("prob_cross")),
					   Double.valueOf(prop.getProperty("prob_inside")),
					   Double.valueOf(prop.getProperty("prob_at")),
					   Double.valueOf(prop.getProperty("prob_deeper")),
					   Double.valueOf(prop.getProperty("xmin")),
					   Double.valueOf(prop.getProperty("beta")),
					   Double.valueOf(prop.getProperty("market_mu")),
					   Double.valueOf(prop.getProperty("market_sigma")),
					   Double.valueOf(prop.getProperty("limit_mu")),
					   Double.valueOf(prop.getProperty("limit_sigma")),
					   Double.valueOf(prop.getProperty("default_spread")),
					   Double.valueOf(prop.getProperty("default_price")),
					   Integer.valueOf(prop.getProperty("rollMeanLen")),
					   Integer.valueOf(prop.getProperty("vMin")),
					   Integer.valueOf(prop.getProperty("vMax")),
					   Integer.valueOf(prop.getProperty("vMinus")),
					   Integer.valueOf(prop.getProperty("orderMin")),
					   Integer.valueOf(prop.getProperty("orderMax")));
	}

	public void populateMarket(double cash, int numAssets, double prob_market,
			double prob_limit, double prob_cancel, double prob_cross,
			double prob_inside, double prob_at, double prob_deeper,
			double xmin, double beta, double market_mu, double market_sigma,
			double limit_mu, double limit_sigma, double default_spread,
			double default_price, int rollMeanLen, int vMin, int vMax, int vMinus,
			int orderMin, int orderMax) {
		int tId = 0;
		ArrayList<Trader> noiseTraders = new ArrayList<Trader>();
		ArrayList<Trader> marketMakers = new ArrayList<Trader>();
		ArrayList<Trader> fundamentalTraders = new ArrayList<Trader>();
		for (int i=0;i<n_NTs;i++) {
			NoiseTrader nt = new NoiseTrader(tId, cash, numAssets, prob_market,
					 prob_limit,  prob_cancel,  prob_cross,
					 prob_inside,  prob_at,  prob_deeper,
					 xmin,  beta,  market_mu,  market_sigma,
					 limit_mu,  limit_sigma,  default_spread,
					 default_price);
			noiseTraders.add(nt);
			tradersById.put(tId, nt);
			tId+=1;
		}
		for (int i=0;i<n_MMs;i++) {
			MarketMaker mm = new MarketMaker(tId, cash, numAssets, 
											  rollMeanLen, vMin, vMax, vMinus);
			marketMakers.add(mm);
			tradersById.put(tId, mm);
			tId+=1;
		}
		for (int i=0;i<n_FTs;i++) {
			FundamentalTrader ft = new FundamentalTrader(tId, cash, numAssets,
															orderMin, orderMax);
			fundamentalTraders.add(ft);
			tradersById.put(tId, ft);
			tId+=1;
		}
		tradersByType.put("NT", noiseTraders);
		tradersByType.put("MM", marketMakers);
		tradersByType.put("FT", fundamentalTraders);
	}

}
