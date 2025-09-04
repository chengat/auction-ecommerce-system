package auctionClasses;

import exceptions.DatabaseConnectionException;

import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DutchAuction extends Auction {
	private double increment;
	private double reservePrice;

	public DutchAuction(ResultSet result) throws SQLException {
		super(result);
		this.increment = result.getDouble("increment");
		this.reservePrice = result.getDouble("reservePrice");
	}

	public DutchAuction() {
		super(AuctionType.DUTCH);
		this.increment = 0.0;
		this.reservePrice = 0.0;
	}

	public double getIncrement() {
		return increment;
	}

	public double getReservePrice() {
		return reservePrice;
	}

	protected void setIncrement(double increment) {
		this.increment = increment;
	}

	protected void setReservePrice(double reservePrice) {
		this.reservePrice = reservePrice;
	}
	
	public void changeDutchCurrentPrice(double newPrice) {
		this.setStartingPrice(newPrice);
	}

	public synchronized void decrementPrice() throws DatabaseConnectionException,RemoteException {
		if (isClosed()) {
			return;
		}
		System.out.println("Decrement called on auction: " + this.toString());
		double newPrice = this.getStartingPrice() - increment;
		System.out.println("New price calculated at " + newPrice);
		if (newPrice <= reservePrice) {
			newPrice = reservePrice;
		}

		changeDutchCurrentPrice(newPrice);
		auctionModule.updateAuctionPrice(this, newPrice);
		System.out.println("New State: " + this.toString());

		// TODO: notify users about the price reduction
	}

	@Override
	public String toString() {
		return "DutchAuction [increment=" + increment + ", reservePrice=" + reservePrice
				+ ", getCurrentBid()=" + getCurrentBidId() + ", getAuctionId()=" + getAuctionId()
				+ ", getItemId()=" + getItemId() + ", getAuctionClose()=" + getAuctionClose()
				+ ", getAuctionType()=" + getAuctionType()
				+ ", startingPrice=" + getStartingPrice()
				+ ", isClosed=" + isClosed()
				+ "]";
	}
}
