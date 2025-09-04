package paymentClasses;
import java.sql.Date;

import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import authClasses.Token;


public class PaymentData {
	Token token;
	AuctionListing auction;
	Bid bid;
	CardType card;
	String cardnumber;
	String cardname;
	int securityCodeInt;
	Date expiryDate;
	AbstractCreditCard credit;
	ShippingType ship;
	CardFactory factory=new CardFactory();
	public PaymentData(Token token,AuctionListing auction,Bid bid, CardType card, String cardnumber, String cardname,int securityCodeInt, Date expiryDate, ShippingType ship) {
		this.token=token;
		this.auction=auction;
		this.ship=ship;
		this.bid=bid;
		this.credit =factory.getCreditCard(card, cardnumber, cardname, securityCodeInt, expiryDate);
	}
	public Token gettoken() {
		return token;
	}
	public AuctionListing getauction() {
		return auction;
	}
	public ShippingType getship() {
		return ship;
	}
	public Bid getbid() {
		return bid;
	}
	public AbstractCreditCard getcredit() {
		return credit;
	}
}