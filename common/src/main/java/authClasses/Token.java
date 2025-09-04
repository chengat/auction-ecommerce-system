package authClasses;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import databaseRemoteInterfaces.DatabaseAuthRemoteInterface;

public class Token implements Serializable{
	private static final long serialVersionUID = -2817270843094753314L;
	private String username;
	private String token;
	private Object socketConnection;
	private int selectedAuction;
	private DatabaseAuthRemoteInterface dbModule;
	
	protected Token(String username, String token) {
		this.username = username;
		this.token = token;
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			dbModule = ((DatabaseAuthRemoteInterface)registry.lookup("authenticationDatabase"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
	}
	
	public Object getSocketConnection() {
		return socketConnection;
	}

	public void setSocketConnection(Object socketConnection) {
		this.socketConnection = socketConnection;
		try {
			dbModule.updateToken(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public int getSelectedAuction() {
		return selectedAuction;
	}

	public void setSelectedAuction(int selectedAuction) {
		if(this.selectedAuction == 0) {
			this.selectedAuction = selectedAuction;
			try {
				dbModule.updateToken(this);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}
	
	public String toString() {
		return this.getUsername() + ":" + this.getSelectedAuction() + ":::" + this.getToken() + " on auction " + this.getSelectedAuction();
	}
	
	public static class TokenBuilder{
		public static Token buildToken(String u, String t) {
			return new Token(u,t);
		}
	}
}
