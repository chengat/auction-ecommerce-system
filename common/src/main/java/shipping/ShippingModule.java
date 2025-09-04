package shipping;

import auctionClasses.Bid;
import auctionClasses.Item;
import paymentClasses.ShippingType;

public class ShippingModule {
	private ShippingType shippingType;

	public ShippingModule() {
		this.shippingType = ShippingType.STANDARD;
	}

	public void setShippingType(ShippingType type) {
		this.shippingType = type;
	}

	public double getItemPrice(Bid bid) {
		PrivateShipping ship = new PrivateShipping(this);
		return ship.getItemPrice(bid);
	}

	public double getShippingPrice(Item item, Bid bid) {
		PrivateShipping ship = new PrivateShipping(this);
		return ship.getShippingPrice(item,bid);
	}

	public ShippingType getShippingType() {
		return this.shippingType;
	}
	
	public int getShippingDays(Item item,Bid bid) {
		PrivateShipping ship = new PrivateShipping(this);
		
		return ship.getShippingDays(item, bid);
		
	}
}
