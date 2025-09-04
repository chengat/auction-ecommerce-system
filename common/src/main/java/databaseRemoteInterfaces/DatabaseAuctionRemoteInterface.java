package databaseRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import authClasses.User;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;

public interface DatabaseAuctionRemoteInterface extends Remote {
	public Bid placeBid(User user, int auctionId, double price) throws DatabaseConnectionException, AuctionBidException, RemoteException;
	
	/**
	 * Gets a Bid
	 * @param bidId the ID of the Bid to get
	 * @return A Bid if it exists, null otherwise
	 * @throws DatabaseConnectionException if an error occurs connecting to the database
	 * @throws RemoteException 
	 */
	public Bid getBid(int bidId) throws DatabaseConnectionException, RemoteException;
	
	/**
	 * Attempts to close an auction by setting its end time to the current time. This will prevent further bids but WILL NOT notify anyone
	 * @param auction The auction to close
	 * @throws DatabaseConnectionException  if an error occurs connecting to the database
	 * @throws RemoteException 
	 */
	public void closeAuction(Auction auction) throws DatabaseConnectionException, RemoteException;
	
	/**
	 * Changes the starting price of an auction to reflect a price decrement
	 * @param auctionId The auctionID
	 * @param newStartingPrice the new price
	 * @throws DatabaseConnectionException If an error occurs connecting to the database
	 * @throws RemoteException 
	 */
	public void changeDutchStartingPrice(int auctionId, double newStartingPrice) throws DatabaseConnectionException, RemoteException;
	
	/**
	 * Changes the starting price of an auction to reflect a price decrement
	 * @param auction the auction
	 * @param newStartingPrice the new price
	 * @throws DatabaseConnectionException If an error occurs connecting to the database
	 * @throws RemoteException 
	 */
	public void changeDutchStartingPrice(Auction auction, double newStartingPrice) throws DatabaseConnectionException, RemoteException;
	
	/**
	 * debug method to create a new auction and item. The auctionId and itemId will be ignored and generated on auction creation.
	 * @param toCreate The auction information for creation
	 * @return an AuctionListing object with the correct IDs if successful, null otherwise
	 * @throws DatabaseConnectionException 
	 * @throws RemoteException 
	 */
	public AuctionListing createAuction(AuctionListing toCreate) throws DatabaseConnectionException, RemoteException;
}
