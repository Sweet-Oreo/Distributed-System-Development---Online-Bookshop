/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Construct JSON string for Book list
 * 
 * @author Team 6
 */
public class BookList extends ArrayList<Book> {
    
    public BookList(List<po.Book> books) {
        for (po.Book book : books) {
            this.add(book.vo());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Book book : this) {
            sb.append(book.toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return "[" + sb + "]";
    }
    
}
