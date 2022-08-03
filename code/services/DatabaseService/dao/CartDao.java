/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.Map;

/**
 * Direct interaction with database with respect to Cart
 * 
 * @author Team 6
 */
public interface CartDao {
    
    /**
     * Add books user has chosen to user's cart
     * 
     * @param tel user telephone number
     * @param isbn book ISBN
     * @param amount amount of books to be added
     * @param syn database synchronization control bit
     * @return true if books are added to cart successfully
     */
    boolean addToCart(String tel, String isbn, int amount, boolean syn);
    
    /**
     * Search user's cart
     * 
     * @param tel user telephone number
     * @return user's cart information
     */
    Map<String, Integer> searchUserCart(String tel);
    
    /**
     * Delete user's cart
     * 
     * @param tel user telephone number 
     * @param syn database synchronization control bit
     * @return true if user's cart deleted successfully
     */
    boolean deleteCart(String tel, boolean syn);
    
    /**
     * Modify user's cart
     * 
     * @param tel user telephone number
     * @param isbn book ISBN number
     * @param amount amount of book the be modified
     * @param syn database synchronization control bit
     * @return true if user's cart are modified successfully
     */
    boolean modifyUserCart(String tel, String isbn, int amount, boolean syn);
    
}
