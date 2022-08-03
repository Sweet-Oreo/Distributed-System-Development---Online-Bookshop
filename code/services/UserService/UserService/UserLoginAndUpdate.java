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
import userverifyservice.UserVerify;
import userverifyservice.UserVerify_Service;

/**
 * Servlet: Handle user login and information update
 * 
 * @author Team 6
 */
public class UserLoginAndUpdate extends HttpServlet {

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        
        // Parse URI to get telephone number
        String URI = request.getRequestURI();
        String tel = URI.split("/")[3];
        // Get user password
        String passwd = request.getParameter("passwd");
        // Call remote SOAP Database Service to verify user
        UserVerify_Service verifyService = new UserVerify_Service();
        UserVerify verifyPort = verifyService.getUserVerifyPort();
        boolean result = verifyPort.verify(tel, passwd);
        
        // Call remote SOAP Database Service to search user info, and respond to client
        if (result) {
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            try {
                connection.connect();
                UserService_Service databaseService = new UserService_Service();
                UserService  databasePort = databaseService.getUserServicePort();
                String name = databasePort.searchUserNameByTel(tel);
                String card = databasePort.searchUserCardByTel(tel);
                String addr = databasePort.searchUserAddrByTel(tel);
                double consumption = databasePort.searchUserConsumption(tel);
                out.print("{\"result\": true, \"user\": {\"name\": \"" + name + "\", \"card\": \"" + card + "\", \"consumption\": " + consumption + ", \"addr\": \"" + addr + "\"}}");
            } catch (Exception e) {
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                String name = databasePort.searchUserNameByTel(tel);
                String card = databasePort.searchUserCardByTel(tel);
                String addr = databasePort.searchUserAddrByTel(tel);
                double consumption = databasePort.searchUserConsumption(tel);
                out.print("{\"result\": true, \"user\": {\"name\": \"" + name + "\", \"card\": \"" + card + "\", \"consumption\": " + consumption + ", \"addr\": \"" + addr + "\"}}");
            }
        } else { // User verification fails
            out.print("{\"result\": false}");
        }
        out.flush();
        
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        
        // Parse URI to get telephone number
        String URI = request.getRequestURI();
        String tel = URI.split("/")[3];
        // Get user password and updated information
        String passwd = request.getParameter("passwd");
        String newName = request.getParameter("new_name");
        String newPasswd = request.getParameter("new_passwd");
        String newCard = request.getParameter("new_card");
        String newAddr = request.getParameter("new_addr");
        
        // Call remote SOAP Database Service to verify user
        UserVerify_Service verifyService = new UserVerify_Service();
        UserVerify verifyPort = verifyService.getUserVerifyPort();
        boolean result = verifyPort.verify(tel, passwd);
        
        // Call remote SOAP service to update user information
        if (result) {
            
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            boolean isPasswdUpdated = true, isNameUpdated = true, isCardUpdated = true, isAddrUpdated = true;
            try {
                connection.connect();
                UserService_Service service = new UserService_Service();
                UserService  port = service.getUserServicePort();
                if (newName != null) isNameUpdated = port.updateName(tel, newName);
                if (newPasswd != null) isPasswdUpdated = port.updatePasswd(tel, newPasswd);
                if (newCard != null) isCardUpdated = port.updateCard(tel, newCard);
                if (newAddr != null) isAddrUpdated = port.updateAddress(tel, newAddr);
                if (isPasswdUpdated && isNameUpdated && isCardUpdated && isAddrUpdated)
                    out.print("{\"result\": true}");
                else
                    out.print("{\"result\": false, \"error\": \"Unknown Error\"}");
            } catch (Exception e) {
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                if (newName != null) isNameUpdated = databasePort.updateName(tel, newName);
                if (newPasswd != null) isPasswdUpdated = databasePort.updatePasswd(tel, newPasswd);
                if (newCard != null) isCardUpdated = databasePort.updateCard(tel, newCard);
                if (newAddr != null) isAddrUpdated = databasePort.updateAddress(tel, newAddr);
                if (isPasswdUpdated && isNameUpdated && isCardUpdated && isAddrUpdated)
                    out.print("{\"result\": true}");
                else
                    out.print("{\"result\": false, \"error\": \"Unknown Error\"}");
            }
            
        } else { // User verification fails
            out.print("{\"result\": false}");
        }
        out.flush();
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS");
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
