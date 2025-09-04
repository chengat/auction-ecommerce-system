package auctionRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import auctionClasses.AuctionListing;
import authClasses.Token;

public interface CatalogueRemoteInterface extends Remote{

	public Map<Integer, AuctionListing> search(String word, Token t) throws RemoteException;
	public AuctionListing getAuction(int auctionId, Token t) throws RemoteException;
}