package shippingStrategies;

import auctionClasses.Bid;
import auctionClasses.Item;

public class ExpeditedShipping implements ShippingStrategy {

	@Override
	public double calculateShippingCost(Item item, Bid bid) {
		return item.getExpeditedShippingCost();
	}

	@Override
	public int getNumberOfShippingDays(Item item, Bid bid) {
		return 2;
	}

}
