package databaseRemoteInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import authClasses.Address;
import authClasses.SignUpUser;
import authClasses.Token;
import authClasses.User;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;

public interface DatabaseAuthRemoteInterface extends Remote {
	public Token createUser(SignUpUser user, Address address, String password) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException;
	
	/**
	 * Allocates a new token to a user in order to represent a login session
	 * @param username The username
	 * @param password The password
	 * @return A token if the username and password are correct
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the username or password are invalid, either due to the account not existing or the password being incorrect
	 * @throws RemoteException 
	 */
	public Token allocateToken(String username, String password) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException;
	
	/**
	 * Internal helper method that creates the token in the database
	 * @param username The username for which the token is being created for
	 * @return A token
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the username is invalid
	 */	
	/**
	 * Revokes a token, used at the end of a user session. Overloaded methods exist for passing in different versions of the parameters in Objects
	 * @param username The username to which the token belongs
	 * @param token The token string
	 * @throws RemoteException 
	 * @throws DatabaseConnectionExceptionWhen an internal error occurred when connecting to the database, or the token does not exist
	 */
	public void revokeToken(String username, String token) throws DatabaseConnectionException, RemoteException;
	
	//3 alternative overloads so that whichever works can be used
	public void revokeToken(String username, Token token) throws DatabaseConnectionException, RemoteException;
	
	public void revokeToken(User user, String token) throws DatabaseConnectionException, RemoteException;
	
	public void revokeToken(User user, Token token) throws DatabaseConnectionException, RemoteException;
	
	/**
	 * Returns a User object from the database. An overload exists for getting a user directly from a Token object
	 * @param username The username of the user
	 * @return A User object if they exist
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the username is invalid
	 * @throws RemoteException 
	 */
	public User getUser(String username) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException;
	
	//Alternative overload that uses the Token class
	public User getUser(Token t) throws DatabaseConnectionException, DatabaseCredentialException, RemoteException;
	
	/**
	 * Retrieves a token from the database from a token String
	 * NOTE: This method cannot verify that the a provided username matches the returned token, that check must be performed seperately.
	 * @param tokenString
	 * @return a Token object if the string is valid
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the token String is invalid
	 * @throws RemoteException 
	 */
	public Token getToken(String tokenString) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException;
	
	/**
	 * Receives a change made to a token model and updates the server values for the current auction and socket
	 * This method does not throw any errors, success or failure is reflected via the return value
	 * @param t The token that was updated
	 *@return a boolean representing if the operation completed successfully
	 * @throws RemoteException 
	 */
	public boolean updateToken(Token t) throws RemoteException;
	
	public Address getUserAddress(User user) throws RemoteException, DatabaseConnectionException;
}
