package auction;

import authClasses.User;
import catalogue.CatalogueModule;
import databaseRemoteInterfaces.DatabaseAuctionRemoteInterface;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionClasses.DutchAuction;
import auctionClasses.Auction.AuctionType;

public class AuctionModule{
    private static AuctionModule instance;
    private AuctionScheduler scheduler;
    private DatabaseAuctionRemoteInterface dbConnector;
    private ConcurrentMap<Integer, Auction> activeAuctions;

    private AuctionModule() throws DatabaseConnectionException{
        scheduler = new AuctionScheduler(this);
        activeAuctions = new ConcurrentHashMap<>();
        try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			dbConnector = ((DatabaseAuctionRemoteInterface)registry.lookup("auctionDatabase"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseConnectionException("An error occurred when connecting to the database");
        }
    }

    public static synchronized AuctionModule getInstance() throws DatabaseConnectionException {
        if (instance == null) {
            instance = new AuctionModule();
        }
        return instance;
    }

    // Add a method to update auction price
    public void updateAuctionPrice(Auction auction, double newPrice) throws DatabaseConnectionException, RemoteException {
        dbConnector.changeDutchStartingPrice(auction, newPrice);
    }

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
     * @throws RemoteException 
     */
    public Bid placeBid(User user, int auctionId, double price)
            throws DatabaseConnectionException, AuctionBidException, RemoteException {

		Auction auction = new CatalogueModule().getAuction(auctionId).getAuction();
		Bid currentTopBid = auction.getCurrentBid();
		if(currentTopBid != null && price <= currentTopBid.getBidPrice() || price < auction.getStartingPrice()) {
			throw new AuctionBidException("Bid is too low");
		}
        Bid bid = dbConnector.placeBid(user, auctionId, price);

		if (auction != null) {
		    synchronized (auction) { // Ensure thread safety
		        auction.setCurrentBid(bid);

		        // If it's a Dutch Auction, close it immediately upon a successful bid
		        if (auction instanceof DutchAuction) {
		            auction.close();
		        }
		    }
		}

		return bid;
    }

    /**
     * Attempts to close an auction by setting its end time to the current time.
     * This prevents further bids and finalizes the auction.
     * 
     * @param auction The auction to close
     * @throws DatabaseConnectionException If there is a database connection issue
     * @throws RemoteException 
     */
    public void closeAuction(Auction auction) throws DatabaseConnectionException, RemoteException {
        auction.close();
		activeAuctions.remove(auction.getAuctionId());
		System.out.println("Closing " + auction.toString());
		// TODO: Notify users or perform additional actions as needed
    }
    
    public void delistAuction(Auction auction) throws RemoteException{
    	System.out.println("Delisting " + auction.toString());
    	activeAuctions.remove(auction.getAuctionId());
    }

    /**
     * Creates a new auction and item.
     * 
     * @param toCreate The auction information for creation
     * @return an AuctionListing object with the correct IDs if successful, null
     *         otherwise
     * @throws DatabaseConnectionException
     * @throws RemoteException 
     */
    public AuctionListing createAuction(AuctionListing toCreate) throws DatabaseConnectionException, RemoteException {
    	//Calculate closing time for a dutch auction
    	if(toCreate.getAuction().getAuctionType() == AuctionType.DUTCH) {
    		DutchAuction dutchAuction = ((DutchAuction)toCreate.getAuction());
    		int numberOfIncrements = (int) Math.round((dutchAuction.getStartingPrice() - dutchAuction.getReservePrice()) / dutchAuction.getIncrement()) + 1;
    		long closingTime = System.currentTimeMillis() + (numberOfIncrements * AuctionScheduler.incrementMillisecUnit);
    		dutchAuction.setAuctionClose(closingTime);
    	}
        AuctionListing createdAuction = dbConnector.createAuction(toCreate);
        System.out.println("Created: "+createdAuction.getAuction());
        activeAuctions.put(createdAuction.getAuction().getAuctionId(), createdAuction.getAuction());
        scheduler.scheduleAuction(createdAuction.getAuction());
        return createdAuction;
    }

    /**
     * Loads an auction from the database and adds it to active auctions if it's
     * still active.
     * 
     * @param auctionId The ID of the auction to load
     * @return The loaded AuctionListing or null if not found or closed
     * @throws DatabaseConnectionException
     */
    public AuctionListing loadAndRegisterAuction(int auctionId) throws DatabaseConnectionException {
        AuctionListing listing = new CatalogueModule().getAuction(auctionId);;
		if (listing == null || listing.getAuction().isClosed()) {
		    return null;
		}

		activeAuctions.put(auctionId, listing.getAuction());
		scheduler.scheduleAuction(listing.getAuction());

		return listing;
    }
}
