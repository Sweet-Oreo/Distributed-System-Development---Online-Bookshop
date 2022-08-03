/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.BookDao;
import dao.CartDao;
import dao.TransactionDao;
import dao.UserDao;
import dao.impl.BookDaoImpl;
import dao.impl.CartDaoImpl;
import dao.impl.TransactionDaoImpl;
import dao.impl.UserDaoImpl;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import sun.misc.BASE64Decoder;

/**
 * Handle all user's service for remote caller
 * 
 * @author Team 6
 */
@WebService(serviceName = "UserService")
public class UserService {
    
    UserDao userDao = new UserDaoImpl();
    CartDao cartDao = new CartDaoImpl();
    BookDao bookDao = new BookDaoImpl();
    TransactionDao transactionDao = new TransactionDaoImpl();

    /**
     * Web service operation: User login
     */
    @WebMethod(operationName = "login")
    public Boolean login(@WebParam(name = "tel") String tel, @WebParam(name = "password") String password) {
        
        
        return userDao.findUserByTelAndPassword(tel, password);
    }


    /**
     * Web service operation: Add books to user's cart
     */
    @WebMethod(operationName = "addToCart")
    public Boolean addToCart(@WebParam(name = "tel") String tel, @WebParam(name = "isbn") String isbn, @WebParam(name = "amount") int amount) {
        return cartDao.addToCart(tel, isbn, amount, true);
    }

    /**
     * Web service operation: Update user's address
     */
    @WebMethod(operationName = "updateAddress")
    public Boolean updateAddress(@WebParam(name = "tel") String tel, @WebParam(name = "address") String address) {
        return userDao.updateAddr(tel, address, true);
    }

    /**
     * Web service operation: Update user's card number
     */
    @WebMethod(operationName = "updateCard")
    public Boolean updateCard(@WebParam(name = "tel") String tel, @WebParam(name = "cardNumber") String cardNumber) {
        return userDao.updateCard(tel, cardNumber, true);
    }

    /**
     * Web service operation: Create transaction for user
     */
    @WebMethod(operationName = "createTransaction")
    public String createTransaction(@WebParam(name = "tel") String tel) {
        
        Map<String, Integer> cartInfo = cartDao.searchUserCart(tel);
        if (cartInfo.isEmpty()) return "Cart Empty";
        // Check stocks of books in user's cart
        for(Map.Entry<String, Integer> entry: cartInfo.entrySet()) {
            int stock = bookDao.searchStockByISBN(entry.getKey());
            if (stock - entry.getValue() < 0) return "Stock Not Enough";
        }
        // Calculate total amount of comsumption
        double totalConsumption = 0;
        for (Map.Entry<String, Integer> entry: cartInfo.entrySet()) {
            totalConsumption += (entry.getValue() * bookDao.searchPriceByISBN(entry.getKey())); 
        }
        // Create transaction, update user cart info, card info, and update book stock
        boolean result = transactionDao.createTransaction(tel, totalConsumption, cartInfo, true);
        if(result) return "Success";
        else return "Unknown Error";
        
    }

    /**
     * Web service operation: Search user's name
     */
    @WebMethod(operationName = "searchUserNameByTel")
    public String searchUserNameByTel(@WebParam(name = "tel") String tel) {
        return userDao.searchUserNameByTel(tel);
    }


    /**
     * Web service operation: Search users card
     */
    @WebMethod(operationName = "searchUserCardByTel")
    public String searchUserCardByTel(@WebParam(name = "tel") String tel) {
        return userDao.searchUserCardByTel(tel);
    }

    /**
     * Web service operation: Search user's address
     */
    @WebMethod(operationName = "searchUserAddrByTel")
    public String searchUserAddrByTel(@WebParam(name = "tel") String tel) {
        return userDao.searchUserAddrByTel(tel);
    }

    /**
     * Web service operation: Add new user (user registration)
     */
    @WebMethod(operationName = "addUser")
    public Boolean addUser(@WebParam(name = "tel") String tel, @WebParam(name = "name") String name, @WebParam(name = "passwd") String passwd, @WebParam(name = "card") String card, @WebParam(name = "addr") String addr) throws IOException {
        // Decrypt user credit card number and address
        String decryptedCard = new String((new BASE64Decoder()).decodeBuffer(card));
        String decryptedAddr = new String((new BASE64Decoder()).decodeBuffer(addr));
        return userDao.addUser(tel, name, passwd, decryptedCard, decryptedAddr, true);
    }

    /**
     * Web service operation: Update user's password
     */
    @WebMethod(operationName = "updatePasswd")
    public Boolean updatePasswd(@WebParam(name = "tel") String tel, @WebParam(name = "passwd") String passwd) {
        return userDao.updatePasswd(tel, passwd, true);
    }

    /**
     * Web service operation: Update user's name
     */
    @WebMethod(operationName = "updateName")
    public Boolean updateName(@WebParam(name = "tel") String tel, @WebParam(name = "name") String name) {
        return userDao.updateName(tel, name, true);
    }

    /**
     * Web service operation: Empty user's cart
     */
    @WebMethod(operationName = "deleteCart")
    public Boolean deleteCart(@WebParam(name = "tel") String tel) {
        return cartDao.deleteCart(tel, true);
    }

    /**
     * Web service operation: Modify user's cart
     */
    @WebMethod(operationName = "modifyUserCart")
    public Boolean modifyUserCart(@WebParam(name = "tel") String tel, @WebParam(name = "isbn") String isbn, @WebParam(name = "amount") int amount) {
        return cartDao.modifyUserCart(tel, isbn, amount, true);
    }

    /**
     * Web service operation: Search user's cart information
     */
    @WebMethod(operationName = "searchUserCart")
    public String searchUserCart(@WebParam(name = "tel") String tel) {
        
        // Get books in user cart
        Map<String, Integer> cartInfo = cartDao.searchUserCart(tel);
        // Store detailed cart information in map
        Map<String, String> cartFullInfo = new HashMap<String, String>();
        for (Map.Entry<String, Integer> entry: cartInfo.entrySet()) {
            String bookISBN = entry.getKey();
            int amount = entry.getValue();
            String title = bookDao.searchTitleByISBN(bookISBN);
            double price = bookDao.searchPriceByISBN(bookISBN);
            String bookInfo = title + "/" + price + "/" + amount;
            cartFullInfo.put(bookISBN, bookInfo);
        }
        Gson gson = new GsonBuilder().create();
        return gson.toJson(cartFullInfo);
    }

    /**
     * Web service operation: Handle returning items
     */
    @WebMethod(operationName = "returnItems")
    public Boolean returnItems(@WebParam(name = "tel") String tel, @WebParam(name = "txnId") String txnId) {
        Map<String, Integer> map = transactionDao.searchTransactionById(txnId);
        if (map == null) return false;
        return transactionDao.returnItems(tel, txnId, map, true);
    }

    /**
     * Web service operation: Search user's transactions
     */
    @WebMethod(operationName = "searchTransactions")
    public String searchTransactions(@WebParam(name = "tel") String tel) {
        Map<String, String> transactions = transactionDao.searchUserTransactions(tel);
        Gson gson = new GsonBuilder().create();
        return gson.toJson(transactions);
    }

    /**
     * Web service operation: Search book price given book ISBN number
     */
    @WebMethod(operationName = "searchBookPriceByISBN")
    public Double searchBookPriceByISBN(@WebParam(name = "isbn") String isbn) {
        return bookDao.searchPriceByISBN(isbn);
    }

    /**
     * Web service operation: Search book title given book ISBN number
     */
    @WebMethod(operationName = "searchBookTitleByISBN")
    public String searchBookTitleByISBN(@WebParam(name = "isbn") String isbn) {
        return bookDao.searchTitleByISBN(isbn);
    }

    /**
     * Web service operation: Search user's total consumption
     */
    @WebMethod(operationName = "searchUserConsumption")
    public Double searchUserConsumption(@WebParam(name = "tel") String tel) {
        return userDao.searchUserConsumption(tel);
    }

    /**
     * Web service operation: Handle user's receipt of books
     */
    @WebMethod(operationName = "receiveItems")
    public Boolean receiveItems(@WebParam(name = "id") String id) {
        return transactionDao.receiveItems(id, true);
    }


}
