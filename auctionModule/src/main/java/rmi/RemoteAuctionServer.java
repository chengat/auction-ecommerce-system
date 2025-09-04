package rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RemoteAuctionServer {

	public static void main(String[] args) throws RemoteException, AlreadyBoundException, InterruptedException {
		AuctionModuleFacade auctionRemote = new AuctionModuleFacade();
		Registry registry = LocateRegistry.createRegistry(1019);
		registry.bind("auction", auctionRemote);
		System.out.println("Bound auction server");
		CatalogueRemote catalogueRemote = new CatalogueRemote();
		registry.bind("catalogue", catalogueRemote);
		System.out.println("Bound catalogue server");
		System.out.println("Starting loop");
		while(true) {
			Thread.sleep(1000);
		}
	}
}
