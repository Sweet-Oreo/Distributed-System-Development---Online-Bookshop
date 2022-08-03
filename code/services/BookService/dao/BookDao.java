/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import po.Book;

/**
 * Direct interaction with database with respect to Book
 * 
 * @author Team 6
 */
public interface BookDao {
    
    List<Book> findBooksByKeyword(String keyword, int limit, int offset);
    
}
