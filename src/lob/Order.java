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
	
	
	
}
