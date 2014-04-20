package lob;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderReport {
	/*
	 * Return after an order is submitted to the lob. Contains:
	 * 	- trades:
	 * 
	 * 	- orderInBook
	 */
	private ArrayList<Trade> trades = new ArrayList<Trade>();
	private boolean orderInBook = false;
	private HashMap<String, String> order = new HashMap<String, String>();
	
	public OrderReport(ArrayList<Trade> trades, 
					   boolean orderInBook) {
		this.trades = trades;
		this.orderInBook = orderInBook;
	}

	public HashMap<String, String> getOrders() {
		return order;
	}

	public ArrayList<Trade> getTrades() {
		return trades;
	}

	public boolean isOrderInBook() {
		return orderInBook;
	}

	public void setOrder(HashMap<String, String> order) {
		this.order = order;
	}
	
	public String toString() {
		String retString = "--- Order Report ---:\nTrades:\n";
		for (Trade t : trades) {
			retString += ("\n" + t.toString());
		}
		retString += ("order in book? " + orderInBook + "\n");
		retString+= ("\nOrders:\n");
		retString += (order.toString());
		return  retString + "\n--------------------------";
	}
	
}
