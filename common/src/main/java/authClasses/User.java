package authClasses;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import databaseRemoteInterfaces.DatabaseAuthRemoteInterface;

public class User implements Serializable{
	private static final long serialVersionUID = -8979192074585712022L;
	private String username;
	private String nameFirst;
	private String nameLast;
	public User(String username, String nameFirst, String nameLast) {
		super();
		this.username = username;
		this.nameFirst = nameFirst;
		this.nameLast = nameLast;
	}
	public String getNameFirst() {
		return nameFirst;
	}

	public String getNameLast() {
		return nameLast;
	}

	public String getUsername() {
		return username;
	}
	
	public String toString() {
		return this.getUsername() + ":" + this.getNameFirst() + " " + this.getNameLast();
	}
	
	public Address getAddress() {
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			DatabaseAuthRemoteInterface dbModule = ((DatabaseAuthRemoteInterface)registry.lookup("authenticationDatabase"));
			return dbModule.getUserAddress(this);
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return null;
		}
	}
}