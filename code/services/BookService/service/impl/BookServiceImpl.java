/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.impl;

import dao.BookDao;
import dao.impl.BookDaoImpl;
import po.Book;
import service.BookService;
import vo.BookList;

import java.util.List;

/**
 * Service related to Book (implementation)
 * 
 * @author Team 6
 */
public class BookServiceImpl implements BookService {
    
    private final BookDao dao = new BookDaoImpl();
    
    @Override
    public BookList findBooksByKeyword(String keyword, int page) {
        if (page < 1) {
            page = 1;
        }
        final int limit = 10;
        int offset = (page - 1) * limit;
        List<Book> books = dao.findBooksByKeyword(keyword, limit, offset);
        if (books.size() == 0) {
            return null;
        } else {
            return new BookList(books);
        }
    }
    
}
