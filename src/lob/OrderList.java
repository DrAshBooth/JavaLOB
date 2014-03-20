package lob;

public class OrderList {
	private Order headOrder;
	private Order tailOrder;
	private Integer length;
	private Integer volume;    // Total volume at this price level
	private Order last;
	
	public OrderList() {
		headOrder = null;
		tailOrder = null;
		length = 0;
		volume = 0;
		last = null;
	}
	
	public void appendOrder(Order incomingOrder) {
		if (length == 0) {
			
		} else{
			
		}
	}
	// http://www.java2s.com/Tutorial/Java/0140__Collections/CreatingIterableObjectsusingaforeachforlooponanIterableobject.htm
	
	
	
	
	
	
	
	
	public Integer getLength() {
		return length;
	}

	public Order getHeadOrder() {
		return headOrder;
	}
	
}
