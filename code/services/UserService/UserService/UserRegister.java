/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.UserService;
import service.UserServiceCoop;
import service.UserServiceCoop_Service;
import service.UserService_Service;
import sun.misc.BASE64Encoder;
import userverifyservice.UserVerify;
import userverifyservice.UserVerify_Service;

/**
 * Servlet: Handle user registration
 * 
 * @author Team 6
 */
public class UserRegister extends HttpServlet {

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        
        // Obtain registe information
        String tel = request.getParameter("tel");
        String name = request.getParameter("name");
        String passwd = request.getParameter("passwd");
        String card = request.getParameter("card");
        String addr = request.getParameter("addr");
        
       // Encrypt user credit card number and address
        String encryptedCard = new String((new BASE64Encoder()).encode(card.getBytes()));
        String encryptedAddr = new String((new BASE64Encoder()).encode(addr.getBytes()));
        
        // Call remote SOAP Database Service to register new user
        // Test whether one remote server is working, if it doesn't work, send request to another server
        HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        boolean result = false;
        try {
            UserService_Service service = new UserService_Service();
            UserService  port = service.getUserServicePort();
            result = port.addUser(tel, name, passwd, encryptedCard, encryptedAddr);
        } catch (Exception e) {
            UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
            UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
            result = databasePort.addUser(tel, name, passwd, encryptedCard, encryptedAddr);
        }
        
        // Respond to client
        if (result) out.print("{\"result\": true}");
        else out.print("{\"result\": false, \"error\": \"This telephone number has already been registered\"}");
        out.flush();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
