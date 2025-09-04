package catalogue;

import databaseRemoteInterfaces.DatabaseCatalogueRemoteInterface;
import exceptions.DatabaseConnectionException;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.Map;

import auctionClasses.AuctionListing;
public class CatalogueModule {
	private DatabaseCatalogueRemoteInterface database;
	public CatalogueModule() {
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			database = ((DatabaseCatalogueRemoteInterface)registry.lookup("catalogueDatabase"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
	}
	public Map<Integer, AuctionListing> search(String word){
		try {

			return database.getAuctionListings(word);
		} catch (DatabaseConnectionException | RemoteException e) {
			e.printStackTrace();
		}
		return null;

	}
	public AuctionListing getAuction(int auctionId) {
		try {
			return database.getAuction(auctionId);
		} catch (DatabaseConnectionException | RemoteException e) {
			e.printStackTrace();
			return null;
		}
	}	
}