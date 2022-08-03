/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.impl;

import dao.BookDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.JDBCUtil;
import po.Book;

/**
 * Direct interaction with database with respect to Book (implementation)
 * 
 * @author Team 6
 */
public class BookDaoImpl implements BookDao {
    
    @Override
    public List<Book> findBooksByKeyword(String keyword, int limit, int offset) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        List<Book> books = new ArrayList<>();
        final String sql = "SELECT * FROM `book` WHERE `b_title` LIKE ? OR `b_author` LIKE ? ORDER BY `b_title` LIMIT ? OFFSET ?;";
        try {
            conn = JDBCUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setInt(3, limit);
            pstmt.setInt(4, offset);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString(rs.findColumn("b_isbn"));
                String title = rs.getString(rs.findColumn("b_title"));
                String authors = rs.getString(rs.findColumn("b_author"));
                String publisher = rs.getString(rs.findColumn("b_publisher"));
                String year = rs.getString(rs.findColumn("b_year"));
                int stock = rs.getInt(rs.findColumn("b_stock"));
                double price = rs.getDouble(rs.findColumn("b_price"));
                books.add(new Book(isbn, title, authors, publisher, year, stock, price));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) {
                try {
                pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return books;
    }
    
}
