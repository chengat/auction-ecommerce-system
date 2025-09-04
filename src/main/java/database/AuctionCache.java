package database;

import java.util.HashMap;
import java.util.Map;

import auctionClasses.AuctionListing;
import exceptions.DatabaseConnectionException;

public class AuctionCache {
	private static AuctionCache instance;
	private Map<Integer,AuctionListing> auctionMap;
	
	private AuctionCache() {
		this.auctionMap = new HashMap<>();
	}
	
	public static AuctionCache getInstance() {
		if(instance == null) {
			instance = new AuctionCache();
		}
		return instance;
	}
	
	public AuctionListing getAuction(int auctionId, CatalogueDatabaseModule dbModule) throws DatabaseConnectionException {
		System.out.println("Cache request for ID " + auctionId);
		if(auctionMap.containsKey(auctionId)) {
			System.out.println("Returning auction " + auctionMap.get(auctionId).getAuction());
			return auctionMap.get(auctionId);
		}else {
			AuctionListing listing = dbModule.getAuctionInternal(auctionId);
			if(listing == null) {
				return null;
			}
			auctionMap.put(auctionId, listing);
			return listing;
		}
	}
	
	public void registerAuction(AuctionListing listing) {
		System.out.println("Caching with ID " + listing.getAuction().getAuctionId());
		auctionMap.put(listing.getAuction().getAuctionId(), listing);
	}
}
