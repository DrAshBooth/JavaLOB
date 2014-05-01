package simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import lob.*;
import traders.*;

public class Market {

	private HashMap<Integer, Trader> tradersById = new HashMap<Integer, Trader>();
	private HashMap<String, ArrayList<Trader>> tradersByType = 
										new HashMap<String, ArrayList<Trader>>();
	private List<Integer> tIds;
	private final int n_NTs;
	private final int n_MMs;
	private final int n_FTs;
	
	private OrderBook lob;
	private List<Order> quoteCollector = new ArrayList<Order>();
	
	private Random generator = new Random();
	
	private String dataDir;
	
	public Market(Properties prop, String dataDir) {
		super();
		this.dataDir = dataDir;
		this.n_NTs = Integer.valueOf(prop.getProperty("n_NTs"));
		this.n_MMs = Integer.valueOf(prop.getProperty("n_MMs"));
		this.n_FTs = Integer.valueOf(prop.getProperty("n_FTs"));
		
		lob = new OrderBook(Double.valueOf(prop.getProperty("tick_size")));
		
		populateMarket(Double.valueOf(prop.getProperty("starting_cash")),
					   Integer.valueOf(prop.getProperty("starting_assets")),
					   Double.valueOf(prop.getProperty("NT_prob_market")),
					   Double.valueOf(prop.getProperty("NT_prob_limit")),
					   Double.valueOf(prop.getProperty("NT_prob_cancel")),
					   Double.valueOf(prop.getProperty("NT_prob_cross")),
					   Double.valueOf(prop.getProperty("NT_prob_inside")),
					   Double.valueOf(prop.getProperty("NT_prob_at")),
					   Double.valueOf(prop.getProperty("NT_prob_deeper")),
					   Double.valueOf(prop.getProperty("NT_xmin")),
					   Double.valueOf(prop.getProperty("NT_beta")),
					   Double.valueOf(prop.getProperty("NT_market_mu")),
					   Double.valueOf(prop.getProperty("NT_market_sigma")),
					   Double.valueOf(prop.getProperty("NT_limit_mu")),
					   Double.valueOf(prop.getProperty("NT_limit_sigma")),
					   Double.valueOf(prop.getProperty("default_spread")),
					   Double.valueOf(prop.getProperty("default_price")),
					   Integer.valueOf(prop.getProperty("MM_rollMeanLen")),
					   Integer.valueOf(prop.getProperty("MM_vMin")),
					   Integer.valueOf(prop.getProperty("MM_vMax")),
					   Integer.valueOf(prop.getProperty("MM_vMinus")),
					   Integer.valueOf(prop.getProperty("FT_orderMin")),
					   Integer.valueOf(prop.getProperty("FT_orderMax")));
	}
	
	public void run(int timesteps, boolean verbose) {
		for (int time = 1; time <= timesteps; time++) {
			Trader tdr;
			// if either book empty, pick NT to trade
			if ((lob.volumeOnSide("bid")==0) || (lob.volumeOnSide("offer")==0)) {
				int ntIdx = generator.nextInt(tradersByType.get("NT").size());
				tdr = tradersByType.get("NT").get(ntIdx);
				submitOrder(tdr,time); // deals with clearing and bookkeeping
			} else {
				// pick random trader
				int randTId = tIds.get(generator.nextInt(tIds.size()));
				tdr = tradersById.get(randTId);
				submitOrder(tdr,time);
			}
			//update all traders
			for (int tId : tIds) {
				tradersById.get(tId).update(lob);
			}
			if (verbose) {
				System.out.println(this.toString());
			}
		}
	}
	
	private void submitOrder(Trader tdr, int time) {
		ArrayList<Order> quotes;
		quotes = tdr.getOrders(lob, time);
		for (Order q : quotes) {
			// add sign of order to orderSigns list
			quoteCollector.add(q);
			OrderReport orderRep = lob.processOrder(q, false);
			clearing(tdr,orderRep);
		}
	}
	
	/**
	 * If an order didn't clear or was partially cleared and was put in the 
	 * book, the trader that submitted the order is first informed of this.
	 * Next, we run bookkeep on each trader so that hey can update their books 
	 * and keep track of what orders have cleared.
	 * 
	 * @param quotingTrader - the guy that submitted the last order
	 * @param orderRep - the order report
	 */
	private void clearing(Trader quotingTrader, OrderReport orderRep) {
		if (orderRep.isOrderInBook()) {
			quotingTrader.addOrder(orderRep.getOrder());
		}
		for (Trade t : orderRep.getTrades()) {
			Trader buyer = tradersById.get(t.getBuyer());
			Trader seller = tradersById.get(t.getSeller());
			buyer.bookkeep(t);
			seller.bookkeep(t);
		}
	}
	

	private void populateMarket(double cash, int numAssets, double prob_market,
			double prob_limit, double prob_cancel, double prob_cross,
			double prob_inside, double prob_at, double prob_deeper,
			double xmin, double beta, double market_mu, double market_sigma,
			double limit_mu, double limit_sigma, double default_spread,
			double default_price, int rollMeanLen, int vMin, int vMax, 
			int vMinus, int orderMin, int orderMax) {
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
		this.tIds = new ArrayList<Integer>(tradersById.keySet());
	}
	
	/**************************************************************************
	 *************************** Writing and Printing *************************
	 **************************************************************************/
	
	public void quotesToCSV(String fName) {
		try {
			File dumpFile = new File(dataDir+fName);
			BufferedWriter output = new BufferedWriter(new FileWriter(dumpFile));
			output.write("time, type, side, quantity, price, tId\n");
			for (Order q : quoteCollector) {
				String quoteString = (q.getTimestamp() + ", " +
										(q.isLimit() ? "limit" : "market") + ", " + 
										q.getSide() + ", " +
										q.getQuantity() + ", ");
				if (q.isLimit()) {
					quoteString += q.getPrice();
				} else {
					quoteString += "\t";
				}
				quoteString += (", " + q.gettId() + "\n");
				output.write(quoteString);
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void tapeToCSV(String fName) {
		try {
			File dumpFile = new File(dataDir+fName);
			BufferedWriter output = new BufferedWriter(new FileWriter(dumpFile));
			output.write("time, price, quantity, provider, taker, buyer, seller\n");
			for (Trade t : lob.getTape()) {
				output.write(t.toCSV());
			}
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeDaysData(String tradeDataName, String quoteDataName) {
		tapeToCSV(tradeDataName);
		quotesToCSV(quoteDataName);
	}
	
	public String toString() {
		StringWriter fileStr = new StringWriter();
		fileStr.write(lob.toString());
		fileStr.write("Traders:\n");
		for (int tId : tIds) {
			fileStr.write(tradersById.get(tId).toString());
		}
		return fileStr.toString();
	}

}
