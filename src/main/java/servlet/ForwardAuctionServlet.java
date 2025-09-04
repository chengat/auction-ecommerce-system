package servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import websocket.WebSocketHandler;
import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionRemoteInterfaces.AuctionRemoteInterface;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;
import authClasses.User;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import exceptions.AuctionBidException;
import exceptions.DatabaseConnectionException;

public class ForwardAuctionServlet extends HttpServlet {
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
            Bid currentBid = listing.getAuction().getCurrentBid();
            double currentPrice = currentBid != null ? currentBid.getBidPrice() : listing.getAuction().getStartingPrice();
            request.setAttribute("bidPrice", currentPrice);
            request.setAttribute("endTime", listing.getAuction().getAuctionClose());
            request.setAttribute("username", token.getUsername());
            request.setAttribute("bidUsername", currentBid == null ? "" : currentBid.getUsername());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Forward.jsp");
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
        AuctionRemoteInterface auctionModule;
        try {
			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			auctionModule = ((AuctionRemoteInterface)registry.lookup("auction"));
			Registry authRegistry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)authRegistry.lookup("authentication"));
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

        String itemId = request.getParameter("auctionId");
        String bidAmountStr = request.getParameter("bidAmount");

        try {
            int auctionId = Integer.parseInt(itemId);
            double bidAmount = Double.parseDouble(bidAmountStr);
            User user = auth.getUserInfo(token);
            Bid bid = auctionModule.placeBid(user, auctionId, bidAmount);
            if (bid != null) {
                // Notify participants via WebSocket
                WebSocketHandler.sendBidUpdate(
                        String.valueOf(auctionId),
                        bid.getBidPrice(),
                        bid.getUsername()
                );
                response.sendRedirect("forwardAuction?auctionId=" + itemId);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed to place bid. Token may be invalid.");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input.");
        } catch (AuctionBidException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error placing bid: " + e.getMessage());
        } catch (DatabaseConnectionException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error.");
        }
    }
}
