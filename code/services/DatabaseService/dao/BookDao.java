/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 * Direct interaction with database with respect to Book
 * 
 * @author Team 6
 */
public interface BookDao {
    
    /**
     * Search stock of book with specific ISBN
     * 
     * @param isbn book ISBN number
     * @return stock of the book
     */
    public int searchStockByISBN(String isbn);
    
    
    /**
     * Search price of book with specific ISBN
     * 
     * @param isbn book ISBN number
     * @return price of the book
     */
    public double searchPriceByISBN(String isbn);
    
    /**
     * Search title of book with specific ISBN
     * 
     * @param isbn book ISBN number
     * @return title of the book
     */
    public String searchTitleByISBN(String isbn);
    
    
}
