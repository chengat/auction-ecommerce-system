package paymentClasses;

import java.io.Serializable;

public class Receipt implements Serializable{
	private static final long serialVersionUID = -1969730702002371508L;
	private int auctionId;
	private String userId;
	private double amount;
	private int creditCardId;
	private ShippingType shippingType;
	public Receipt(int auctionId, String userId, double amount, int creditCardId, ShippingType shippingType) {
		super();
		this.auctionId = auctionId;
		this.userId = userId;
		this.amount = amount;
		this.creditCardId = creditCardId;
		this.shippingType = shippingType;
	}
	public int getAuctionId() {
		return auctionId;
	}
	public String getUserId() {
		return userId;
	}
	public double getAmount() {
		return amount;
	}
	public int getCreditCardId() {
		return creditCardId;
	}
	
	public ShippingType getShippingType() {
		return this.shippingType;
	}
}
