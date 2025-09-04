package rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import payment.PaymentModule;

public class RemotePaymentServer {

	public static void main(String[] args) throws RemoteException, AlreadyBoundException, InterruptedException {
		PaymentModule paymentRemote = new PaymentModule();
		Registry registry = LocateRegistry.createRegistry(1019);
		registry.bind("payment", paymentRemote);
		System.out.println("Bound payment server");
		System.out.println("Starting loop");
		while(true) {
			Thread.sleep(1000);
		}
	}
}
