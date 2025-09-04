package servlet;
import javax.servlet.*;
import javax.servlet.http.*;

import authClasses.Token;
import authRemoteInterfaces.AuthenticationRemoteInterface;
import exceptions.DatabaseConnectionException;
import exceptions.DatabaseCredentialException;

import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// SignInServlet.java
public class SignInServlet extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        request.setAttribute("error", "");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/signIn.jsp");
        dispatcher.forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // In a real application, you would validate against a database
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
        	Token t = auth.signIn(username, password);
        	request.getSession(true).setAttribute("token", t.getToken());
            response.sendRedirect("itemSearch");
        }catch(DatabaseConnectionException conn) {
            request.setAttribute("error", "Internal error, please try again later");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/signIn.jsp");
            dispatcher.forward(request, response);
        }catch(DatabaseCredentialException cred) {
            request.setAttribute("error", cred.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/signIn.jsp");
            dispatcher.forward(request, response);
        }
    }
}