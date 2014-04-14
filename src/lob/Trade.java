package lob;

public class Trade {
	private int timestamp;
	private double price;
	private int qty;
	private int provider;
	private int taker;
	private int buyer;
	private int seller;
	
	public Trade(int time, double price, int qty, int provider, 
				 int taker, int buyer, int seller) {
		this.timestamp = time;
		this.price = price;
		this.qty = qty;
		this.provider = provider;
		this.taker = taker;
		this.buyer = buyer;
		this.seller = seller;
	}
	
	
	
	public String toSting() {
		return ("TRADE \n t= " + timestamp + 
				"\nprice = " + price +
				"\nquantity = " + qty +
				"\nProvider = " + provider +
				"\nTaker = " + taker +
				"\nBuyer = " + buyer +
				"\nSeller = " + seller);
	}
}
