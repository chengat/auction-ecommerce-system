package database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

import authClasses.Address;
import authClasses.SignUpUser;
import authClasses.Token;
import authClasses.User;
import authClasses.Token.TokenBuilder;
import authentication.TokenGenerator;
import database.DatabaseManager.Database;
import databaseConnectors.LoginDatabaseConnector;
import databaseRemoteInterfaces.DatabaseAuthRemoteInterface;
import exceptions.*;

public class AccountDatabaseModule extends UnicastRemoteObject implements DatabaseAuthRemoteInterface{
	private static final long serialVersionUID = -8854582351946911196L;

	public AccountDatabaseModule() throws RemoteException {
		super();
	}

	/**
	 * Creates a new user
	 * @param user The username to be used
	 * @param address An an Address object
	 * @param password The password to be used for the user
	 * @return A valid token for a new session if the account was created
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When an account already exists with the given username
	 */
	public Token createUser(SignUpUser user, Address address, String password) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException {
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		//Connection established, perform business logic
		try {
			database.beginTransaction();
			//Check if account already exists
			PreparedStatement lookupStmt = database.getNewPreparedStatement("SELECT * FROM users WHERE username = ? LIMIT 1");
			lookupStmt.setString(1, user.getUsername());
			ResultSet lookupResult = lookupStmt.executeQuery();
			boolean currentlyExists= lookupResult.next();
			//Throw an exception if the account already exists
			if (currentlyExists){
				throw new DatabaseCredentialException("An account with this username already exists");
			}
			//Create the statements to insert the user and their address
			PreparedStatement userCreationStmt = database.getNewPreparedStatement("INSERT INTO users VALUES (?,?,?,?)");
			userCreationStmt.setString(1, user.getUsername());
			userCreationStmt.setString(2, password);
			userCreationStmt.setString(3, user.getNameFirst());
			userCreationStmt.setString(4, user.getNameLast());
			PreparedStatement addressCreationStmt = database.getNewPreparedStatement("INSERT INTO addresses VALUES(?,?,?,?,?,?)");
			addressCreationStmt.setString(1, user.getUsername());
			addressCreationStmt.setString(2, address.getStreetName());
			addressCreationStmt.setInt(3, address.getStreetNumber());
			addressCreationStmt.setString(4, address.getPostalCode());
			addressCreationStmt.setString(5, address.getCity());
			addressCreationStmt.setString(6, address.getCountry());
			
			//Execute the statements
			userCreationStmt.executeUpdate();
			addressCreationStmt.executeUpdate();
			
			//Commit to the database
			database.commitTransaction();
			database.closeConnection();
			
			//Generate and return a token for this login session
			return this.createToken(user.getUsername());
		} catch(SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
			} catch (SQLException e1) {throw new DatabaseConnectionException("Error connecting to the database");}//Not much to do
			throw new DatabaseConnectionException("Error connecting to the database");
		}
	}
	
	/**
	 * Allocates a new token to a user in order to represent a login session
	 * @param username The username
	 * @param password The password
	 * @return A token if the username and password are correct
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the username or password are invalid, either due to the account not existing or the password being incorrect
	 */
	public Token allocateToken(String username, String password) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException {
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			DatabaseConnectionException ex = new DatabaseConnectionException("An error occured when connecting to the database");
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
		//Check if the username & password are correct
		try {
			PreparedStatement passwordStmt = database.getNewPreparedStatement("SELECT username,password FROM users WHERE username=?");
			passwordStmt.setString(1, username);
			ResultSet passwordResults = passwordStmt.executeQuery();
			passwordResults.next();
			String userDbPassword = passwordResults.getString(2);
			if(!userDbPassword.equals(password)) {
				throw new DatabaseCredentialException("Wrong password");
			}
			database.closeConnection();
			return createToken(username);
		}catch(SQLException e) {
			throw new DatabaseCredentialException("No account exists with that username");
		}
	}
	
	/**
	 * Internal helper method that creates the token in the database
	 * @param username The username for which the token is being created for
	 * @return A token
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the username is invalid
	 */
	private Token createToken(String username) throws DatabaseConnectionException,DatabaseCredentialException{
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			//Start a transaction
			database.beginTransaction();
			String generatedToken;
			//Find a unique token value
			while(true) {
				PreparedStatement tokenStmt = database.getNewPreparedStatement("SELECT COUNT(1) FROM tokens WHERE token = ? LIMIT 1");
				generatedToken = TokenGenerator.generateToken(64);
				tokenStmt.setString(1, generatedToken);
				ResultSet tokenResult = tokenStmt.executeQuery();
				tokenResult.next();
				boolean currentlyExists = tokenResult.getInt(1) == 1;
				if(!currentlyExists) {
					break;
				}
			}
			//Insert the token into the DB
			PreparedStatement createStmt = database.getNewPreparedStatement("INSERT INTO tokens VALUES(?,?,NULL,NULL,?)");
			createStmt.setString(1, username);
			createStmt.setString(2, generatedToken);
			createStmt.setLong(3, System.currentTimeMillis()/1000);
			createStmt.executeUpdate();
			//Commit, close & return the token
			database.commitTransaction();
			database.closeConnection();
			return TokenBuilder.buildToken(username, generatedToken);
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error provisioning token");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error provisioning token");}//Not much to do
		}
	}
	
	/**
	 * Revokes a token, used at the end of a user session. Overloaded methods exist for passing in different versions of the parameters in Objects
	 * @param username The username to which the token belongs
	 * @param token The token string
	 * @throws DatabaseConnectionExceptionWhen an internal error occurred when connecting to the database, or the token does not exist
	 */
	public void revokeToken(String username, String token) throws DatabaseConnectionException, RemoteException{
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement delStmt = database.getNewPreparedStatement("DELETE FROM tokens WHERE token = ? AND username = ?");
			delStmt.setString(1, token);
			delStmt.setString(2, username);
			int rowsAffected = delStmt.executeUpdate();
			if(rowsAffected != 1) {
				throw new DatabaseConnectionException("Internal error when querying database to revoke token");
			}
			return;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error revoking token");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error revoking token");}//Not much to do
		}
	}
	
	//3 alternative overloads so that whichever works can be used
	public void revokeToken(String username, Token token) throws DatabaseConnectionException, RemoteException{
		revokeToken(username,token.getToken());
	}
	
	public void revokeToken(User user, String token) throws DatabaseConnectionException, RemoteException{
		revokeToken(user.getUsername(),token);
	}
	
	public void revokeToken(User user, Token token) throws DatabaseConnectionException, RemoteException{
		revokeToken(user.getUsername(),token.getToken());
	}
	
	/**
	 * Returns a User object from the database. An overload exists for getting a user directly from a Token object
	 * @param username The username of the user
	 * @return A User object if they exist
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the username is invalid
	 */
	public User getUser(String username) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException{
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement userStmt = database.getNewPreparedStatement("SELECT * FROM users WHERE username = ?");
			userStmt.setString(1, username);
			ResultSet userResults = userStmt.executeQuery();
			boolean isResult = userResults.next();
			//If no results, the username is invalid
			if(!isResult) {
				throw new DatabaseCredentialException("Username is invalid");
			}
			//Package into User class and return
			User user = new User(username, userResults.getString("nameFirst"), userResults.getString("nameLast"));
			database.closeConnection();
			return user;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error");}//Not much to do
		}
	}
	
	//Alternative overload that uses the Token class
	public User getUser(Token t) throws DatabaseConnectionException, DatabaseCredentialException, RemoteException{
		return getUser(t.getUsername());
	}
	
	/**
	 * Retrieves a token from the database from a token String
	 * NOTE: This method cannot verify that the a provided username matches the returned token, that check must be performed seperately.
	 * @param tokenString
	 * @return a Token object if the string is valid
	 * @throws DatabaseConnectionException When an internal error occurred when connecting to the database
	 * @throws DatabaseCredentialException When the token String is invalid
	 */
	public Token getToken(String tokenString) throws DatabaseConnectionException,DatabaseCredentialException, RemoteException{
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			//Query for the token
			PreparedStatement tokenStmt = database.getNewPreparedStatement("SELECT * FROM tokens WHERE token = ?");
			tokenStmt.setString(1, tokenString);
			ResultSet tokenResult = tokenStmt.executeQuery();
			boolean isValidToken = tokenResult.next();
			//Check if the token exists in the db
			if(!isValidToken) {
				throw new DatabaseCredentialException("Token string is invalid");
			}
			//Package into Token class and return
			Token token = TokenBuilder.buildToken(tokenResult.getString("username"), tokenString);
			token.setSelectedAuction(tokenResult.getInt("selectedAuction"));
			token.setSocketConnection(tokenResult.getObject("socketConnection"));
			database.closeConnection();
			return token;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {throw new DatabaseConnectionException("Internal error");}//Not much to do
		}
	}
	
	/**
	 * Receives a change made to a token model and updates the server values for the current auction and socket
	 * This method does not throw any errors, success or failure is reflected via the return value
	 * @param t The token that was updated
	 *@return a boolean representing if the operation completed successfully
	 */
	public boolean updateToken(Token t) throws RemoteException{
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		try {
			PreparedStatement tokenStmt = database.getNewPreparedStatement("UPDATE tokens SET socketConnection = ?, selectedAuction = ? WHERE token = ? AND username = ?");
			tokenStmt.setObject(1, t.getSocketConnection());
			tokenStmt.setInt(2, t.getSelectedAuction());
			tokenStmt.setString(3, t.getToken());
			tokenStmt.setString(4, t.getUsername());
			tokenStmt.executeUpdate();
			database.closeConnection();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				return false;
			} catch (SQLException e1) {return false;}//Not much to do
		}
	}
	
	/**
	 * Gets the Address information for a user
	 * @param user
	 * @return an Address object or null if no results exist
	 * @throws RemoteException
	 * @throws DatabaseConnectionException 
	 */
	public Address getUserAddress(User user) throws RemoteException, DatabaseConnectionException{
		LoginDatabaseConnector database;
		//Connect to the database
		try {
			database = (LoginDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.LOGIN);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement addressStmt = database.getNewPreparedStatement("SELECT * FROM addresses WHERE username = ?");
			addressStmt.setString(1, user.getUsername());
			ResultSet getAddressStmt = addressStmt.executeQuery();
			if(getAddressStmt.next()) {
				String streetName = getAddressStmt.getString("streetName");
				int streetNumber = getAddressStmt.getInt("streetNumber");
				String postalCode = getAddressStmt.getString("postalCode");
				String city = getAddressStmt.getString("city");
				String country = getAddressStmt.getString("country");
				Address address = new Address(user.getUsername(), streetName, streetNumber, postalCode, city, country);
				return address;
			}else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {return null;}//Not much to do
		}
	}
}