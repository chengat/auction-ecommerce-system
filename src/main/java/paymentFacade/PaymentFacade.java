package paymentFacade;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auctionClasses.*;
import authClasses.*;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import paymentClasses.*;
import paymentProxy.PaymentProxy;
import paymentRemoteInterfaces.PaymentRemoteInterface;
import shipping.ShippingModule;
public class PaymentFacade {
	Token token;
	AuctionListing auction;
	CardFactory factory=new CardFactory();
	PaymentProxy proxy;
	ShippingType ship;
	Bid bid;
	AbstractCreditCard cr;
	Boolean pay;
	PaymentData data;
	public PaymentFacade(PaymentData data) {

		this.data=data;
	}

	public boolean processpayment() {
		proxy=new PaymentProxy(data.gettoken());
		if (data.getcredit().processPayment(data.getbid().getBidPrice())==true) {
			pay=proxy.payWithCreditCard(data.getauction(), data.getbid(), data.getcredit(), data.getship());
			return pay;
		}
		return false;

	}
	public Receipt getreceipt() throws RemoteException {
		PaymentRemoteInterface paymentService;
		AuthenticationRemoteInterface authService;
        try {
			Registry registry = LocateRegistry.getRegistry("paymentcontainer",1019);
			paymentService = ((PaymentRemoteInterface)registry.lookup("payment"));
			Registry authRegistry = LocateRegistry.getRegistry("authcontainer",1019);
			authService = ((AuthenticationRemoteInterface)authRegistry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return null;
		}
		return paymentService.getReceipt(data.getauction().getAuction(), data.gettoken().getUsername());
		
	}
	public int shippingdays() {
		ShippingModule ship = new ShippingModule();
		ship.setShippingType(data.getship());
		
		return ship.getShippingDays(null, bid);
		
	}
}