package databaseConnectors;

public class LoginDatabaseConnector extends AbstractDatabaseConnector {
	
	public LoginDatabaseConnector() throws Exception{
		this.driverString = "com.mysql.jdbc.Driver";
		this.url = "jdbc:mysql://db:3306/logindatabase";
		this.username = "root";
		this.password = "password";
		connect();
	}
}