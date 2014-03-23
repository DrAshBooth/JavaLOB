package lob;

import java.util.HashMap;

public class Order {
	private Integer timestamp;
	private Integer quantity;
	private Double price;
	private Integer qId;
	private Integer tId;
	private Order nextOrder;
	private Order prevOrder;
	// private Orderlist oL;
	
	public Order(HashMap<String,String> quote) {
		this.timestamp = Integer.parseInt(quote.get("timestamp"));
		this.quantity = Integer.parseInt(quote.get("quantity"));
		this.price = Double.parseDouble(quote.get("price"));
		this.qId = Integer.parseInt(quote.get("qId"));
		this.tId = Integer.parseInt(quote.get("tId"));
	}
	
	public static void updateQty(Integer qty, Integer timestamp) {
		
	}
	
	public String toString() {
        return quantity + "\t@\t" + price + "\tt=" + timestamp;
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

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getqId() {
		return qId;
	}

	public void setqId(Integer qId) {
		this.qId = qId;
	}

	public Integer gettId() {
		return tId;
	}

	public void settId(Integer tId) {
		this.tId = tId;
	}
	
	
	
}
