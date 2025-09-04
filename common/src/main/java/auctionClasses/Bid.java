package auctionClasses;

import java.io.Serializable;

public class Bid implements Serializable{
	private static final long serialVersionUID = 3017960049360516741L;
	private int bidId;
	private String username;
	private int auctionId;
	private double bidPrice;
	private long bidTime;
	
	public Bid(int bidId, String username, int auctionId, double bidPrice, long bidTime) {
		super();
		this.bidId = bidId;
		this.username = username;
		this.auctionId = auctionId;
		this.bidPrice = bidPrice;
		this.bidTime = bidTime;
	}

	public int getBidId() {
		return bidId;
	}

	public String getUsername() {
		return username;
	}

	public int getAuctionId() {
		return auctionId;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public long getBidTime() {
		return bidTime;
	}

	@Override
	public String toString() {
		return "Bid [bidId=" + bidId + ", username=" + username + ", auctionId=" + auctionId + ", bidPrice=" + bidPrice
				+ ", bidTime=" + bidTime + "]";
	}
}
