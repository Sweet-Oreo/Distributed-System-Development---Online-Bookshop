/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.impl;

import dao.CartDao;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.DatabaseSynchronizationCoop;
import service.DatabaseSynchronizationCoop_Service;
import service.Destination;
import util.JDBCUtil;

/**
 * Direct interaction with database with respect to Cart (implementation)
 * 
 * @author Team 6
 */
public class CartDaoImpl implements CartDao{

    @Override
    public boolean addToCart(String tel, String isbn, int amount, boolean syn) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "INSERT INTO `cart` (`u_tel`, `b_isbn`, `c_amount`) VALUES (?, ?, ?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            pstmt.setString(2, isbn);
            pstmt.setInt(3, amount);
            result = pstmt.executeUpdate();
            
            // Synchronize remote database
            if (syn) {
                // Check whether the remote database is working
                HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DESTINATION).openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                // If the remote database is working, synchronize remote database
		if (HttpURLConnection.HTTP_OK==connection.getResponseCode()) {
                    DatabaseSynchronizationCoop_Service service = new DatabaseSynchronizationCoop_Service();
                    DatabaseSynchronizationCoop port = service.getDatabaseSynchronizationCoopPort();
                    port.addToCartSyn(tel, isbn, amount);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return result == 1;
	} catch (SQLException e) {			
	    return false;
	} catch (MalformedURLException ex) {
            Logger.getLogger(CartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
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
        return true;
    }

    @Override
    public Map<String, Integer> searchUserCart(String tel) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
            String sql = "SELECT `b_isbn`, `c_amount` FROM `cart` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            Map<String, Integer> cartInfo = new HashMap<String, Integer>();
            while (rs.next()) {
                String isbn = rs.getString(1);
                int amount = rs.getInt(2);
                cartInfo.put(isbn, amount);
            }
            return cartInfo;
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
        return new HashMap<String, Integer>();
    }

    @Override
    public boolean deleteCart(String tel, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "DELETE FROM `cart` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            pstmt.executeUpdate();
            
            // Synchronize remote database
            if (syn) {
                // Check whether the remote database is working
                HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DESTINATION).openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                // If the remote database is working, synchronize remote database
		if (HttpURLConnection.HTTP_OK==connection.getResponseCode()) {
                    DatabaseSynchronizationCoop_Service service = new DatabaseSynchronizationCoop_Service();
                    DatabaseSynchronizationCoop port = service.getDatabaseSynchronizationCoopPort();
                    port.deleteCartSyn(tel);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return true;
	} catch (SQLException e) {			
	    return false;
	} catch (MalformedURLException ex) {
            Logger.getLogger(CartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
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
        return true;
    }

    @Override
    public boolean modifyUserCart(String tel, String isbn, int amount, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            // If the amount of book remained is not 0, update the number of book with given ISBN in user cart
            if (amount != 0) {
                conn = JDBCUtil.getConnection();
                String sql = "UPDATE `cart` SET `c_amount` = ? WHERE `u_tel` = ? and `b_isbn` = ?;";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, amount);
                pstmt.setString(2, tel);
                pstmt.setString(3, isbn);
                pstmt.executeUpdate();
                
                // Synchronize remote database
                if (syn) {
                    // Check whether the remote database is working
                    HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DESTINATION).openConnection();
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    // If the remote database is working, synchronize remote database
                    if (HttpURLConnection.HTTP_OK==connection.getResponseCode()) {
                        DatabaseSynchronizationCoop_Service service = new DatabaseSynchronizationCoop_Service();
                        DatabaseSynchronizationCoop port = service.getDatabaseSynchronizationCoopPort();
                        port.modifyUserCartSyn(tel, isbn, amount);
                    } else {
                        System.err.println("Remote Database is down...");
                    }
                }
                
            } else {  // If the amount of book remained is 0, delete the book with given ISBN in user cart
                conn = JDBCUtil.getConnection();
                String sql = "DELETE FROM `cart` WHERE `u_tel` = ? AND `b_isbn` = ?;";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, tel);
                pstmt.setString(2, isbn);
                pstmt.executeUpdate();
                
                // Synchronize remote database
                if (syn) {
                    // Check whether the remote database is working
                    HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DESTINATION).openConnection();
                    connection.setConnectTimeout(1000);
                    connection.setReadTimeout(1000);
                    // If the remote database is working, synchronize remote database
                    if (HttpURLConnection.HTTP_OK==connection.getResponseCode()) {
                        DatabaseSynchronizationCoop_Service service = new DatabaseSynchronizationCoop_Service();
                        DatabaseSynchronizationCoop port = service.getDatabaseSynchronizationCoopPort();
                        port.modifyUserCartSyn(tel, isbn, amount);
                    } else {
                        System.err.println("Remote Database is down...");
                    }
                }
                
            }
            
            return true;
	} catch (SQLException e) {			
	    return false;
	} catch (MalformedURLException ex) {
            Logger.getLogger(CartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CartDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
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
        return true;
        
    }
    
}
