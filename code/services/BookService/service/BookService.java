/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import vo.BookList;

/**
 * Service related to Book
 * 
 * @author Team 6
 */
public interface BookService {
    
    BookList findBooksByKeyword(String keyword, int page);
    
}
