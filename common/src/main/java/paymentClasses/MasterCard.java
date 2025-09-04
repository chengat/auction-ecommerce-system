package paymentClasses;
import java.sql.Date;

import paymentClasses.MasterCard;
public class MasterCard extends AbstractCreditCard{

	public MasterCard(int cardId, String cardNumber, String cardholderName, int securityCode, Date expiryDate, CardType cardType) {
		super(cardId, cardNumber, cardholderName, securityCode, expiryDate,cardType);
	}

	@Override
	public boolean processPayment(double amount) {
		return true;
	}
	
	

	

}
