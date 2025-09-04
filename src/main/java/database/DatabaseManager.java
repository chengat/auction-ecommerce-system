package database;

import databaseConnectors.AbstractDatabaseConnector;
import databaseConnectors.AuctionDatabaseConnector;
import databaseConnectors.LoginDatabaseConnector;
import exceptions.DatabaseConnectionException;

public class DatabaseManager {
	private static DatabaseManager manager;

	private DatabaseManager() {
		// Private constructor for singleton pattern
	}

	public static synchronized DatabaseManager getInstance() {
		if (manager == null) {
			manager = new DatabaseManager();
		}
		return manager;
	}

	public AbstractDatabaseConnector getDatabaseConnection(Database db) throws DatabaseConnectionException {
		try {
			switch (db) {
				case LOGIN:
					return new LoginDatabaseConnector();
				case AUCTION:
					return new AuctionDatabaseConnector();
				default:
					throw new DatabaseConnectionException("Unsupported database type.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			DatabaseConnectionException ex = new DatabaseConnectionException("Failed to establish database connection.");
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
	}

	public enum Database {
		LOGIN,
		AUCTION
	}
}
