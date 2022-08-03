/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CartService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
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
 * Handle request from client to add books to cart, update cart, delete books in cart, and check books in cart
 * 
 * @author Team 6
 */
public class CartHandler extends HttpServlet {

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
        // Respond to client
        if (result) {
            
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            String cartJson = null;
            try {
                connection.connect();
                // Call remote SOAP Database Service to search user cart
                UserService_Service service = new UserService_Service();
                UserService  port = service.getUserServicePort();
                cartJson = port.searchUserCart(tel);
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                cartJson = databasePort.searchUserCart(tel);
            }
            
            Gson gson = new GsonBuilder().create();
            Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
            Map<String, String> cartInfo = gson.fromJson(cartJson, typeOfHashMap);
            
            if (cartInfo.isEmpty()) {  // If user's cart is empty
                out.print("{\"result\": true}");
            } else {
                StringBuilder cart = new StringBuilder("{\"result\": true, \"books\": [");
                for (Map.Entry<String, String> entry: cartInfo.entrySet()) {
                    String bookISBN = entry.getKey();
                    String bookInfo = entry.getValue();
                    String title = bookInfo.split("/")[0];
                    String price = bookInfo.split("/")[1];
                    String amount = bookInfo.split("/")[2];
                    cart.append("{\"isbn\": " + "\"" + bookISBN + "\", \"title\": " + "\"" + title + "\", \"amount\": " + amount + ", \"price\": " + price + "}, ");
            }
                cart.delete(cart.length() - 2, cart.length());
                cart.append("]}");
                out.print(cart.toString()); 
            }
        } else {  // User verification fails
            out.print("\"result\": false");
        }
        out.flush();
    }

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
        
        // Parse URI to get telephone number
        String URI = request.getRequestURI();
        String tel = URI.split("/")[3];
        // Get user password and book isbn
        String passwd = request.getParameter("passwd");
        String isbn = request.getParameter("isbn");
        // Get the amount of added books and convert to a integer
        int amount = 0;
        try {
            amount = Integer.parseInt(request.getParameter("amount"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        
        // Call remote SOAP Database Service to verify user
        UserVerify_Service verifyService = new UserVerify_Service();
        UserVerify verifyPort = verifyService.getUserVerifyPort();
        boolean result = verifyPort.verify(tel, passwd);
        
        // Respond to client
        if (result) {
            
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            boolean isBookAdded = false;
            try {
                connection.connect();
                // Call remote SOAP Database Service to add books to user cart
                UserService_Service service = new UserService_Service();
                UserService  port = service.getUserServicePort();
                isBookAdded = port.addToCart(tel, isbn, amount); 
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                isBookAdded = databasePort.addToCart(tel, isbn, amount);
            }
            
            if (isBookAdded)
                out.print("{\"result\": true}");
            else
                out.print("{\"result\": false}");
        } else {  // User verification fails
            out.print("{\"result\": false}");
        }
        out.flush();
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
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
        
        // Respond to client
        if (result) {
            
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            boolean isCartDeleted = false;
            try {
                connection.connect();
                // Call remote SOAP Database Service to empty user cart
                UserService_Service service = new UserService_Service();
                UserService  port = service.getUserServicePort();
                isCartDeleted = port.deleteCart(tel);  
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                isCartDeleted = databasePort.deleteCart(tel);
            }
            
            if (isCartDeleted)
                out.print("{\"result\": true}");
            else
                out.print("{\"result\": false}");
        } else {  // User verification fails
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
        
        // Parse URI to get telephone number and book isbn
        String URI = request.getRequestURI();
        String tel = URI.split("/")[3];
        String isbn = URI.split("/")[4];
        // Get user password and book isbn
        String passwd = request.getParameter("passwd");
        // Get the amount of added books and convert to a integer
        int amount = 0;
        try {
            amount = Integer.parseInt(request.getParameter("amount"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        // Call remote SOAP Database Service to verify user
        UserVerify_Service verifyService = new UserVerify_Service();
        UserVerify verifyPort = verifyService.getUserVerifyPort();
        boolean result = verifyPort.verify(tel, passwd);
        
        // Respond to client
        if (result) {
            
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            boolean isCartModified = false;
            try {
                connection.connect();
                // Call remote SOAP Database Service to add books to user cart
                UserService_Service service = new UserService_Service();
                UserService  port = service.getUserServicePort();
                isCartModified = port.modifyUserCart(tel, isbn, amount);
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                isCartModified = databasePort.modifyUserCart(tel, isbn, amount);
            }
            
            if (isCartModified)
                out.print("{\"result\": true}");
            else
                out.print("{\"result\": false}");
        } else {  // User verification fails
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
