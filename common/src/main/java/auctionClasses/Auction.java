package auctionClasses;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.ResultSet;
import java.sql.SQLException;

import auctionRemoteInterfaces.AuctionRemoteInterface;
import databaseRemoteInterfaces.DatabaseAuctionRemoteInterface;
import exceptions.DatabaseConnectionException;

public abstract class Auction implements Cloneable, Serializable {
	private static final long serialVersionUID = -578853581425972640L;
	private int auctionId;
	private int itemId;
	private int currentBid;
	private double startingPrice;
	private AuctionType auctionType;
	private long auctionClose;
	private boolean isClosed;
	private DatabaseAuctionRemoteInterface dbModule;
	protected AuctionRemoteInterface auctionModule;

	/**
	 * Returns a new Auction item with the auctionId and itemId modified
	 * 
	 * @param auctionId
	 * @param itemId
	 * @return
	 */
	public Auction copyWithIds(int auctionId, int itemId) {
		Auction returnVal;
		try {
			returnVal = (Auction) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		returnVal.auctionId = auctionId;
		returnVal.itemId = itemId;
		returnVal.isClosed = false;
		returnVal.currentBid = 0;
		return returnVal;
	}
	
	protected Auction() {
		try {
			Registry dbregistry = LocateRegistry.getRegistry("webcontainer",1019);
			dbModule = ((DatabaseAuctionRemoteInterface)dbregistry.lookup("auctionDatabase"));
			Registry auctionregistry = LocateRegistry.getRegistry("auctioncontainer",1019);
			auctionModule = ((AuctionRemoteInterface)auctionregistry.lookup("auction"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
	}

	protected Auction(AuctionType type) {
		this();
		this.auctionId = 0;
		this.itemId = 0;
		this.currentBid = 0;
		this.startingPrice = 0.0;
		this.auctionClose = 0;
		this.auctionType = type;
		this.isClosed = false;
	}

	public Auction(ResultSet result) throws SQLException {
		this();
		this.auctionId = result.getInt("auctionId");
		this.itemId = result.getInt("itemId");
		this.currentBid = result.getInt("currentBid");
		this.startingPrice = result.getDouble("startingPrice");
		this.auctionClose = result.getLong("endTime");
		this.auctionType = AuctionType.valueOf(result.getString("auctionType"));
		this.isClosed = result.getBoolean("isClosed");
	}

	public int getCurrentBidId() {
		return currentBid;
	}
	
	public Bid getCurrentBid() {
		try {
			return dbModule.getBid(this.getCurrentBidId());
		} catch (DatabaseConnectionException | RemoteException e) {
			return null;
		}
	}

	public void setCurrentBid(Bid currentBid) {
		this.currentBid = currentBid.getBidId();
	}

	public int getAuctionId() {
		return auctionId;
	}

	public int getItemId() {
		return itemId;
	}

	public long getAuctionClose() {
		return auctionClose;
	}

	public AuctionType getAuctionType() {
		return this.auctionType;
	}

	public double getStartingPrice() {
		return this.startingPrice;
	}

	protected void setStartingPrice(double startingPrice) {
		this.startingPrice = startingPrice;
	}

	public void setAuctionClose(long auctionClose) {
		this.auctionClose = auctionClose;
	}
	
	public void setClosedStatus() {
		this.isClosed = true;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void close() {
		this.isClosed = true;
		try {
			System.out.println("Auction class closing started");
			dbModule.closeAuction(this);
			auctionModule.delistAuction(this);
			System.out.println("Auction class closing finished");
		} catch (RemoteException | DatabaseConnectionException e) {
			e.printStackTrace();
		}
	}

	public enum AuctionType {
		FORWARD, DUTCH
	}
}
