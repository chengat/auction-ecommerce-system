package auctionRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import authClasses.User;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;

public interface AuctionRemoteInterface extends Remote {

    // Add a method to update auction price
    public void updateAuctionPrice(Auction auction, double newPrice) throws DatabaseConnectionException, RemoteException;

    /**
     * Attempts to place a bid. Bids are verified and placed atomically.
     * 
     * @param user      The user placing the bid
     * @param auctionId The auction on which to bid
     * @param price     The price the user is willing to pay
     * @return A Bid object if the bid is placed
     * @throws DatabaseConnectionException If there is a database connection issue
     * @throws AuctionBidException         If the bid is invalid. A message will be
     *                                     included
     */
    public Bid placeBid(User user, int auctionId, double price)
            throws DatabaseConnectionException, AuctionBidException, RemoteException;

    /**
     * Attempts to close an auction by setting its end time to the current time.
     * This prevents further bids and finalizes the auction.
     * 
     * @param auction The auction to close
     * @throws DatabaseConnectionException If there is a database connection issue
     */
    public void closeAuction(Auction auction) throws DatabaseConnectionException,RemoteException;

    /**
     * Creates a new auction and item.
     * 
     * @param toCreate The auction information for creation
     * @return an AuctionListing object with the correct IDs if successful, null
     *         otherwise
     * @throws DatabaseConnectionException
     */
    public AuctionListing createAuction(AuctionListing toCreate) throws DatabaseConnectionException,RemoteException;

    /**
     * Loads an auction from the database and adds it to active auctions if it's
     * still active.
     * 
     * @param auctionId The ID of the auction to load
     * @return The loaded AuctionListing or null if not found or closed
     * @throws DatabaseConnectionException
     */
    public AuctionListing loadAndRegisterAuction(int auctionId) throws DatabaseConnectionException,RemoteException;
    
    public void delistAuction(Auction auction) throws RemoteException;
}
