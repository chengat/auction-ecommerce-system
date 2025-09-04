package databaseConnectors;
import java.sql.*;

public abstract class AbstractDatabaseConnector {
	protected String driverString;
	protected String url;
	protected String username;
	protected String password;
	
	protected Connection connection; 
	
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName(this.driverString);
		this.connection = DriverManager.getConnection(url,username,password);
	}
	
	public Statement getStatement() throws SQLException {
		return this.connection.createStatement();
	}
	
	public PreparedStatement getNewPreparedStatement(String sqlQuery) throws SQLException {
		return this.connection.prepareStatement(sqlQuery);
	}
	
	public PreparedStatement getNewPreparedStatement(String sqlQuery,int command) throws SQLException {
		return this.connection.prepareStatement(sqlQuery,command);
	}
	
	public void closeConnection() throws SQLException {
		this.connection.close();
	}
	
	//Changes autocommit to false so that a transaction takes place
	public void beginTransaction() throws SQLException {
		connection.setAutoCommit(false);
	}
	
	//Commits the transaction and resets autocommit so that following operations do not occur
	public void commitTransaction() throws SQLException {
		try {
			connection.commit();
		} catch (SQLException e) {
			connection.rollback();
			throw new SQLException("Rolling back");
		}
		connection.setAutoCommit(true);
	}
}