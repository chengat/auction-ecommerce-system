package payment;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import authClasses.Token;
import authClasses.User;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import databaseRemoteInterfaces.DatabasePaymentRemoteInterface;
import exceptions.DatabaseConnectionException;
import exceptions.PaymentException;
import paymentClasses.AbstractCreditCard;
import shipping.ShippingModule;

public class FinalizeTransaction{
	DatabasePaymentRemoteInterface payment;
	public FinalizeTransaction() {
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			payment = ((DatabasePaymentRemoteInterface)registry.lookup("paymentDatabase"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
	}
	
	//Protected so that the PaymentModule must be used
	protected void finalizeTransaction(AuctionListing listing, Bid bid, Token token, AbstractCreditCard cc,ShippingModule ship) throws PaymentException {
		AuthenticationRemoteInterface sessionManager;
        try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			sessionManager = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			throw new PaymentException("Registry issue");
		}
		try {
			PaymentModule price=new PaymentModule();
			User user = sessionManager.getUserInfo(token);
			double fprice= price.getTotalPrice(listing.getAuction(),listing.getItem(), bid ,ship.getShippingType());
			//"Charge" the credit card
			if(cc.processPayment(fprice)) {
				payment.createReceipt(listing.getAuction(), bid, user, fprice, ship.getShippingType());
				payment.attachCreditCard(payment.getReceipt(listing.getAuction().getAuctionId(), user.getUsername()), cc);
			}else {
				throw new PaymentException("Credit card processor rejected payment");
			}
		} catch (DatabaseConnectionException | RemoteException e) {
			e.printStackTrace();
			throw new PaymentException("Internal Error");
		}
	}
}
