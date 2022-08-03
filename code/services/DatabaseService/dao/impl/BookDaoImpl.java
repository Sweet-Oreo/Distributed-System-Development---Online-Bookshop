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
import util.JDBCUtil;

/**
 * Direct interaction with database with respect to Book (implementation)
 * 
 * @author Team 6
 */
public class BookDaoImpl implements BookDao {

    @Override
    public int searchStockByISBN(String isbn) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `b_stock` FROM `book` WHERE `b_isbn` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getInt(1);
            }
            return -1;
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
        return -1;
    }

    @Override
    public double searchPriceByISBN(String isbn) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `b_price` FROM `book` WHERE `b_isbn` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getDouble(1);
            }
            return 0;
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
        return 0;
    }

    @Override
    public String searchTitleByISBN(String isbn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `b_title` FROM `book` WHERE `b_isbn` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()){
                return rs.getString(1);
            }
            return null;
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
        return null;
    }
}
