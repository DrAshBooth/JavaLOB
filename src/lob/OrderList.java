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
	
	// http://www.java2s.com/Tutorial/Java/0140__Collections/CreatingIterableObjectsusingaforeachforlooponanIterableobject.htm

	
	public void appendOrder(Order incomingOrder) {
		if (length == 0) {
			incomingOrder.setNextOrder(null);
			incomingOrder.setPrevOrder(null);
			headOrder = incomingOrder;
			tailOrder = incomingOrder;
		} else{
			incomingOrder.setPrevOrder(tailOrder);
			incomingOrder.setNextOrder(null);
			tailOrder.setNextOrder(incomingOrder);
			tailOrder = incomingOrder;
		}
	}
	
	public void removeOrder(Order order) {
		this.volume -= order.getQuantity();
		this.length -= 1;
		if (this.length == 0) {
			return;
		}
		Order tempNextOrder = order.getNextOrder();
		Order tempPrevOrder = order.getPrevOrder();
		if ((tempNextOrder != null) && (tempPrevOrder != null)) {
			tempNextOrder.setPrevOrder(tempPrevOrder);
			tempPrevOrder.setNextOrder(tempNextOrder);
		} else if (tempNextOrder != null){
			tempNextOrder.setPrevOrder(null);
			this.headOrder = tempNextOrder;
		} else if (tempPrevOrder != null){
			tempPrevOrder.setNextOrder(null);
			this.tailOrder = tempPrevOrder;
		}
	}
	
	public void moveTail(Order order) {
		if (order.getPrevOrder() != null) {
			order.getPrevOrder().setNextOrder(order.getNextOrder());
		} else {
			// Update head order
			this.headOrder = order.getNextOrder();
		}
		order.getNextOrder().setPrevOrder(order.getPrevOrder());
		// Set the previous tail's next order to this order
		this.tailOrder.setNextOrder(order);
		order.setPrevOrder(this.tailOrder);
		this.tailOrder = order;
		order.setNextOrder(null);
	}
	
	public String toString() {
		String outSting = "";
		for (Order o : this) {
			
		}
	}
	
	
	
	public Integer getLength() {
		return length;
	}

	public Order getHeadOrder() {
		return headOrder;
	}
	
}
