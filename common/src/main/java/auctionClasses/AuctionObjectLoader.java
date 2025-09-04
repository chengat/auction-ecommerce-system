package auctionClasses;

import java.sql.ResultSet;
import java.sql.SQLException;

import auctionClasses.Auction.AuctionType;

public class AuctionObjectLoader {
	/**
	 * Loads a query result directly into an Item Object
	 * 
	 * @param result the ResultSet from a query
	 * @return the Item
	 * @throws SQLException
	 */
	public static Item loadItem(ResultSet result) throws SQLException {
		return new Item(
				result.getInt("itemId"),
				result.getString("itemName"),
				result.getString("itemDesc"),
				result.getDouble("shippingCost"),
				result.getDouble("expeditedShippingCost"));
	}

	public static AuctionListing loadListing(ResultSet result) throws SQLException {
		return new AuctionListing(loadAuction(result), loadItem(result));
	}

	public static Bid loadBid(ResultSet result) throws SQLException {
		return new Bid(
				result.getInt("bidId"),
				result.getString("username"),
				result.getInt("auctionId"),
				result.getDouble("bidPrice"),
				result.getLong("bidTime"));
	}

	public static Auction loadAuction(ResultSet result) throws SQLException {
		AuctionType type = AuctionType.valueOf(result.getString("auctionType"));
		switch (type) {
			case DUTCH:
				return new DutchAuction(result);
			case FORWARD:
				return new ForwardAuction(result);
			default:
				throw new SQLException("Invalid auction type passed");
		}
	}
}
