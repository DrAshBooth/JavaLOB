package lob;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

public class OrderTree {
	TreeMap<Double, OrderList> priceTree = new TreeMap<Double, OrderList>();
	HashMap<Double, OrderList> priceMap = new HashMap<Double, OrderList>();;
	HashMap<Integer, Order> orderMap = new HashMap<Integer, Order>();
	int volume;
	int nOrders;
	int depth;
	
	public OrderTree() {
		reset();
	}
	
	public void reset() {
		priceTree.clear();
		priceMap.clear();
		orderMap.clear();
		volume = 0;
		nOrders = 0;
		depth = 0;
	}
	
	public Integer length() {
		return orderMap.size();
	}
	
	public OrderList getPriceList(double price) {
		/*
		 * Returns the OrderList object associated with 'price'
		 */
		return priceMap.get(price);
	}
	
	public Order getOrder(int id) {
		/*
		 * Returns the order given the order id
		 */
		return orderMap.get(id);
	}
	
	public void createPrice(double price) {
		depth += 1;
		OrderList newList = new OrderList();
		priceTree.put(price, newList);
		priceMap.put(price, newList);
	}
	
	public void removePrice(double price) {
		depth -= 1;
		priceTree.remove(price);
		priceMap.remove(price);
	}
	
	public boolean priceExists(double price) {
		return priceMap.containsKey(price);
	}
	
	public boolean orderExists(int id) {
		return orderMap.containsKey(id);
	}
	
	public void insertOrder(Order quote) {
		int quoteID = quote.getqId();
		double quotePrice = quote.getPrice();
		if (orderExists(quoteID)) {
			removeOrderByID(quoteID);
		}
		nOrders += 1;
		if (!priceExists(quotePrice)) {
			createPrice(quotePrice);
		}
		quote.setoL(priceMap.get(quotePrice));
		priceMap.get(quotePrice).appendOrder(quote);
		orderMap.put(quoteID, quote);
		volume += quote.getQuantity();
	}
	
	public void updateOrderQty(int qty, int qId) {
		Order order = this.orderMap.get(qId);
		int originalVol = order.getQuantity();
		order.updateQty(qty, order.getTimestamp());
		this.volume += (order.getQuantity() - originalVol);
	}
	
	public void updateOrder(Order orderUpdate) {
		int idNum = orderUpdate.getqId();
		double price = orderUpdate.getPrice();
		Order order = this.orderMap.get(idNum);
		int originalVol = order.getQuantity();
		if (price != order.getPrice()) {
			// Price has been updated
			OrderList tempOL = this.priceMap.get(order.getPrice());
			tempOL.removeOrder(order);
			if (tempOL.getLength()==0) {
				removePrice(order.getPrice());
			}
			insertOrder(orderUpdate);
		} else {
			// The quantity has changed
			order.updateQty(orderUpdate.getQuantity(), 
					orderUpdate.getTimestamp());
		}
		this.volume += (order.getQuantity() - originalVol);
	}
	
	public void removeOrderByID(int id) {
		this.nOrders -=1;
		Order order = orderMap.get(id);
		this.volume -= order.getQuantity();
		order.getoL().removeOrder(order);
		if (order.getoL().getLength() == 0) {
			this.removePrice(order.getPrice());
		}
		this.orderMap.remove(id);
	}
	
	public Double maxPrice() {
		if (this.depth>0) {
			return this.priceTree.lastKey();
		} else {
			return null;
		}
	}
	
	public Double minPrice() {
		if (this.depth>0) {
			return this.priceTree.firstKey();
		} else {
			return null;
		}
	}
	
	public OrderList maxPriceList() {
		if (this.depth>0) {
			return this.getPriceList(maxPrice());
		} else {
			return null;
		}
	}
	
	public OrderList minPriceList() {
		if (this.depth>0) {
			return this.getPriceList(minPrice());
		} else {
			return null;
		}
	}
	
	public String toString() {
		String outString = "| The Book:\n" + 
							"| Max price = " + maxPrice() +
							"\n| Min price = " + minPrice() +
							"\n| Volume in book = " + getVolume() +
							"\n| Depth of book = " + getDepth() +
							"\n| Orders in book = " + getnOrders() +
							"\n| Length of tree = " + length() + "\n";
		for (Map.Entry<Double, OrderList> entry : this.priceTree.entrySet()) {
			outString += entry.getValue().toString();
			outString += ("|\n");
		}
		return outString;
	}

	public Integer getVolume() {
		return volume;
	}

	public Integer getnOrders() {
		return nOrders;
	}

	public Integer getDepth() {
		return depth;
	}
	
}

