package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import authClasses.Address;
import authClasses.SignUpUser;
import authClasses.Token;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// SignUpServlet.java
public class SignUpServlet extends HttpServlet {
	private static final long serialVersionUID = -3954615245481380481L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setAttribute("error", "");
        response.setContentType("text/html");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/signUp.jsp");
        dispatcher.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        String firstName = request.getParameter("first-name");
        String lastName = request.getParameter("last-name");
        
        int streetNumber;
        try {
        	streetNumber = Integer.valueOf(request.getParameter("street-number"));
        }catch(Exception e) {
        	request.setAttribute("error", "Street number must be a number");
        	RequestDispatcher dispatcher = request.getRequestDispatcher("/signUp.jsp");
            dispatcher.forward(request, response);
            return;
        }
        String streetName = request.getParameter("street-name");
        
        String city = request.getParameter("city");
        String postalCode = request.getParameter("postal-code");
        String country = request.getParameter("country");
        
        if(postalCode.length() != 6) {
        	request.setAttribute("error", "Postal code must be 6 characters, no space");
        	RequestDispatcher dispatcher = request.getRequestDispatcher("/signUp.jsp");
            dispatcher.forward(request, response);
        	return;
        }
        
        Address address = new Address(username, streetName, streetNumber, postalCode, city, country);
        SignUpUser newUser = new SignUpUser(username, firstName, lastName);
        
        AuthenticationRemoteInterface auth;
        try {
			Registry registry = LocateRegistry.getRegistry("authcontainer",1019);
			auth = ((AuthenticationRemoteInterface)registry.lookup("authentication"));
		}catch(Exception e) {
			System.out.println("Issue with registry");
			e.printStackTrace();
			return;
		}
        
        try {
			Token t = auth.signUp(newUser, password, address);
			request.getSession(true).setAttribute("token", t.getToken());
			response.sendRedirect("itemSearch");
		} catch (DatabaseCredentialException e) {
            request.setAttribute("error", e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/signUp.jsp");
            dispatcher.forward(request, response);
		} catch (DatabaseConnectionException e) {
            request.setAttribute("error", "Internal error, please try again later");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/signUp.jsp");
            dispatcher.forward(request, response);
		}
    }
}