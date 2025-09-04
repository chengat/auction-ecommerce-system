package proxies;

import java.util.Map;

import auctionClasses.AuctionListing;
import authClasses.Token;
import catalogue.CatalogueModule;
import proxyHelper.ProxyHelper;

public class CatalogueProxy extends CatalogueModule {
	Token token;
	CatalogueModule catalogue;
	
	public CatalogueProxy(Token token) {
		this.token = token;
		this.catalogue = new CatalogueModule();
	}
	
	@Override
	public Map<Integer, AuctionListing> search(String word){
		if(ProxyHelper.verifyToken(token)) {
			return catalogue.search(word);
		}else {
			return null;
		}
	}
	
	@Override
	public AuctionListing getAuction(int auctionId) {
		if (ProxyHelper.verifyToken(token)) {
			return catalogue.getAuction(auctionId);
		}else {
			return null;
		}
	}
}