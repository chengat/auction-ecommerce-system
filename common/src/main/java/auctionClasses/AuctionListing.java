package auctionClasses;

import java.io.Serializable;

public class AuctionListing implements Serializable{
	private static final long serialVersionUID = -2329352429168479519L;
	private Auction auction;
	private Item item;
	public AuctionListing(Auction auction, Item item) {
		super();
		this.auction = auction;
		this.item = item;
	}
	public Auction getAuction() {
		return auction;
	}
	public Item getItem() {
		return item;
	}
	
	@Override
	public String toString() {
		return "AuctionListing [auction=" + auction + ", item=" + item + "]";
	}
}
