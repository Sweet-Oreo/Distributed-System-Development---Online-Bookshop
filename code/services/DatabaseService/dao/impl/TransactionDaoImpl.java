/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.BookDao;
import dao.TransactionDao;
import dao.UserDao;
import java.io.IOException;
import java.lang.reflect.Type;
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
 * Direct interaction with database with respect to Transaction (implementation)
 * 
 * @author Team 6
 */
public class TransactionDaoImpl implements TransactionDao{

    @Override
    public boolean createTransaction(String tel, double consumption, Map<String, Integer> cartInfo, boolean syn) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false);
            // Insert an item for the transaction in database
            StringBuilder JSON = new StringBuilder("{");
            for (Map.Entry<String, Integer> entry: cartInfo.entrySet()) {
                JSON.append("\"" + entry.getKey() + "\": " +entry.getValue() + ", ");
            }
            JSON.delete(JSON.length() - 2, JSON.length());
            JSON.append("}");
	    String insertTransaction = "INSERT INTO `txn` (`u_tel`, `t_books`, `t_amount`) VALUES (?, ?, ?);";
            pstmt = conn.prepareStatement(insertTransaction);
            pstmt.setString(1, tel);
            pstmt.setString(2, JSON.toString());
            pstmt.setDouble(3, consumption);
            pstmt.executeUpdate();
            // Empty user cart
            String deleteUserCart = "DELETE from `cart` where `u_tel` = ?;";
            pstmt = conn.prepareStatement(deleteUserCart);
            pstmt.setString(1, tel);
            pstmt.executeUpdate();
            // Update user's consumption
            UserDao userDao = new UserDaoImpl();
            double currentConsumption = userDao.searchUserConsumption(tel);
            String updateUserCard = "UPDATE `user` SET `u_consumption` = ? where `u_tel` = ?";
            pstmt = conn.prepareStatement(updateUserCard);
            pstmt.setDouble(1, currentConsumption + consumption);
            pstmt.setString(2, tel);
            pstmt.executeUpdate();
            // Update the stock of books, namely, decrease the stock of book
            BookDao bookDao = new BookDaoImpl();
            for (Map.Entry<String, Integer> entry: cartInfo.entrySet()) {
                int stock = bookDao.searchStockByISBN(entry.getKey());
                String updateBookStock = "UPDATE `book` SET `b_stock` = ? where `b_isbn` = ?";
                pstmt = conn.prepareStatement(updateBookStock);
                pstmt.setInt(1, stock - entry.getValue());
                pstmt.setString(2, entry.getKey());
                pstmt.executeUpdate();
            }
            // If all the updates are successfully done, commit the transaction
            conn.commit();
            
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
                    port.createTransactionSyn(tel);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return true;
	} catch (SQLException e) {			
	    try {
                conn.rollback();
                return false;
            } catch(Exception ex) {
                e.printStackTrace();
                return false;
            }
	} catch (MalformedURLException ex) {
            Logger.getLogger(TransactionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TransactionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
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
    public Map<String, Integer> searchTransactionById(String id) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `t_books` FROM `txn` WHERE `t_id` = ?";
            pstmt = conn.prepareStatement(sql);
            int idInt = Integer.parseInt(id);
            pstmt.setInt(1, idInt);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Change map to Json format for remote transmission
                Gson gson = new Gson();
                Map<String, Integer> map = new HashMap<String, Integer>();
                Type typeOfHashMap = new TypeToken<Map<String, Integer>>() { }.getType();
                map = gson.fromJson(rs.getString(1), typeOfHashMap);
                return map;
            } else return null;
	} catch (SQLException e) {			
	    e.printStackTrace();
            return null;
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
    }

    @Override
    public boolean returnItems(String tel, String id, Map<String, Integer> items, boolean syn) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            // Search for user's current consumption
	    conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false);
            String searchConsumption = "SELECT `t_amount` FROM `txn` WHERE `t_id` = ?;";
            pstmt = conn.prepareStatement(searchConsumption);
            int idInt = Integer.parseInt(id);
            pstmt.setInt(1, idInt);
            ResultSet rs = pstmt.executeQuery();
            double consumption = 0;
            if (rs.next()) {
                consumption = rs.getDouble(1);
            } else return false;
            // Return money back to user's bank account, namely, reduce user's consumpiton
            UserDao userDao = new UserDaoImpl();
            double currentConsumption = userDao.searchUserConsumption(tel);
            String updateUserCard = "UPDATE `user` SET `u_consumption` = ? where `u_tel` = ?";
            pstmt = conn.prepareStatement(updateUserCard);
            pstmt.setDouble(1, currentConsumption - consumption);
            pstmt.setString(2, tel);
            pstmt.executeUpdate();
            // Update book stock, namely, increase the book stock
            BookDao bookDao = new BookDaoImpl();
            for (Map.Entry<String, Integer> entry: items.entrySet()) {
                int stock = bookDao.searchStockByISBN(entry.getKey());
                String updateBookStock = "UPDATE `book` SET `b_stock` = ? where `b_isbn` = ?";
                pstmt = conn.prepareStatement(updateBookStock);
                pstmt.setInt(1, stock + entry.getValue());
                pstmt.setString(2, entry.getKey());
                pstmt.executeUpdate();
            }
            // Update transaction status
            String updateTxnStatus = "UPDATE `txn` SET `t_status` = ? WHERE `t_id` = ?;";
            pstmt = conn.prepareStatement(updateTxnStatus);
            pstmt.setInt(1, 2);
            pstmt.setInt(2, idInt);
            pstmt.executeUpdate();
            // If all the updates are successfully done, commit the transaction
            conn.commit();
            
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
                    port.returnItemsSyn(tel, id);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
           
            return true;
        } catch (SQLException e) {			
            try {
                conn.rollback();
                return false;
            } catch (SQLException ex) {
                Logger.getLogger(TransactionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
	} catch (MalformedURLException ex) {
            Logger.getLogger(TransactionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TransactionDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
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
    public Map<String, String> searchUserTransactions(String tel) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `t_id`, `t_books`, `t_amount`, `t_status`, `t_time` FROM `txn` WHERE `u_tel` = ? ORDER BY `t_status` ASC, `t_time` DESC;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            Map<String, String> transactions = new HashMap<String, String>();
            while (rs.next()) {
                String txnId = rs.getInt(1) + "";
                String txnInfo = rs.getString(2) + "/" + rs.getDouble(3) + "/" + rs.getInt(4) + "/" + rs.getTimestamp(5);
                transactions.put(txnId, txnInfo);
            }
            return transactions;
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

    @Override
    public boolean receiveItems(String id, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        
        try {
	    conn = JDBCUtil.getConnection();
            String check = "SELECT `t_status` FROM `txn` WHERE `t_id` = ?;";
            pstmt = conn.prepareStatement(check);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) == 2) return false;
            }
            
	    String sql = "UPDATE `txn` SET `t_status` = ? WHERE `t_id` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, 1);
            pstmt.setString(2, id);
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
                    port.receiveItems(id);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return true;
	} catch (SQLException e) {			
	    return false;
	} catch (MalformedURLException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
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
