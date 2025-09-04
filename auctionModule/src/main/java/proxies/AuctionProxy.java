package proxies;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auction.AuctionModule;
import auctionClasses.Bid;
import authClasses.Token;
import authClasses.User;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;
import proxyHelper.ProxyHelper;

public class AuctionProxy {
	AuctionModule instance;
	Token token;
	private AuthenticationRemoteInterface authModule;
	public AuctionProxy(Token token) throws DatabaseConnectionException {
		instance = AuctionModule.getInstance();
		this.token = token;
		try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			authModule = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
	}
	
    /**
     * Attempts to place a bid. Bids are verified and placed atomically.
     * 
     * @param user      The user placing the bid
     * @param auctionId The auction on which to bid
     * @param price     The price the user is willing to pay
     * @return A Bid object if the bid is placed
     * @throws DatabaseConnectionException If there is a database connection issue
     * @throws AuctionBidException         If the bid is invalid. A message will be
     *                                     included
     * @throws RemoteException 
     */
    public Bid placeBid(int auctionId, double price)
            throws DatabaseConnectionException, AuctionBidException, RemoteException {
    	if(ProxyHelper.verifyToken(token)) {
    		User user = authModule.getUserInfo(token);
    		return instance.placeBid(user, auctionId, price);
    	}else {
    		return null;
    	}
    }
}