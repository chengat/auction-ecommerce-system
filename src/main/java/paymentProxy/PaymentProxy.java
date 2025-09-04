package paymentProxy;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionClasses.Item;
import authClasses.Token;
import paymentClasses.AbstractCreditCard;
import paymentClasses.ShippingType;
import paymentRemoteInterfaces.PaymentRemoteInterface;
import proxyHelper.ProxyHelper;

public class PaymentProxy {
	PaymentRemoteInterface payment;
	Token token;
	
	public PaymentProxy(Token token) {
		try {
			Registry paymentRegistry = LocateRegistry.getRegistry("paymentcontainer",1019);
			payment = ((PaymentRemoteInterface)paymentRegistry.lookup("payment"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
		this.token = token;
	}
	
	public double getTotalPrice(Auction auction, Item item, Bid bid, ShippingType shippingType) {
		if(ProxyHelper.verifyTokenMatching(token, auction)) {
			try {
				return payment.getTotalPrice(auction, item, bid, shippingType);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0.0;
	}
	public boolean payWithCreditCard(AuctionListing listing,Bid bid, AbstractCreditCard cc,ShippingType shippingType) {
		if(ProxyHelper.verifyTokenMatching(token, listing.getAuction())) {
			try {
				return payment.payWithCreditCard(listing, bid, token, cc, shippingType);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}else {
			return false;
		}
	}
}
