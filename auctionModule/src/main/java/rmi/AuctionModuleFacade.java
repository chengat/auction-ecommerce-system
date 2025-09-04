package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import auction.AuctionModule;
import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionRemoteInterfaces.AuctionRemoteInterface;
import authClasses.User;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;

public class AuctionModuleFacade extends UnicastRemoteObject implements AuctionRemoteInterface{
	private static final long serialVersionUID = 2460250910957615250L;
	private AuctionModule module;
	public AuctionModuleFacade() throws RemoteException{
		super();
		try {
			module = AuctionModule.getInstance();
		} catch (DatabaseConnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateAuctionPrice(Auction auction, double newPrice) throws DatabaseConnectionException, RemoteException {
		module.updateAuctionPrice(auction, newPrice);
		
	}

	@Override
	public Bid placeBid(User user, int auctionId, double price)
			throws DatabaseConnectionException, AuctionBidException,RemoteException {
		return module.placeBid(user, auctionId, price);
	}

	@Override
	public void closeAuction(Auction auction) throws DatabaseConnectionException,RemoteException {
		module.closeAuction(auction);
		
	}

	@Override
	public AuctionListing createAuction(AuctionListing toCreate) throws DatabaseConnectionException,RemoteException {
		return module.createAuction(toCreate);
	}

	@Override
	public AuctionListing loadAndRegisterAuction(int auctionId) throws DatabaseConnectionException,RemoteException {
		return module.loadAndRegisterAuction(auctionId);
	}

	@Override
	public void delistAuction(Auction auction) throws RemoteException {
		module.delistAuction(auction);
		
	}

}
