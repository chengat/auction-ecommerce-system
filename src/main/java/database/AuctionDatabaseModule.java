package database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.AuctionObjectLoader;
import auctionClasses.Bid;
import auctionClasses.DutchAuction;
import auctionClasses.Item;
import authClasses.User;
import database.DatabaseManager.Database;
import databaseConnectors.AuctionDatabaseConnector;
import databaseRemoteInterfaces.DatabaseAuctionRemoteInterface;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;
import websocket.WebSocketHandler;

public class AuctionDatabaseModule extends UnicastRemoteObject implements DatabaseAuctionRemoteInterface{
	private static final long serialVersionUID = -3578826968066440151L;

	public AuctionDatabaseModule() throws RemoteException {
		super();
	}

	/**
	 * Attempts to place a bid. Bids are verified and placed atomically.
	 * @param user The user placing the bid
	 * @param auctionId The auction on which to bid
	 * @param price The price the user is willing to pay
	 * @return A Bid object if the bid is placed
	 * @throws DatabaseConnectionException If there is a database connection issue
	 * @throws AuctionBidException If the bid is invalid. A message will be included
	 */
	public Bid placeBid(User user, int auctionId, double price) throws DatabaseConnectionException, AuctionBidException, RemoteException{
		AuctionDatabaseConnector database;
		//Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			database.beginTransaction();
			//Get a fresh version of the auction
			AuctionListing auctionListing = new CatalogueDatabaseModule().getAuctionInternal(auctionId, database);
			if(auctionListing == null) {
				throw new AuctionBidException("AuctionId is invalid");
			}
			Auction auction = auctionListing.getAuction();
			//Check if the auction is still open
			if(auction.isClosed() == true) {
				throw new AuctionBidException("This auction is closed");
			}
			Bid currentBid = getBid(auction.getCurrentBidId(),database);
			if(price < auction.getStartingPrice() || currentBid != null && currentBid.getBidPrice() >= price) {
				throw new AuctionBidException("Bid is too low");
			}
			//This is a valid bid, store it
			long commitTime = System.currentTimeMillis();
			PreparedStatement writeStmt = database.getNewPreparedStatement("INSERT INTO bids (username, auctionId, bidPrice, bidTime) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			writeStmt.setString(1, user.getUsername());
			writeStmt.setInt(2, auctionId);
			writeStmt.setDouble(3, price);
			writeStmt.setLong(4, commitTime);
			//Run update and get the key (the bidId)
			writeStmt.executeUpdate();
			ResultSet writeResult = writeStmt.getGeneratedKeys();
			if(writeResult.next() == false) {
				throw new DatabaseConnectionException("Internal database error");
			}
			int bidId = writeResult.getInt(1);
			//Update the auction with the new highest bid
			PreparedStatement auctionUpdateStmt = database.getNewPreparedStatement("UPDATE auctions SET currentBid = ? WHERE auctionId = ?");
			auctionUpdateStmt.setInt(1, bidId);
			auctionUpdateStmt.setInt(2, auctionId);
			auctionUpdateStmt.executeUpdate();
			//Commit and close
			database.commitTransaction();
			Bid newBid = new Bid(bidId, user.getUsername(), auctionId, price, commitTime);
			database.closeConnection();
			new CatalogueDatabaseModule().getAuction(auctionId).getAuction().setCurrentBid(newBid);
			return newBid;
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error");}//Not much to do
		}
	}
	
	/**
	 * Gets a Bid
	 * @param bidId the ID of the Bid to get
	 * @param db optional database connection to use as part of a transaction. Will NOT be closed automatically
	 * @return A Bid if it exists, null otherwise
	 * @throws DatabaseConnectionException if an error occurs connecting to the database
	 */
	protected Bid getBid(int bidId, AuctionDatabaseConnector db) throws DatabaseConnectionException {
		AuctionDatabaseConnector database;
		//Only create a new connection if none was supplied
		if(db == null) {
			//Connect to the database
			try {
				database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DatabaseConnectionException("An error occured when connecting to the database");
			}
		}else {
			database = db;
		}
		//Logic
		try {
			PreparedStatement bidStmt = database.getNewPreparedStatement("SELECT * FROM bids WHERE bidId = ?");
			bidStmt.setInt(1, bidId);
			ResultSet bidResult = bidStmt.executeQuery();
			boolean exists = bidResult.next();
			if(exists == false) {
				return null;
			}
			Bid result = AuctionObjectLoader.loadBid(bidResult);
			//Only close the connection if we created it
			if(db == null) {
				database.closeConnection();
			}
			return result;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
			} catch (SQLException e1) {throw new DatabaseConnectionException("Error connecting to the database");}//Not much to do
			throw new DatabaseConnectionException("Error connecting to the database");
		}
	}
	
	/**
	 * Gets a Bid
	 * @param bidId the ID of the Bid to get
	 * @return A Bid if it exists, null otherwise
	 * @throws DatabaseConnectionException if an error occurs connecting to the database
	 */
	public Bid getBid(int bidId) throws DatabaseConnectionException , RemoteException{
		return this.getBid(bidId, null);
	}
	
	/**
	 * Attempts to close an auction by setting its end time to the current time. This will prevent further bids but WILL NOT notify anyone
	 * @param auction The auction to close
	 * @throws DatabaseConnectionException  if an error occurs connecting to the database
	 */
	public void closeAuction(Auction auction) throws DatabaseConnectionException, RemoteException {
		AuctionDatabaseConnector database;
		//Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement closeStmt = database.getNewPreparedStatement("UPDATE auctions SET isClosed = TRUE WHERE auctionId = ?");
			closeStmt.setInt(1, auction.getAuctionId());
			closeStmt.executeUpdate();
			database.closeConnection();
			Bid winningBid = auction.getCurrentBid();
			WebSocketHandler.sendAuctionEndNotification(String.valueOf(auction.getAuctionId()), winningBid == null ? "" : winningBid.getUsername(), winningBid == null ? 0.0 : winningBid.getBidPrice());
			AuctionCache.getInstance().getAuction(auction.getAuctionId(), new CatalogueDatabaseModule()).getAuction().setClosedStatus();
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error");}//Not much to do
		}
	}
	
	/**
	 * Changes the starting price of an auction to reflect a price decrement
	 * @param auctionId The auctionID
	 * @param newStartingPrice the new price
	 * @throws DatabaseConnectionException If an error occurs connecting to the database
	 */
	public void changeDutchStartingPrice(int auctionId, double newStartingPrice) throws DatabaseConnectionException, RemoteException {
		AuctionDatabaseConnector database;
		//Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement priceStmt = database.getNewPreparedStatement("UPDATE auctions SET startingPrice = ? WHERE auctionId = ?");
			priceStmt.setDouble(1, newStartingPrice);
			priceStmt.setInt(2, auctionId);
			priceStmt.executeUpdate();
			DutchAuction auction = (DutchAuction)new CatalogueDatabaseModule().getAuction(auctionId).getAuction();
			auction.changeDutchCurrentPrice(newStartingPrice);
			WebSocketHandler.sendPriceDecrement(String.valueOf(auctionId), newStartingPrice);
			database.closeConnection();
		}catch(SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error");}//Not much to do
		}
	}
	
	/**
	 * Changes the starting price of an auction to reflect a price decrement
	 * @param auction the auction
	 * @param newStartingPrice the new price
	 * @throws DatabaseConnectionException If an error occurs connecting to the database
	 */
	public void changeDutchStartingPrice(Auction auction, double newStartingPrice) throws DatabaseConnectionException, RemoteException {
		this.changeDutchStartingPrice(auction.getAuctionId(), newStartingPrice);
	}
	
	/**
	 * debug method to create a new auction and item. The auctionId and itemId will be ignored and generated on auction creation.
	 * @param toCreate The auction information for creation
	 * @return an AuctionListing object with the correct IDs if successful, null otherwise
	 * @throws DatabaseConnectionException 
	 */
	public AuctionListing createAuction(AuctionListing toCreate) throws DatabaseConnectionException, RemoteException {
		AuctionDatabaseConnector database;
		//Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			database.beginTransaction();
			//Add the item
			PreparedStatement itemStmt = database.getNewPreparedStatement("INSERT INTO catalogue (itemName, itemDesc, shippingCost, expeditedShippingCost) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			itemStmt.setString(1, toCreate.getItem().getItemName());
			itemStmt.setString(2, toCreate.getItem().getItemDescription());
			itemStmt.setDouble(3, toCreate.getItem().getShippingCost());
			itemStmt.setDouble(4, toCreate.getItem().getExpeditedShippingCost());
			//Execute and verify it can be added
			itemStmt.executeUpdate();
			ResultSet itemUpdateKeySet = itemStmt.getGeneratedKeys();
			if(itemUpdateKeySet.next() == false) {
				throw new DatabaseConnectionException("Could not create item");
			}
			//Create return object with new ID
			Item newItem = new Item(toCreate.getItem(),itemUpdateKeySet.getInt(1));
			//Create the auction
			PreparedStatement auctionStmt;
			switch(toCreate.getAuction().getAuctionType()) {
			case DUTCH:
				auctionStmt = database.getNewPreparedStatement("INSERT INTO auctions (itemId,currentBid, auctionType, endTime, startingPrice,increment,reservePrice,isClosed) VALUES (?,?,?,?,?,?,?,FALSE)",Statement.RETURN_GENERATED_KEYS);
				auctionStmt.setInt(1,newItem.getItemId());
				auctionStmt.setDouble(2, 0);
				auctionStmt.setString(3, toCreate.getAuction().getAuctionType().toString());
				auctionStmt.setLong(4, toCreate.getAuction().getAuctionClose());
				auctionStmt.setDouble(5, toCreate.getAuction().getStartingPrice());
				auctionStmt.setDouble(6, ((DutchAuction)toCreate.getAuction()).getIncrement());
				auctionStmt.setDouble(7, ((DutchAuction)toCreate.getAuction()).getReservePrice());
				break;
			case FORWARD:
				auctionStmt = database.getNewPreparedStatement("INSERT INTO auctions (itemId,currentBid, auctionType, endTime, startingPrice, isClosed) VALUES (?,?,?,?,?,FALSE)",Statement.RETURN_GENERATED_KEYS);
				auctionStmt.setInt(1,newItem.getItemId());
				auctionStmt.setDouble(2, 0);
				auctionStmt.setString(3, toCreate.getAuction().getAuctionType().toString());
				auctionStmt.setLong(4, toCreate.getAuction().getAuctionClose());
				auctionStmt.setDouble(5, toCreate.getAuction().getStartingPrice());
				break;
			default:
				throw new DatabaseConnectionException("Invalid Auction Type");
			}
			auctionStmt.executeUpdate();
			ResultSet auctionUpdateKeySet = auctionStmt.getGeneratedKeys();
			if(auctionUpdateKeySet.next() == false) {
				throw new DatabaseConnectionException("Could not create auction");
			}
			System.out.println("Creating Auction : Item - "+auctionUpdateKeySet.getInt(1)+" : "+newItem.getItemId());
			Auction newAuction = toCreate.getAuction().copyWithIds(auctionUpdateKeySet.getInt(1),newItem.getItemId());
			database.commitTransaction();
			database.closeConnection();
			AuctionListing newListing = new AuctionListing(newAuction,newItem);
			AuctionCache.getInstance().registerAuction(newListing);
			return newListing;
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error");}//Not much to do
		}
	}
}
