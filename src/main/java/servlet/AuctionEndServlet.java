package servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import auctionClasses.AuctionListing;
import auctionClasses.Bid;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;
import authRemoteInterfaces.AuthenticationRemoteInterface;

public class AuctionEndServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        AuthenticationRemoteInterface auth;
        CatalogueRemoteInterface catalogue;

        try {
            Registry authRegistry = LocateRegistry.getRegistry("authcontainer", 1019);
            auth = (AuthenticationRemoteInterface) authRegistry.lookup("authentication");

            Registry auctionRegistry = LocateRegistry.getRegistry("auctioncontainer", 1019);
            catalogue = (CatalogueRemoteInterface) auctionRegistry.lookup("catalogue");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registry lookup failed.");
            return;
        }
        Token token = (Token) auth.checkToken(session.getAttribute("token").toString());
        String auctionIdRaw = request.getParameter("auctionId");
        if (auctionIdRaw == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Auction ID is missing.");
            return;
        }

        try {
            int auctionId = Integer.parseInt(auctionIdRaw);

            
            AuctionListing listing = catalogue.getAuction(auctionId, token); 

            if (listing == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Auction not found.");
                return;
            }

            Bid winningBid = listing.getAuction().getCurrentBid();

            if (winningBid == null) {
                // No bids were placed
                request.setAttribute("message", "No bids were placed for this auction.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("/noWinner.jsp");
                dispatcher.forward(request, response);
                return;
            }

            // Set session attributes
            session.setAttribute("AuctionListing", listing);
            session.setAttribute("Bid", winningBid);
            session.setAttribute("item", listing.getItem());

            // Forward to the auction end JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/auctionEnd.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Auction ID format.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing auction end.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
