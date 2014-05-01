package lob;


public class Order {
	private int timestamp;
	private boolean limit;
	private int quantity;
	private String side;
	private double price;
	private int qId;
	private int tId;
	private Order nextOrder;
	private Order prevOrder;
	private OrderList oL;
	
	public Order(int time, boolean limit, int quantity, int tId, String side) {
		this(time, limit, quantity, tId, side, null);
	}	
	
	public Order(int time, boolean limit, int quantity, 
				int tId, String side, Double price) {
		
		this.timestamp = time;
		this.limit = limit;
		this.side = side;
		this.quantity = quantity;
		if (price!=null) {
			this.price = (double)price;
		}
		this.tId = tId;
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

	public boolean isLimit() {
		return limit;
	}

	public String getSide() {
		return side;
	}

	public void setoL(OrderList oL) {
		this.oL = oL;
	}
	
	
	
}
