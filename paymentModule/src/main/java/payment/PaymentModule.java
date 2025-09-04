package payment;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionClasses.Item;
import authClasses.Token;
import databaseRemoteInterfaces.DatabasePaymentRemoteInterface;
import exceptions.PaymentException;
import paymentClasses.AbstractCreditCard;
import paymentClasses.Receipt;
import paymentClasses.ShippingType;
import paymentRemoteInterfaces.PaymentRemoteInterface;
import shipping.ShippingModule;

public class PaymentModule extends UnicastRemoteObject implements PaymentRemoteInterface{
	
	private static final long serialVersionUID = -6848222101739423891L;

	public PaymentModule() throws RemoteException {
		super();
	}
	public double getTotalPrice(Auction auction, Item item, Bid bid, ShippingType shippingType) throws RemoteException{
		ShippingModule shippingModule= new ShippingModule();
		shippingModule.setShippingType(shippingType);
		double price = shippingModule.getItemPrice(bid) + shippingModule.getShippingPrice(item, bid);
		return price;
		
	}
	public boolean payWithCreditCard(AuctionListing listing,Bid bid, Token token, AbstractCreditCard cc,ShippingType shippingType) throws RemoteException{
		FinalizeTransaction finalize= new FinalizeTransaction();
		ShippingModule ship=new ShippingModule();
		ship.setShippingType(shippingType);
		try {
			System.out.println("Calling finalize");
			finalize.finalizeTransaction(listing,bid, token, cc,ship);
			return true;
		} catch (PaymentException e) {
			return false;
		}
	}

	public Receipt getReceipt(int auctionId, String username) throws RemoteException{
		DatabasePaymentRemoteInterface payment;
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			payment = ((DatabasePaymentRemoteInterface)registry.lookup("paymentDatabase"));
			return payment.getReceipt(auctionId, username);
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return null;
		}
	}
	
	public Receipt getReceipt(Auction auction,String username) throws RemoteException{
		return this.getReceipt(auction.getAuctionId(), username);
	}

}