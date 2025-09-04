package rmi;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import authentication.SessionManagementModule;

public class RemoteAuthenticationServer {

	public static void main(String[] args) throws AlreadyBoundException, IOException, InterruptedException {
		Registry registry = LocateRegistry.createRegistry(1019);
		SessionManagementModule authRemote = new SessionManagementModule();
		registry.bind("authentication", authRemote);
		System.out.println("Bound authentication server");
		System.out.println("Starting loop");
		while(true) {
			Thread.sleep(1000);
		}
	}
}