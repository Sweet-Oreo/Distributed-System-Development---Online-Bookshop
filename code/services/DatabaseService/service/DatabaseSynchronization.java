/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import dao.BookDao;
import dao.CartDao;
import dao.TransactionDao;
import dao.UserDao;
import dao.impl.BookDaoImpl;
import dao.impl.CartDaoImpl;
import dao.impl.TransactionDaoImpl;
import dao.impl.UserDaoImpl;
import java.util.Map;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

/**
 * Synchronize database
 * 
 * @author Team 6
 */
@WebService(serviceName = "DatabaseSynchronization")
public class DatabaseSynchronization {
    
    UserDao userDao = new UserDaoImpl();
    CartDao cartDao = new CartDaoImpl();
    BookDao bookDao = new BookDaoImpl();
    TransactionDao transactionDao = new TransactionDaoImpl();


    /**
     * Web service operation: Synchronize database for emptying user cart
     */
    @WebMethod(operationName = "deleteCartSyn")
    public Boolean deleteCartSyn(@WebParam(name = "tel") String tel) {
        return cartDao.deleteCart(tel, false);
    }


    /**
     * Web service operation: Synchronize database for adding books to user's cart
     */
    @WebMethod(operationName = "addToCartSyn")
    public Boolean addToCartSyn(@WebParam(name = "tel") String tel, @WebParam(name = "isbn") String isbn, @WebParam(name = "amount") int amount) {
        return cartDao.addToCart(tel, isbn, amount, false);
    }

    /**
     * Web service operation: Synchronize database for updating user's address
     */
    @WebMethod(operationName = "updateAddrSyn")
    public Boolean updateAddrSyn(@WebParam(name = "tel") String tel, @WebParam(name = "addr") String addr) {
        return userDao.updateAddr(tel, addr, false);
    }

    /**
     * Web service operation: Synchronize database for updating user's card number
     */
    @WebMethod(operationName = "updateCardSyn")
    public Boolean updateCardSyn(@WebParam(name = "tel") String tel, @WebParam(name = "card") String card) {
        return userDao.updateCard(tel, card, false);
    }

    /**
     * Web service operation: Synchronize database for creating transaction
     */
    @WebMethod(operationName = "createTransactionSyn")
    public Boolean createTransactionSyn(@WebParam(name = "tel") String tel) {
       
        Map<String, Integer> cartInfo = cartDao.searchUserCart(tel);
        // Calculate total amount of comsumption
        double totalConsumption = 0;
        for (Map.Entry<String, Integer> entry: cartInfo.entrySet()) {
            totalConsumption += (entry.getValue() * bookDao.searchPriceByISBN(entry.getKey())); 
        }
        // Create transaction, update user cart info, card info, and update book stock
        boolean result = transactionDao.createTransaction(tel, totalConsumption, cartInfo, true);
        return result == true ? true : false;
    }

    /**
     * Web service operation: Synchronize database for adding new user
     */
    @WebMethod(operationName = "addUserSyn")
    public Boolean addUserSyn(@WebParam(name = "tel") String tel, @WebParam(name = "name") String name, @WebParam(name = "passwd") String passwd, @WebParam(name = "cartd") String cartd, @WebParam(name = "addr") String addr) {
        return userDao.addUser(tel, name, passwd, cartd, addr, false);
    }

    /**
     * Web service operation: Synchronize database for updating user's password
     */
    @WebMethod(operationName = "updatePasswdSyn")
    public Boolean updatePasswdSyn(@WebParam(name = "tel") String tel, @WebParam(name = "passwd") String passwd) {
        return userDao.updatePasswd(tel, passwd, false);
    }

    /**
     * Web service operation: Synchronize database for updating user's name
     */
    @WebMethod(operationName = "updateNameSyn")
    public Boolean updateNameSyn(@WebParam(name = "tel") String tel, @WebParam(name = "name") String name) {
        return userDao.updateName(tel, name, false);
    }

    /**
     * Web service operation: Synchronize database for returning items
     */
    @WebMethod(operationName = "returnItemsSyn")
    public Boolean returnItemsSyn(@WebParam(name = "tel") String tel, @WebParam(name = "txnId") String txnId) {
        Map<String, Integer> map = transactionDao.searchTransactionById(txnId);
        return transactionDao.returnItems(tel, txnId, map, false);
    }

    /**
     * Web service operation: Synchronize database for modifying user's cart
     */
    @WebMethod(operationName = "modifyUserCartSyn")
    public Boolean modifyUserCartSyn(@WebParam(name = "tel") String tel, @WebParam(name = "isbn") String isbn, @WebParam(name = "amount") int amount) {
        return cartDao.modifyUserCart(tel, isbn, amount, false);
    }

    /**
     * Web service operation: Synchronize database for updating transaction status when user receives items
     */
    @WebMethod(operationName = "receiveItemsSyn")
    public Boolean receiveItemsSyn(@WebParam(name = "id") String id) {
        return transactionDao.receiveItems(id, false);
    }

}
