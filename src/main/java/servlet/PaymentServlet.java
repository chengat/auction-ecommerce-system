package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import auctionClasses.*;
import authClasses.*;
import authRemoteInterfaces.AuthenticationRemoteInterface;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import paymentClasses.CardType;
import paymentClasses.PaymentData;
import paymentClasses.ShippingType;
import paymentFacade.PaymentFacade;
import paymentRemoteInterfaces.PaymentRemoteInterface;

public class PaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	request.setAttribute("error", "");
        response.setContentType("text/html");
        AuthenticationRemoteInterface auth;
        PaymentRemoteInterface payment;
        try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
			Registry paymentRegistry = LocateRegistry.getRegistry("paymentcontainer",1019);
			payment = ((PaymentRemoteInterface)paymentRegistry.lookup("payment"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        HttpSession session = request.getSession();
        //Set user info
        Token t = auth.checkToken(session.getAttribute("token").toString());
        User user = auth.getUserInfo(t);
        session.setAttribute("userInformation", user);
        session.setAttribute("userAddress", user.getAddress());
        //Calculate and set final price
        AuctionListing auction = (AuctionListing) session.getAttribute("AuctionListing");
        ShippingType shippingType = ShippingType.valueOf(request.getParameter("ShippingType").toString());
        session.setAttribute("ShippingType", shippingType.toString());
        Bid bid = (Bid)session.getAttribute("Bid");
        double finalPrice = payment.getTotalPrice(auction.getAuction(), auction.getItem(),bid , shippingType);
        session.setAttribute("totalPrice", finalPrice);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Payment.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	AuthenticationRemoteInterface auth;
        try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        //Form elements
         String cardnumber=request.getParameter("cardnumber");
         String cardname=request.getParameter("cardname");
         String expirydate=request.getParameter("expirydate");
         String securitycode=request.getParameter("securitycode");
         String paymentMethod = request.getParameter("paymentMethod");
         //Get info from session
         HttpSession session = request.getSession(false);
         Token token = (Token) auth.checkToken(session.getAttribute("token").toString());
         AuctionListing auction = (AuctionListing) session.getAttribute("AuctionListing");
         ShippingType Ship = ShippingType.valueOf(session.getAttribute("ShippingType").toString());
         Bid bid = (Bid) session.getAttribute("Bid");
         //Set data types
         String fulldate = expirydate + "-01";
         Date ExpiryDate = Date.valueOf(fulldate);
         int securityCodeInt = Integer.parseInt(securitycode);
         CardType card = CardType.valueOf(paymentMethod);
         
        //Process payment
        System.out.println("Charging card");
        PaymentData data =new PaymentData(token,auction,bid,card, cardnumber, cardname, securityCodeInt, ExpiryDate, Ship);
    	PaymentFacade facade= new PaymentFacade(data);
    	System.out.println("Created classes");
    	session.setAttribute("shipdays", facade.shippingdays());
    	boolean process=facade.processpayment();
    	System.out.println("Card has been charged");
    	if (process==true) {
    		request.getSession(true).setAttribute("receipt", facade.getreceipt());
    		request.getSession().setAttribute("itemId", auction.getItem().getItemId());
    		response.sendRedirect("receipt");
    	}else {
    		response.getWriter().println("403 Error");
    	}
    }

    /**private PaymentDetails getPaymentDetails(String itemId, boolean expeditedShipping) {
        // Implement payment details retrieval
        return new PaymentDetails(); // Placeholder
    }

    private boolean processPayment(HttpServletRequest request) {
        // Implement payment processing
        return true; // Placeholder
    }**/
}