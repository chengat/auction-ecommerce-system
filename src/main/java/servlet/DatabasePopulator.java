package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import auctionClasses.AuctionListingBuilder;
import auctionClasses.Item;
import auctionClasses.Auction.AuctionType;
import auctionClasses.AuctionListing;
import auctionRemoteInterfaces.AuctionRemoteInterface;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.TimeUnit;

public class DatabasePopulator extends HttpServlet {
	private static final long serialVersionUID = -896804989577698584L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
		System.out.println("Populator activated");
        response.setContentType("text/html");
        PrintWriter output = response.getWriter();
        AuctionRemoteInterface auctionService;
        output.println("Connecting to auction module");
        try {
			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			auctionService = ((AuctionRemoteInterface)registry.lookup("auction"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        output.println("Generating 2 new database entries");
        Item forwardItem = new Item(0, "Junk", "A mix of junk for hoarders who want to grow their collection.", 50.0, 100.0);
        Item DutchItem = new Item(0, "Toys", "Rare toys for young children.", 20.0, 40.0);
        //Forward auction
        AuctionListingBuilder forwardAuction = new AuctionListingBuilder(AuctionType.FORWARD);
        forwardAuction.setItem(forwardItem);
        forwardAuction.setStartingPrice(20.0);
        forwardAuction.setAuctionClose(TimeUnit.MINUTES.toMillis(1) + System.currentTimeMillis());//Close in 2 minutes
        //Dutch
        AuctionListingBuilder dutchAuction = new AuctionListingBuilder(AuctionType.DUTCH);
        dutchAuction.setItem(DutchItem);
        dutchAuction.setStartingPrice(300.0);
        dutchAuction.setReservePrice(200.0);
        dutchAuction.setDutchIncrement(15.0);
        dutchAuction.setAuctionClose(TimeUnit.MINUTES.toMillis(2) + System.currentTimeMillis());//Close in 2 minutes
        try {
			AuctionListing forward = auctionService.createAuction(forwardAuction.build());
			output.println("Forward auction built & started:" + forward.toString());
			AuctionListing dutch = auctionService.createAuction(dutchAuction.build());
			output.println("Dutch auction built & started:" + dutch.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}