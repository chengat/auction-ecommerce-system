package database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import auctionClasses.AuctionListing;
import auctionClasses.AuctionObjectLoader;
import database.DatabaseManager.Database;
import databaseConnectors.AuctionDatabaseConnector;
import databaseRemoteInterfaces.DatabaseCatalogueRemoteInterface;
import exceptions.DatabaseConnectionException;

public class CatalogueDatabaseModule extends UnicastRemoteObject implements DatabaseCatalogueRemoteInterface{
	private static final long serialVersionUID = 4960563385265902233L;
	private AuctionCache cache;
	
	public CatalogueDatabaseModule() throws RemoteException{
		super();
		cache = AuctionCache.getInstance();
	}
	
	/**
	 * Searches the database for any ongoing auctions that contain the keyword
	 * @param keyword the keyword that must be contained in the item name
	 * @return A map of AuctionListing types, which contain both the item and auction information
	 * @throws DatabaseConnectionException If an internal error occurs when connecting to the database
	 */
	public Map<Integer,AuctionListing> getAuctionListings(String keyword) throws DatabaseConnectionException, RemoteException{
		AuctionDatabaseConnector database;
		//Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			//Search the database for active auctions matching the keyword (up to 50 results). JOIN is required as the auction table
			//does not store item names
			PreparedStatement searchStmt = database.getNewPreparedStatement("SELECT * FROM auctions JOIN catalogue ON catalogue.itemId = auctions.itemId WHERE catalogue.itemName LIKE ? AND auctions.endTime > ? AND auctions.isClosed = 0 LIMIT 50");
			searchStmt.setString(1, "%" + keyword + "%");
			//Only search auctions that are ongoing
			searchStmt.setLong(2, System.currentTimeMillis()/1000);
			ResultSet searchResult = searchStmt.executeQuery();
			//Load in each result
			Map<Integer,AuctionListing> results = new HashMap<>();
			while(searchResult.next()) {
				AuctionListing listing = AuctionObjectLoader.loadListing(searchResult);
				results.put(listing.getAuction().getAuctionId(),listing);
			}
			database.closeConnection();
			return results;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
			} catch (SQLException e1) {throw new DatabaseConnectionException("Error connecting to the database");}//Not much to do
			throw new DatabaseConnectionException("Error connecting to the database");
		}
	}
	
	/**
	 * Gets an auction listing. This method allows for an existing connection to be used as part of a transaction
	 * @param auctionId The ID of the auction to get
	 * @param database An optional AuctionDatabaseConnection to use. This connection will NOT be closed
	 * @return An auction listing object, or null if the auction does not exist
	 * @throws DatabaseConnectionException If an error occurs connecting to the database
	 */
	protected AuctionListing getAuctionInternal(int auctionId, AuctionDatabaseConnector db) throws DatabaseConnectionException{
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
		try {
			PreparedStatement lookupStmt = database.getNewPreparedStatement("SELECT * FROM auctions JOIN catalogue ON catalogue.itemId = auctions.itemId WHERE auctions.auctionId = ?");
			lookupStmt.setInt(1, auctionId);
			ResultSet lookupResult = lookupStmt.executeQuery();
			boolean isResult = lookupResult.next();
			if(isResult == false) {
				return null;
			}
			AuctionListing finalResult = AuctionObjectLoader.loadListing(lookupResult);
			//Only close the connection if we created it
			if(db == null) {
				database.closeConnection();
			}
			return finalResult;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
			} catch (SQLException e1) {throw new DatabaseConnectionException("Error connecting to the database");}//Not much to do
			throw new DatabaseConnectionException("Error connecting to the database");
		}
	}
	
	protected AuctionListing getAuctionInternal(int auctionId) throws DatabaseConnectionException {
		return this.getAuctionInternal(auctionId, null);
	}
	
	public AuctionListing getAuction(int auctionId) throws DatabaseConnectionException, RemoteException{
		return cache.getAuction(auctionId,this);
	}
	
}
