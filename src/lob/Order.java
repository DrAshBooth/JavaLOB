package lob;

import java.util.HashMap;

public class Order {
	private int timestamp;
	private int quantity;
	private double price;
	private int qId;
	private int tId;
	private Order nextOrder;
	private Order prevOrder;
	private OrderList oL;
	
	public Order(HashMap<String,String> quote, OrderList ol) {
		this.timestamp = Integer.parseInt(quote.get("timestamp"));
		this.quantity = Integer.parseInt(quote.get("quantity"));
		this.price = Double.parseDouble(quote.get("price"));
		this.qId = Integer.parseInt(quote.get("qId"));
		this.tId = Integer.parseInt(quote.get("tId"));
		this.oL = ol;
	}
	
	public void updateQty(int qty, int tstamp) {
		if ((qty > this.quantity) && (this.oL.getTailOrder() != this)) {
			// Move order to the end of the list. i.e. loses time priority
			this.oL.moveTail(this);
		}
		oL.setVolume(oL.getVolume()-(this.quantity-qty));
		this.timestamp = tstamp;
		this.quantity = qty;
	}
	
	public String toString() {
        return Integer.toString(quantity) + "\t@\t" + Double.toString(price) + 
        		"\tt=" + Integer.toString(timestamp);
    }

	
	// Getters and Setters
	public Order getNextOrder() {
		return nextOrder;
	}

	public void setNextOrder(Order nextOrder) {
		this.nextOrder = nextOrder;
	}

	public Order getPrevOrder() {
		return prevOrder;
	}

	public void setPrevOrder(Order prevOrder) {
		this.prevOrder = prevOrder;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getqId() {
		return qId;
	}

	public void setqId(int qId) {
		this.qId = qId;
	}

	public int gettId() {
		return tId;
	}

	public void settId(int tId) {
		this.tId = tId;
	}

	public OrderList getoL() {
		return oL;
	}
	
	
	
}
