package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import auctionClasses.Auction.AuctionType;
import auctionRemoteInterfaces.CatalogueRemoteInterface;
import authClasses.Token;
import authRemoteInterfaces.AuthenticationRemoteInterface;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SearchResultsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String auctionId = request.getParameter("item");
        HttpSession session = request.getSession();
        AuthenticationRemoteInterface auth;
        CatalogueRemoteInterface catalogue;
        try {
			Registry registry = LocateRegistry.getRegistry("auctioncontainer",1019);
			catalogue = ((CatalogueRemoteInterface)registry.lookup("catalogue"));
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
        AuctionType type = catalogue.getAuction(Integer.valueOf(auctionId), token).getAuction().getAuctionType();
        switch(type) {
		case DUTCH:
			response.sendRedirect("dutchAuction?auctionId=" + auctionId);
			break;
		case FORWARD:
			response.sendRedirect("forwardAuction?auctionId=" + auctionId);
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			break;
        }
    }
}