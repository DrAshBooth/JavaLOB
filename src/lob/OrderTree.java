package lob;

import java.util.TreeMap;
import java.util.HashMap;

public class OrderTree {
	TreeMap<Double, OrderList> priceTree;
	HashMap<Double, OrderList> priceMap;
	HashMap<Integer, Order> orderMap;
	Integer volume;
	Integer nOrders;
	Integer depth;
	
	public OrderTree() {
		volume = 0;
		nOrders = 0;
		depth = 0;
	}
	
	public Integer length() {
		return orderMap.size();
	}
	
	public OrderList getPriceList(Double price) {
		/*
		 * Returns the OrderList object associated with 'price'
		 */
		return priceMap.get(price);
	}
	
	public Order getOrder(Integer id) {
		/*
		 * Returns the order given the order id
		 */
		return orderMap.get(id);
	}
	
	public void createPrice(Double price) {
		depth += 1;
		OrderList newList = new OrderList();
		priceTree.put(price, newList);
		priceMap.put(price, newList);
	}
	
	public void removePrice(Double price) {
		depth -= 1;
		priceTree.remove(price);
		priceMap.remove(price);
	}
	
	public boolean priceExists(Double price) {
		return priceMap.containsKey(price);
	}
	
	public boolean orderExists(Integer id) {
		return orderMap.containsKey(id);
	}
	
	public void insertOrder(HashMap<String, String> quote) {
		Integer quoteID = Integer.parseInt(quote.get("qId"));
		Double quotePrice = Double.parseDouble(quote.get("price"));
		if (orderExists(quoteID)) {
			removeOrderByID(quoteID);
		}
		nOrders += 1;
		if (!priceExists(quotePrice)) {
			createPrice(quotePrice);
		}
		Order o = new Order(quote, priceMap.get(quotePrice));
		priceMap.get(o.getPrice()).appendOrder(o);
		orderMap.put(o.getqId(), o);
		volume += o.getQuantity();
	}
	
	public void removeOrderByID(Integer id) {
		
	}
	
}

