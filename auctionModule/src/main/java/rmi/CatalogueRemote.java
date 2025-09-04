package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import auctionClasses.AuctionListing;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;
import proxies.CatalogueProxy;

public class CatalogueRemote extends UnicastRemoteObject implements CatalogueRemoteInterface{

	private static final long serialVersionUID = -8471844408213845727L;

	protected CatalogueRemote() throws RemoteException {
		super();
	}

	@Override
	public Map<Integer, AuctionListing> search(String word, Token t) throws RemoteException{
		return new CatalogueProxy(t).search(word);
	}

	@Override
	public AuctionListing getAuction(int auctionId, Token t) throws RemoteException{
		return new CatalogueProxy(t).getAuction(auctionId);
	}

}
