package database;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import auctionClasses.Auction;
import auctionClasses.Bid;
import authClasses.User;
import database.DatabaseManager.Database;
import databaseConnectors.AuctionDatabaseConnector;
import databaseRemoteInterfaces.DatabasePaymentRemoteInterface;
import exceptions.DatabaseConnectionException;
import paymentClasses.AbstractCreditCard;
import paymentClasses.Receipt;
import paymentClasses.ShippingType;

public class PaymentDatabaseModule extends UnicastRemoteObject implements DatabasePaymentRemoteInterface{
	private static final long serialVersionUID = -5311810332215915582L;

	public PaymentDatabaseModule() throws RemoteException {
		super();
	}

	public Receipt createReceipt(int auctionId, int bidId, String username, double amount, ShippingType shippingType) throws DatabaseConnectionException, RemoteException {
		AuctionDatabaseConnector database;
		// Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement receiptStmt = database.getNewPreparedStatement(
					"INSERT INTO payments (auctionId, userId, amount,shippingType,bidId) VALUES(?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			receiptStmt.setInt(1, auctionId);
			receiptStmt.setString(2, username);
			receiptStmt.setDouble(3, amount);
			receiptStmt.setString(4, shippingType.toString());
			receiptStmt.setInt(5, bidId);
			receiptStmt.executeUpdate();
			return new Receipt(auctionId, username, amount, 0, shippingType);
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {
				throw new DatabaseConnectionException("Internal error");
			} // Not much to do
		}
	}

	public Receipt createReceipt(Auction auction, Bid bid, User user, double ammount, ShippingType shippingType) throws DatabaseConnectionException, RemoteException {
		return this.createReceipt(auction.getAuctionId(), bid.getBidId(), user.getUsername(), ammount, shippingType);
	}

	public Receipt getReceipt(int auctionId, String username) throws DatabaseConnectionException, RemoteException {
		System.out.println("Creating receipt");
		AuctionDatabaseConnector database;
		// Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			PreparedStatement receiptStmt = database
					.getNewPreparedStatement("SELECT * FROM payments WHERE auctionId = ? AND userId = ?");
			receiptStmt.setInt(1, auctionId);
			receiptStmt.setString(2, username);
			ResultSet rs = receiptStmt.executeQuery();
			if (!rs.next()) {
				return null;
			}
			return new Receipt(auctionId, username, rs.getDouble("amount"), rs.getInt("creditCardId"),
					ShippingType.valueOf(rs.getString("shippingType")));
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {
				throw new DatabaseConnectionException("Internal error");
			} // Not much to do
		}
	}

	public Receipt getReceipt(Auction auction, User user) throws DatabaseConnectionException, RemoteException {
		return this.getReceipt(auction.getAuctionId(), user.getUsername());
	}

	public boolean attachCreditCard(Receipt receipt, AbstractCreditCard creditCard) throws DatabaseConnectionException, RemoteException {
		System.out.println("Attaching credit card");
		AuctionDatabaseConnector database;
		// Connect to the database
		try {
			database = (AuctionDatabaseConnector) DatabaseManager.getInstance().getDatabaseConnection(Database.AUCTION);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseConnectionException("An error occured when connecting to the database");
		}
		try {
			database.beginTransaction();
			PreparedStatement ccStatement = database.getNewPreparedStatement(
					"INSERT INTO creditcards (cardNumber,cardholderName,cardExpiry,securityCode,cardType) VALUES (?,?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS);
			ccStatement.setString(1, creditCard.getCardNumber());
			ccStatement.setString(2, creditCard.getCardholderName());
			ccStatement.setDate(3, creditCard.getExpiryDate());
			ccStatement.setInt(4, creditCard.getSecurityCode());
			ccStatement.setString(5, creditCard.getCardType().toString());
			ccStatement.executeUpdate();
			ResultSet genKey = ccStatement.getGeneratedKeys();
			if (!genKey.next()) {
				return false;
			}
			int ccKey = genKey.getInt(1);
			// Attach to the payment
			PreparedStatement paymentStmt = database
					.getNewPreparedStatement("UPDATE payments SET creditCardId = ? WHERE userId = ? AND auctionId = ?");
			paymentStmt.setInt(1, ccKey);
			paymentStmt.setString(2, receipt.getUserId());
			paymentStmt.setInt(3, receipt.getAuctionId());
			int count = paymentStmt.executeUpdate();
			if (count != 1) {
				return false;
			}
			database.commitTransaction();
			database.closeConnection();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				database.closeConnection();
				throw new DatabaseConnectionException("Internal error");
			} catch (SQLException e1) {
				throw new DatabaseConnectionException("Internal error");
			} // Not much to do
		}
	}
}