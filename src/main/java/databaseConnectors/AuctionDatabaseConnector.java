package databaseConnectors;

public class AuctionDatabaseConnector extends AbstractDatabaseConnector {

	public AuctionDatabaseConnector() throws Exception {
		this.driverString = "com.mysql.jdbc.Driver";
		this.url = "jdbc:mysql://db:3306/auctiondatabase";
		this.username = "root";
		this.password = "password";
		connect();
	}
}
