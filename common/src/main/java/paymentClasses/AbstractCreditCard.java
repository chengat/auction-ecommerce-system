package paymentClasses;

import java.io.Serializable;
import java.sql.Date;

public abstract class AbstractCreditCard implements Serializable{
	private static final long serialVersionUID = -1168633994275196348L;
	private int cardId;
	private String cardNumber;
	private String cardholderName;
	private int securityCode;
	private Date expiryDate;
	private CardType cardType;
	
	public AbstractCreditCard(int cardId, String cardNumber, String cardholderName, int securityCode, Date expiryDate, CardType cardType) {
		super();
		this.cardId = cardId;
		this.cardNumber = cardNumber;
		this.cardholderName = cardholderName;
		this.securityCode = securityCode;
		this.expiryDate = expiryDate;
		this.cardType = cardType;
	}

	public int getCardId() {
		return cardId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public String getCardholderName() {
		return cardholderName;
	}

	public int getSecurityCode() {
		return securityCode;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public CardType getCardType() {
		return this.cardType;
	}
	
	public abstract boolean processPayment(double amount);
}
