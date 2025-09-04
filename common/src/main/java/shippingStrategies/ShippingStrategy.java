package shippingStrategies;

import auctionClasses.Bid;
import auctionClasses.Item;

public interface ShippingStrategy {
	public double calculateShippingCost(Item item, Bid bid);
	
	public int getNumberOfShippingDays(Item item, Bid bid);
}