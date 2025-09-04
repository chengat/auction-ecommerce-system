package servlet;

import javax.servlet.*;
import javax.servlet.http.*;

import auctionClasses.Auction;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionRemoteInterfaces.AuctionRemoteInterface;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DutchAuctionServlet extends HttpServlet {

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
        Token token = (Token) auth.checkToken(session.getAttribute("token").toString());
        
        if (token == null) {
            response.sendRedirect("welcome");
            return;
        }

        String auctionIdRaw = request.getParameter("auctionId");
        try {
            int auctionId = Integer.parseInt(auctionIdRaw);
            CatalogueRemoteInterface catalogue;
            try {
    			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
    			catalogue = ((CatalogueRemoteInterface)registry.lookup("catalogue"));
    		}catch(Exception e) {
    			System.out.println("Issue with registry");
    			e.printStackTrace();
    			return;
    		}
            AuctionListing listing = catalogue.getAuction(auctionId,token);

            if (listing == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction item not found.");
                return;
            }

            request.setAttribute("item", listing.getItem());
            double currentPrice = listing.getAuction().getStartingPrice();
            request.setAttribute("bidPrice", currentPrice);
            request.setAttribute("endTime", listing.getAuction().getAuctionClose());
            request.setAttribute("username", token.getUsername());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Dutch.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid auction ID.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error retrieving auction details.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession();
        AuthenticationRemoteInterface auth;
        CatalogueRemoteInterface catalogue;
        AuctionRemoteInterface auction;
        try {
			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			catalogue = ((CatalogueRemoteInterface)registry.lookup("catalogue"));
			auction = ((AuctionRemoteInterface)registry.lookup("auction"));
			Registry authRegistry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)authRegistry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        Token token = (Token) auth.checkToken(session.getAttribute("token").toString());
        String auctionIdRaw = request.getParameter("auctionId");
        int auctionId = Integer.parseInt(auctionIdRaw);
        Auction currentAuction = catalogue.getAuction(auctionId, token).getAuction();
        try {
			auction.placeBid(auth.getUserInfo(token), auctionId, currentAuction.getStartingPrice());
			//Closing and notification will be handled automatically
			response.sendRedirect("auctionEnd");
		} catch (RemoteException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		} catch (DatabaseConnectionException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		} catch (AuctionBidException e) {
			response.getWriter().print("Error placing bid: " + e.getMessage());
			e.printStackTrace();
		}
    }
}
