package proxyHelper;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auctionClasses.Auction;
import auctionClasses.Bid;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;

public class ProxyHelper {
	/**
	 * Verifies that a token represents a logged-in user
	 * @param token
	 * @return a boolean valid/invalid
	 */
	public static boolean verifyToken(Token token) {
		return token != null;
	}
	
	/**
	 * Verifies that a token represents the user who won an auction
	 * @param token the token
	 * @param auction The action to check against
	 * @return a boolean value representing if the user won the auction and should be allowed to pay
	 */
	public static boolean verifyTokenMatching(Token token, Auction auction) {
		System.out.println("Proxy matching invoked");
		CatalogueRemoteInterface catalogue;
        try {
        	Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			catalogue = ((CatalogueRemoteInterface)registry.lookup("catalogue"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return false;
		}
		Auction freshAuction;
		try {
			freshAuction = catalogue.getAuction(auction.getAuctionId(),token).getAuction();
		} catch (RemoteException e) {
			return false;
		}
		System.out.println("Proxy matching: gotten auction class "+freshAuction.toString()+", "+ freshAuction.isClosed());
		//If the auction is ongoing, not allowed to pay
		if(freshAuction.isClosed() == false) {
			return false;
		}
		System.out.println("Proxy matching: checking for a match");
		//Get the bid that won
		Bid winningBid = freshAuction.getCurrentBid();
		if(winningBid != null && winningBid.getUsername().equals(token.getUsername())) {
			return true;
		}
		return false;
	}
}
