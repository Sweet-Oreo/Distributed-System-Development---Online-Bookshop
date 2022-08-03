/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao.impl;

import dao.UserDao;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.DatabaseSynchronizationCoop;
import service.DatabaseSynchronizationCoop_Service;
import service.Destination;
import util.JDBCUtil;

/**
 * Direct interaction with database with respect to User (implementation)
 * 
 * @author Team 6
 */
public class UserDaoImpl implements UserDao{
    
    @Override
    public boolean findUserByTelAndPassword(String tel, String password) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT * FROM `user` WHERE `u_tel` = ? AND `u_passwd` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
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
        return true;
    }

    @Override
    public boolean addUser(String tel, String name, String passwd, String card, String addr, boolean syn) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        try {
	    conn = JDBCUtil.getConnection();
            String check = "SELECT * FROM `user` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(check);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return false;
	    String sql = "INSERT INTO `user` (`u_tel`, `u_name`, `u_passwd`, `u_addr`, `u_card`) VALUES (?, ?, ?, ?, ?);";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            pstmt.setString(2, name);
            pstmt.setString(3, passwd);
            pstmt.setString(4, addr);
            pstmt.setString(5, card);
            result = pstmt.executeUpdate();
             
            // Synchronize remote database
            if (syn) {
                // Check whether the remote database is working
                HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DESTINATION).openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                // If the remote database is working, synchronize remote database
                try {
                    connection.connect();
                    DatabaseSynchronizationCoop_Service service = new DatabaseSynchronizationCoop_Service();
                    DatabaseSynchronizationCoop port = service.getDatabaseSynchronizationCoopPort();
                    port.addUserSyn(tel, name, passwd, card, addr);
                } catch (Exception e) {
                    return result == 1;
                } 
            }
            return result == 1;
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

    @Override
    public String searchUserNameByTel(String tel) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `u_name` FROM `user` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString(1);
            else return null;
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
    public String searchUserCardByTel(String tel) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `u_card` FROM `user` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString(1);
            else return null;
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
    public String searchUserAddrByTel(String tel) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `u_addr` FROM `user` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString(1);
            else return null;
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
    public boolean updatePasswd(String tel, String passwd, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "UPDATE `user` SET `u_passwd` = ? WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, passwd);
            pstmt.setString(2, tel);
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
                    port.updatePasswdSyn(tel, passwd);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return result == 1;
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

    @Override
    public boolean updateName(String tel, String name, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "UPDATE `user` SET `u_name` = ? WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, tel);
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
                    port.updateNameSyn(tel, name);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return result == 1;
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

    @Override
    public boolean updateCard(String tel, String card, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "UPDATE `user` SET `u_card` = ? WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, card);
            pstmt.setString(2, tel);
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
                    port.updateCardSyn(tel, card);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return result == 1;
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

    @Override
    public boolean updateAddr(String tel, String addr, boolean syn) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        int result = 0;
        
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "UPDATE `user` SET `u_addr` = ? WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, addr);
            pstmt.setString(2, tel);
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
                    port.updateAddrSyn(tel, addr);
                } else {
                    System.err.println("Remote Database is down...");
                }
            }
            
            return result == 1;
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

    @Override
    public double searchUserConsumption(String tel) {
        
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
	    conn = JDBCUtil.getConnection();
	    String sql = "SELECT `u_consumption` FROM `user` WHERE `u_tel` = ?;";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tel);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
            else return 0.0;
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
        return 0.0;
        
    }
}
