package auctionClasses;

import auctionClasses.Auction.AuctionType;

/**
 * Builds an Auction object to be sent to the database module for the creation
 * of a new auction.
 * Using this object for other purposes will result in exceptions being thrown
 * as the auctionId will be 0.
 * All calls allow for chaining
 */
public class AuctionListingBuilder {
	private AuctionType type;
	private Auction auction;
	private Item item;

	/**
	 * Create a new builder
	 * 
	 * @param auctionType The type of auction to be created
	 */
	public AuctionListingBuilder(AuctionType auctionType) {
		this.type = auctionType;
		switch (auctionType) {
			case DUTCH:
				this.auction = new DutchAuction();
				break;
			case FORWARD:
				this.auction = new ForwardAuction();
				break;
			default:
				throw new IllegalArgumentException("Unsupported auction type.");
		}
	}

	public AuctionListingBuilder setItem(Item item) {
		this.item = item;
		return this;
	}

	public AuctionListingBuilder setStartingPrice(double startPrice) {
		auction.setStartingPrice(startPrice);
		return this;
	}

	public AuctionListingBuilder setAuctionClose(long closeTime) {
		auction.setAuctionClose(closeTime);
		return this;
	}

	/**
	 * Sets a Dutch auction increment price. Will have no effect on other auction
	 * types
	 * 
	 * @param incrementPrice
	 */
	public AuctionListingBuilder setDutchIncrement(double decrementPrice) {
		if (auction instanceof DutchAuction) {
			((DutchAuction) auction).setIncrement(decrementPrice);
		}
		return this;
	}
	
	public AuctionListingBuilder setDutchReserve(double reservePrice) {
		if (auction instanceof DutchAuction) {
			((DutchAuction) auction).setReservePrice(reservePrice);
		}
		return this;
	}

	/**
	 * Sets a Dutch auction reserve price. Will have no effect on other auction
	 * types
	 * 
	 * @param reservePrice
	 */
	public AuctionListingBuilder setReservePrice(double reservePrice) {
		if (auction instanceof DutchAuction) {
			((DutchAuction) auction).setReservePrice(reservePrice);
		}
		return this;
	}

	/**
	 * Builds an auction
	 * 
	 * @return the built auction
	 */
	public AuctionListing build() {
		if (item == null) {
			throw new IllegalStateException("Item must be set before building an AuctionListing.");
		}
		if(auction.getAuctionClose() <= System.currentTimeMillis()) {
			throw new IllegalStateException("Closing time must be in the future");
		}
		//Additional checks can be added here before building
		switch(this.type) {
		case DUTCH:
			if(((DutchAuction)auction).getIncrement() <= 0.0) {
				throw new IllegalStateException("Increment value must be greater than 0");
			}
			if(((DutchAuction)auction).getReservePrice() < 0.0) {
				throw new IllegalStateException("Reserve price must be greater or equal to 0");
			}
			return new AuctionListing(auction, item);
		case FORWARD:
			return new AuctionListing(auction, item);
		default:
			return null;
		}
	}
}
