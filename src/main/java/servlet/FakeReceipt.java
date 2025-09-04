package servlet;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import auctionClasses.AuctionListing;
import auctionClasses.AuctionListingBuilder;
import auctionClasses.Bid;
import auctionClasses.Item;
import auctionClasses.Auction.AuctionType;
import auctionRemoteInterfaces.AuctionRemoteInterface;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;
import paymentClasses.ShippingType;

/**
 * Places a fake receipt on the session to test checkout
 */
public class FakeReceipt extends HttpServlet {

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
		System.out.println("Receipt faker invoked");
		AuctionRemoteInterface auctionService;
		AuthenticationRemoteInterface authService;
        try {
			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			auctionService = ((AuctionRemoteInterface)registry.lookup("auction"));
			Registry authRegistry = LocateRegistry.getRegistry("authcontainer",1019);
			authService = ((AuthenticationRemoteInterface)authRegistry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
		Item forwardItem = new Item(0, "Junk", "A mix of junk for hoarders who want to grow their collection.", 50.0, 100.0);
        //Forward auction
        AuctionListingBuilder forwardAuction = new AuctionListingBuilder(AuctionType.FORWARD);
        forwardAuction.setItem(forwardItem);
        forwardAuction.setStartingPrice(20.0);
        forwardAuction.setAuctionClose(TimeUnit.MINUTES.toMillis(1) + System.currentTimeMillis());
        try {
			AuctionListing forward = auctionService.createAuction(forwardAuction.build());
			Bid bid = auctionService.placeBid(authService.getUserInfo(authService.checkToken(request.getSession().getAttribute("token").toString())), forward.getAuction().getAuctionId(), 75);
			HttpSession s = request.getSession();
			s.setAttribute("Bid", bid);
			s.setAttribute("AuctionListing", forward);
			s.setAttribute("ShippingType", ShippingType.EXPEDITED.toString());
			response.getWriter().print("Auction bid written");
        } catch (RemoteException | DatabaseConnectionException e) {
			e.printStackTrace();
		} catch (AuctionBidException e) {
			e.printStackTrace();
		}
	}
}
