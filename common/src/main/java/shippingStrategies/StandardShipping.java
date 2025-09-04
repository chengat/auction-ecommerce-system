package shippingStrategies;

import auctionClasses.Bid;
import auctionClasses.Item;

public class StandardShipping implements ShippingStrategy {

	@Override
	public double calculateShippingCost(Item item, Bid bid) {
		return item.getShippingCost();
	}

	@Override
	public int getNumberOfShippingDays(Item item, Bid bid) {
		return 5;
	}

}