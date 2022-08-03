/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.Map;

/**
 * Direct interaction with database with respect to Transaction
 * 
 * @author Team 6
 */
public interface TransactionDao {
    
    /**
     * Create transaction according to user's ordering
     * 
     * @param tel user telephone number
     * @param consumption user consumption for the ordering
     * @param cartInfo user's cart information
     * @param syn database synchronization control bit
     * @return true if transaction created successfully
     */
    public boolean createTransaction(String tel, double consumption, Map<String, Integer> cartInfo, boolean syn);
    
    /**
     * Search the transaction information with given id
     * 
     * @param id transaction id
     * @return transaction information
     */
    public Map<String, Integer> searchTransactionById(String id);
    
    /**
     * Handle returning items
     * 
     * @param tel user telephone number
     * @param id transaction id to be canceled
     * @param items items to be returned
     * @param syn database synchronization control bit
     * @return true if items are returned successfully
     */
    public boolean returnItems(String tel, String id, Map<String, Integer> items, boolean syn);
    
    /**
     * Search user's past transactions
     * 
     * @param tel user telephone number
     * @return all user's past transactions
     */
    public Map<String, String> searchUserTransactions(String tel);
    
    /**
     * Update transaction status when user receives books
     * 
     * @param id transaction id
     * @param status transaction status to be updated
     * @return true if updated successfully
     */
    public boolean receiveItems(String id, boolean syn);
    
}
