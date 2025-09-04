package shipping;

import auctionClasses.Bid;
import auctionClasses.Item;
import shippingStrategies.*;

public class PrivateShipping {
	private ShippingModule shippingModule;

	public PrivateShipping(ShippingModule shippingModule) {
		this.shippingModule = shippingModule;
	}

	double getItemPrice(Bid bid) {
		return bid.getBidPrice();
	}

	double getShippingPrice(Item item, Bid bid) {
		ShippingStrategy strategy;
		switch(shippingModule.getShippingType()) {
		case STANDARD:
			strategy = new StandardShipping();
			break;
		case EXPEDITED:
			strategy = new ExpeditedShipping();
			break;
		default:
			throw new IllegalStateException("Unknown shipping type");
		}
		return strategy.calculateShippingCost(item, bid);
	}
	
	public int getShippingDays(Item item,Bid bid) {
		ShippingStrategy strategy;
		switch(shippingModule.getShippingType()) {
		case STANDARD:
			strategy = new StandardShipping();
			break;
		case EXPEDITED:
			strategy = new ExpeditedShipping();
			break;
		default:
			throw new IllegalStateException("Unknown shipping type");
		}
		return strategy.getNumberOfShippingDays(item, bid);
		
	}
}
