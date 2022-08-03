/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 * Direct interaction with database with respect to User
 * 
 * @author Team 6
 */
public interface UserDao {
    
    /**
     * Find user according to user's telephone number and password
     * 
     * @param tel user telephone number
     * @param password user password
     * @return true if user is found in database
     */
    boolean findUserByTelAndPassword(String tel, String password);
    
    /**
     * Add new users (user registration)
     * 
     * @param tel user telephone number 
     * @param name user name
     * @param passwd user password
     * @param card user card number
     * @param addr user address
     * @param syn database synchronization control bit
     * @return true if user is added successfully
     */
    boolean addUser(String tel, String name, String passwd, String card, String addr, boolean syn);
    
    /**
     * Search user's name
     * 
     * @param tel user telephone number
     * @return user's name
     */
    String searchUserNameByTel(String tel);
    
    /**
     * Search user's card number
     * 
     * @param tel user telephone number
     * @return user's card number
     */
    String searchUserCardByTel(String tel);
    
    /**
     * Search user's address
     * 
     * @param tel user's telephone number
     * @return user's address
     */
    String searchUserAddrByTel(String tel);
    
    /**
     * Update user's password
     * 
     * @param tel user telephone number
     * @param passwd user new telephone number
     * @param syn database synchronization control bit
     * @return true if password updated successfully
     */
    boolean updatePasswd(String tel, String passwd, boolean syn);
    
    /**
     * Update user's name
     * 
     * @param tel user telephone number
     * @param name user new name
     * @param syn database synchronization control bit
     * @return true if name updated successfully
     */
    boolean updateName(String tel, String name, boolean syn);
    
    /**
     * Update user's card number
     * 
     * @param tel user telephone number 
     * @param card user's card number 
     * @param syn database synchronization control bit
     * @return true if card updated successfully
     */
    boolean updateCard(String tel, String card, boolean syn);
    
    /**
     * Update user's address
     * 
     * @param tel user telephone number
     * @param addr user address
     * @param syn database synchronization control bit
     * @return true if address updated successfully
     */
    boolean updateAddr(String tel, String addr, boolean syn);
    
    /**
     * Search user's total consumption
     * 
     * @param tel use telephone number 
     * @return user's total consumption
     */
    double searchUserConsumption(String tel);
    
}
