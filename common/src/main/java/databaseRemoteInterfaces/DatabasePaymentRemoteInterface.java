package databaseRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import auctionClasses.Auction;
import auctionClasses.Bid;
import authClasses.User;
import exceptions.DatabaseConnectionException;
import paymentClasses.AbstractCreditCard;
import paymentClasses.Receipt;
import paymentClasses.ShippingType;

public interface DatabasePaymentRemoteInterface extends Remote {
	public Receipt createReceipt(int auctionId, int bidId, String username, double amount, ShippingType shippingType) throws DatabaseConnectionException, RemoteException;

	public Receipt createReceipt(Auction auction, Bid bid, User user, double ammount, ShippingType shippingType) throws DatabaseConnectionException, RemoteException;

	public Receipt getReceipt(int auctionId, String username) throws DatabaseConnectionException, RemoteException;

	public Receipt getReceipt(Auction auction, User user) throws DatabaseConnectionException, RemoteException;

	public boolean attachCreditCard(Receipt receipt, AbstractCreditCard creditCard) throws DatabaseConnectionException, RemoteException;
}
