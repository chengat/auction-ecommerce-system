package authentication;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import authClasses.Address;
import authClasses.SignUpUser;
import authClasses.Token;
import authClasses.User;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import databaseRemoteInterfaces.DatabaseAuthRemoteInterface;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;

public class SessionManagementModule extends UnicastRemoteObject implements AuthenticationRemoteInterface{
	private static final long serialVersionUID = -7129761489958182529L;
	
	private DatabaseAuthRemoteInterface authDatabase;
	public SessionManagementModule() throws RemoteException{
		super();
		try {
			Registry registry = LocateRegistry.getRegistry("webcontainer",1019);
			authDatabase = ((DatabaseAuthRemoteInterface)registry.lookup("authenticationDatabase"));
			System.out.println("Gotten auth db:" + authDatabase);
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
		}
	}
	
	/**
	 * Attempts to sign a user up, returns a token to represent the new session if successful
	 * @param user a SignUpUser Object. This Object is used to hold the information of a potential user without interfering with the standard User class.
	 * @param password
	 * @param address an Address object
	 * @return a Token if the login is successful, otherwise an error will be thrown
	 * @throws DatabaseConnectionException internal error connecting to the database
	 * @throws DatabaseCredentialException if the username or password are too short/long, or the username is already in use. This error should be displayed to the user
	 * @throws RemoteException 
	 */
	public Token signUp(SignUpUser user, String password, Address address) throws DatabaseConnectionException, DatabaseCredentialException, RemoteException{
		if(user.getUsername().length() < 5 || user.getUsername().length() > 255 || password.length() < 5 || password.length() > 255) {
			throw new DatabaseCredentialException("Username and password must be between 5 and 255 characters");
		}
		return authDatabase.createUser(user, address, password);
	}
	
	/**
	 * Attempts to sign a user in. Returns a token to represent the new session if successful.
	 * @param username
	 * @param password
	 * @return a Token if the login is successful, otherwise an error will be thrown
	 * @throws DatabaseConnectionException internal error connecting to the database
	 * @throws DatabaseCredentialException an error related to the username or password to be shown to the user
	 * @throws RemoteException 
	 */
	public Token signIn(String username, String password) throws DatabaseConnectionException, DatabaseCredentialException, RemoteException {
		Token token = authDatabase.allocateToken(username, password);
		return token;
	}
	
	/**
	 * A facade on the internal VerifierModule, gets the User connected to a token, or null if the token is invalid
	 * @param token the token being used
	 * @return a User object, or null if the token has since been revoked
	 */
	public User getUserInfo(Token token) {
		return new VerifierModule().getUserFromToken(token);
	}
	
	public Token checkToken(String t) {
		return new VerifierModule().checkToken(t);
	}
	
	public void revokeToken(User user, Token t) throws RemoteException, DatabaseConnectionException {
		authDatabase.revokeToken(user, t);
	}
}