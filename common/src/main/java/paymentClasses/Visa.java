package paymentClasses;
import java.sql.Date;

import paymentClasses.Visa;

public class Visa extends AbstractCreditCard{

	public Visa(int cardId, String cardNumber, String cardholderName, int securityCode, Date expiryDate, CardType cardType) {
		super(cardId, cardNumber, cardholderName, securityCode, expiryDate,cardType);
	}
	@Override
	public boolean processPayment(double amount) {
		return true;
	}

}
