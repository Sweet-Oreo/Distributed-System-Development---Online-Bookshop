/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TransactionService;

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
 * Handle retrieving transactions, creating transactions, acknowledging the receipt of books, and returning books
 * 
 * @author Team 6
 */
public class TransactionHandler extends HttpServlet {

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
        if (!result) {
            out.print("{\"result\": false}");  // User verification failed
        } else {
           
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            String transactions = null;
            boolean isFirstDatabaseWork = false;
            try {
                connection.connect();
                // Call remote SOAP service to search transactions
                UserService_Service service = new UserService_Service();
                UserService port = service.getUserServicePort();
                transactions = port.searchTransactions(tel);
                isFirstDatabaseWork = true;
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                transactions = databasePort.searchTransactions(tel);
            }

            Gson gson = new GsonBuilder().create();
            Type typeOfHashMap = new TypeToken<Map<String, String>>() { }.getType();
            Map<String, String> txnInfo = gson.fromJson(transactions, typeOfHashMap);
            
            if (txnInfo.isEmpty()) {
                out.print("{\"result\": true}");
            } else {
                UserService_Service service = new UserService_Service();
                UserService port = service.getUserServicePort();
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                StringBuilder txn = new StringBuilder("{\"result\": true, \"transaction\": [");
                for (Map.Entry<String, String> entry: txnInfo.entrySet()) {
                    String id = entry.getKey();
                    String txnDetail = entry.getValue();
                    String books = txnDetail.split("/")[0];
                    String amount = txnDetail.split("/")[1];
                    String status = txnDetail.split("/")[2];
                    String time = txnDetail.split("/")[3];
                    StringBuilder currentTxn = new StringBuilder("{\"id\": \"" + id + "\", \"books\": [");
                    //StringBuilder currentTxnBooks = new StringBuilder("");
                    typeOfHashMap = new TypeToken<Map<String, Integer>>() { }.getType();
                    Map<String, Integer> booksInfo = gson.fromJson(books, typeOfHashMap);
                    for (Map.Entry<String, Integer> bookEntry: booksInfo.entrySet()) {
                        String currentBookISBN = bookEntry.getKey();
                        double currentBookPrice = 0.0;
                        String currentBookTitle = null;
                        if (isFirstDatabaseWork) {  // If the first database service is working
                            currentBookPrice = port.searchBookPriceByISBN(currentBookISBN);
                            currentBookTitle = port.searchBookTitleByISBN(currentBookISBN);
                        } else {
                            currentBookPrice = databasePort.searchBookPriceByISBN(currentBookISBN);
                            currentBookTitle = databasePort.searchBookTitleByISBN(currentBookISBN);
                        }
                        currentTxn.append("{\"isbn\": \"" + currentBookISBN + "\", \"title\": \"" + currentBookTitle + "\", \"amount\": " + bookEntry.getValue() + ", \"price\": " + currentBookPrice + "}, "); 
                    }
                    currentTxn.delete(currentTxn.length() - 2, currentTxn.length());
                    currentTxn.append("], \"amount\": " + amount + ", \"status\": " + status + ", \"time\": \"" + time + "\"}, ");
                    txn.append(currentTxn.toString());
                }
                txn.delete(txn.length() - 2, txn.length());
                txn.append("]}");
                out.print(txn.toString());
            }
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
        // Get user password
        String passwd = request.getParameter("passwd");
        
        // Call remote SOAP Database Service to verify user
        UserVerify_Service verifyService = new UserVerify_Service();
        UserVerify verifyPort = verifyService.getUserVerifyPort();
        boolean result = verifyPort.verify(tel, passwd);
        if (!result) {
            out.print("{\"result\": false}");  // User verification failed
        } else {     
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            String createTxn = null;
            try {
                connection.connect();
                // Call remote SOAP service to create transaction
                UserService_Service service = new UserService_Service();
                UserService port = service.getUserServicePort();
                createTxn = port.createTransaction(tel);
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                createTxn = databasePort.createTransaction(tel);
            }
         
            if(createTxn.equals("Success"))
                out.print("{\"result\": true}");
            else if(createTxn.equals("Cart Empty")) 
                out.print("{\"result\": false, \"error\": \"Cart Empty\"}");
            else if(createTxn.equals("Stock Not Enough"))
                out.print("{\"result\": false, \"error\": \"Stock Not Enough\"}");
            else
                out.print("{\"result\": false, \"error\": \"Unknown Error\"}");
        }
        out.flush();
        
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter out = response.getWriter();
        // Parse URI to get telephone number and transaction id
        String URI = request.getRequestURI();
        String tel = URI.split("/")[3];
        String id = URI.split("/")[4];
        // Get user password
        String passwd = request.getParameter("passwd");
        String status = request.getParameter("status");
        
        // Call remote SOAP Database Service to verify user
        UserVerify_Service verifyService = new UserVerify_Service();
        UserVerify verifyPort = verifyService.getUserVerifyPort();
        boolean result = verifyPort.verify(tel, passwd);
        if (!result) {
            out.print("{\"result\": false}");  // User verification failed
        } else {
            
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            try {
                connection.connect();
                // Call remote SOAP service to add books to return items
                UserService_Service service = new UserService_Service();
                UserService port = service.getUserServicePort();
                if (status.equals("2")) {  
                    boolean isItemReturned = port.returnItems(tel, id);
                    if (isItemReturned) out.print("{\"result\": true}");
                    else out.print("{\"result\": false}");     
                } else if (status.equals("1")) {
                    boolean isItemReceived = port.receiveItems(id);
                    if (isItemReceived) out.print("{\"result\": true}");
                    else out.print("{\"result\": false}");   
                }
            } catch (Exception e) {
                // If one of database service doesn't work, send request to another one
                UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
                UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
                if (status.equals("2")) {  
                    boolean isItemReturned = databasePort.returnItems(tel, id);
                    if (isItemReturned) out.print("{\"result\": true}");
                    else out.print("{\"result\": false}");     
                } else if (status.equals("1")) {
                    boolean isItemReceived = databasePort.receiveItems(id);
                    if (isItemReceived) out.print("{\"result\": true}");
                    else out.print("{\"result\": false}");   
                }
            }
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





