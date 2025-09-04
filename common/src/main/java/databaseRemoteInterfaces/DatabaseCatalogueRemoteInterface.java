package databaseRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import auctionClasses.AuctionListing;
import exceptions.DatabaseConnectionException;

public interface DatabaseCatalogueRemoteInterface extends Remote{
	public Map<Integer,AuctionListing> getAuctionListings(String keyword) throws DatabaseConnectionException, RemoteException;
	
	public AuctionListing getAuction(int auctionId) throws DatabaseConnectionException, RemoteException;
}
