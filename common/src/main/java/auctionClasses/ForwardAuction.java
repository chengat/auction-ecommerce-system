package auctionClasses;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForwardAuction extends Auction {

	public ForwardAuction(ResultSet result) throws SQLException {
		super(result);
	}

	protected ForwardAuction() {
		super(AuctionType.FORWARD);
	}
	

	@Override
	public String toString() {
		return "ForwardAuction [currentBid=" + getCurrentBidId() + ", auctionId=" + getAuctionId()
				+ ", itemId=" + getItemId() + ", auctionClose=" + getAuctionClose()
				+ ", auctionType=" + getAuctionType()
				+ ", startingPrice=" + getStartingPrice()
				+ ", isClosed=" + isClosed() + "]";
	}
}
