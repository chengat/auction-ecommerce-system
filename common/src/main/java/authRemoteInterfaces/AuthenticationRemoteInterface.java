package authRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import authClasses.Address;
import authClasses.SignUpUser;
import authClasses.Token;
import authClasses.User;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;

public interface AuthenticationRemoteInterface extends Remote{
	/**
	 * Attempts to sign a user up, returns a token to represent the new session if successful
	 * @param user a SignUpUser Object. This Object is used to hold the information of a potential user without interfering with the standard User class.
	 * @param password
	 * @param address an Address object
	 * @return a Token if the login is successful, otherwise an error will be thrown
	 * @throws DatabaseConnectionException internal error connecting to the database
	 * @throws DatabaseCredentialException if the username or password are too short/long, or the username is already in use. This error should be displayed to the user
	 */
	public Token signUp(SignUpUser user, String password, Address address) throws DatabaseConnectionException, DatabaseCredentialException, RemoteException;
	
	/**
	 * Attempts to sign a user in. Returns a token to represent the new session if successful.
	 * @param username
	 * @param password
	 * @return a Token if the login is successful, otherwise an error will be thrown
	 * @throws DatabaseConnectionException internal error connecting to the database
	 * @throws DatabaseCredentialException an error related to the username or password to be shown to the user
	 */
	public Token signIn(String username, String password) throws DatabaseConnectionException, DatabaseCredentialException, RemoteException;
	
	/**
	 * A facade on the internal VerifierModule, gets the User connected to a token, or null if the token is invalid
	 * @param token the token being used
	 * @return a User object, or null if the token has since been revoked
	 */
	public User getUserInfo(Token token) throws RemoteException;
	
	public Token checkToken(String t) throws RemoteException;
	
	public void revokeToken(User user, Token t) throws RemoteException, DatabaseConnectionException;
}