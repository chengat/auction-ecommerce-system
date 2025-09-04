package paymentClasses;

import java.sql.Date;

public class CardFactory {
	 public AbstractCreditCard getCreditCard(CardType cardType, String cardNumber, String cardholderName, int securityCode, Date expiryDate) {
	     
		 switch (cardType) {
	            case VISA:
	                return new Visa(0, cardNumber,cardholderName, securityCode, expiryDate,cardType); 
	            case MASTERCARD:
	                return new MasterCard(0, cardNumber,cardholderName, securityCode, expiryDate,cardType);
	            case AMEX:
	                return new AmericanExpress(0, cardNumber,cardholderName, securityCode, expiryDate,cardType);
	            default:
	                throw new IllegalArgumentException("Invalid card type: " + cardType);
	        }
	    }
}