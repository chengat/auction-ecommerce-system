package paymentRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionClasses.Item;
import authClasses.Token;
import paymentClasses.AbstractCreditCard;
import paymentClasses.Receipt;
import paymentClasses.ShippingType;

public interface PaymentRemoteInterface extends Remote {
	public double getTotalPrice(Auction auction, Item item, Bid bid, ShippingType shippingType) throws RemoteException;
	
	public boolean payWithCreditCard(AuctionListing listing,Bid bid, Token token, AbstractCreditCard cc,ShippingType shippingType) throws RemoteException;

	public Receipt getReceipt(int auctionId, String username) throws RemoteException;
	
	public Receipt getReceipt(Auction auction,String username) throws RemoteException;
}