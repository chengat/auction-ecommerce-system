package auctionClasses;

import java.io.Serializable;

public class Item implements Serializable{
	private static final long serialVersionUID = -4458249517065742105L;
	private int itemId;
	private String itemName;
	private String itemDescription;
	private double shippingCost;
	private double expeditedShippingCost;
	public Item(int itemId, String itemName, String itemDescription, double shippingCost, double expeditedShippingCost) {
		super();
		this.itemId = itemId;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		this.shippingCost = shippingCost;
		this.expeditedShippingCost = expeditedShippingCost;
	}
	
	public Item(Item toCopy, int newId) {
		this(newId, toCopy.itemName,toCopy.itemDescription,toCopy.shippingCost,toCopy.expeditedShippingCost);
	}
	
	public int getItemId() {
		return itemId;
	}
	public String getItemName() {
		return itemName;
	}
	public String getItemDescription() {
		return itemDescription;
	}
	public double getShippingCost() {
		return shippingCost;
	}
	public double getExpeditedShippingCost() {
		return expeditedShippingCost;
	}
}
