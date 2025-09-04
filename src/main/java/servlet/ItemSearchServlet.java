package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;
import authRemoteInterfaces.AuthenticationRemoteInterface;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class ItemSearchServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        AuthenticationRemoteInterface auth;
        try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        String token = (String) session.getAttribute("token");
        if(token != null && auth.checkToken(token) != null) {
            response.setContentType("text/html");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/itemSearch.jsp");
            dispatcher.forward(request, response);
        }else {
        	response.sendRedirect("welcome");
        }
        

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
        Token t = auth.checkToken((String) request.getSession().getAttribute("token"));
        if(t == null) {
        	response.sendRedirect("welcome");
        	return;
        }
        CatalogueRemoteInterface catalogue;
        try {
			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			catalogue = ((CatalogueRemoteInterface)registry.lookup("catalogue"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        String searchQuery = request.getParameter("search");
        Map<Integer, AuctionListing> searchResults = catalogue.search(searchQuery,t);
        Map<Integer,Double> searchPrices = new HashMap<>();
        searchResults.forEach((key,listing)->{
        	double currentPrice = 0.0;
        	Bid currentBid = listing.getAuction().getCurrentBid();
        	if(currentBid != null) {
        		currentPrice = currentBid.getBidPrice();
        	}else {
        		currentPrice = listing.getAuction().getStartingPrice();
        	}
        	searchPrices.put(key, currentPrice);
        });
        request.setAttribute("searchResults", searchResults);
        request.setAttribute("searchPrices", searchPrices);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/searchResults.jsp");
        dispatcher.forward(request, response);
    }
}