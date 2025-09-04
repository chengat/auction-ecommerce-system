package paymentClasses;
import java.sql.Date;

import paymentClasses.AmericanExpress;

public class AmericanExpress extends AbstractCreditCard{
	
	public AmericanExpress(int cardId, String cardNumber, String cardholderName, int securityCode, Date expiryDate, CardType cardType) {
		super(cardId, cardNumber, cardholderName, securityCode, expiryDate, cardType);
	}
	
	@Override
	public boolean processPayment(double amount) {
		return true;
	}
	
	
	

}