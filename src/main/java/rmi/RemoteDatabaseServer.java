package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import database.AccountDatabaseModule;
import database.AuctionDatabaseModule;
import database.CatalogueDatabaseModule;
import database.PaymentDatabaseModule;

public class RemoteDatabaseServer implements ServletContextListener{
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//Authentication
		System.out.println("Running bind process");
		try {
			AccountDatabaseModule authRemote = new AccountDatabaseModule();
			Registry registry = LocateRegistry.createRegistry(1019);
			registry.bind("authenticationDatabase", authRemote);
			System.out.println("Bound auth database");
			//Catalogue
			CatalogueDatabaseModule catalogueRemote = new CatalogueDatabaseModule();
			registry.bind("catalogueDatabase", catalogueRemote);
			System.out.println("Bound catalogue database");
			//Auction
			AuctionDatabaseModule auctionRemote = new AuctionDatabaseModule();
			registry.bind("auctionDatabase", auctionRemote);
			System.out.println("Bound auction database");
			//Payment & Shipping
			PaymentDatabaseModule paymentRemote = new PaymentDatabaseModule();
			registry.bind("paymentDatabase", paymentRemote);
			System.out.println("Bound payment database");
		}catch(Exception e) {
			System.out.println("Error binding databases");
			e.printStackTrace();
		}
	}
}