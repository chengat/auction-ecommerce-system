package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import authClasses.Address;
import authClasses.Token;
import authClasses.User;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import database.AccountDatabaseModule;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;
import paymentClasses.Receipt;
import paymentRemoteInterfaces.PaymentRemoteInterface;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ReceiptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Receipt.jsp");
        dispatcher.forward(request, response);
        AuthenticationRemoteInterface auth;
        try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        //Revoke token
        Token token = auth.checkToken(request.getSession().getAttribute("token").toString());
        User user = auth.getUserInfo(token);
        try {
			auth.revokeToken(user, token);
			request.getSession().removeAttribute("token");
		} catch (RemoteException | DatabaseConnectionException e) {
			e.printStackTrace();
		}
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }
}