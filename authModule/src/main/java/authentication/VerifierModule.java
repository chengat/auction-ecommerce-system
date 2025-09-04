package authentication;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import authClasses.Token;
import authClasses.User;
import databaseRemoteInterfaces.DatabaseAuthRemoteInterface;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;

public class VerifierModule {
	
	DatabaseAuthRemoteInterface dbModule;
	public VerifierModule() {
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			dbModule = ((DatabaseAuthRemoteInterface)registry.lookup("authenticationDatabase"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
	}
	
	/**
	 * Takes a token string and returns the full Token object if the token is valid, or null otherwise
	 * @param t the token string from a client
	 * @return a Token object if valid, null otherwise
	 */
	Token checkToken(String t) {
		try {
			Token token = dbModule.getToken(t);
			return token;
		} catch (DatabaseConnectionException | DatabaseCredentialException | RemoteException e) {
			return null;
		}
	}
	
	/**
	 * Takes a token and returns the user profile tied to the token, or null if the token has been revoked since being checked
	 * @param t a Token object
	 * @return a User, or null if the token is now revoked.
	 */
	User getUserFromToken(Token t) {
		try {
			//Refresh the token. If it has been revoked, it will throw an error
			Token refreshedToken = dbModule.getToken(t.getToken());
			return dbModule.getUser(refreshedToken);
		} catch (DatabaseConnectionException | DatabaseCredentialException | RemoteException e) {
			return null;
		}
	}
	
	//Overloaded method that procures the token from a string.
	//TODO: If time permits, optimize as the overload call results in 2 token verifications
	User getUserFromToken(String token) {
		Token t = this.checkToken(token);
		if(t != null) {
			return this.getUserFromToken(t);
		}
		return null;
	}
}
