/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserVerifyService;

/**
 * Addresses of the remote Database Services
 * 
 * @author Team 6
 */
public interface Destination {
    
    public static final String DATABASE_SERVICE_A = "http://192.168.43.3:8080/DatabaseService/UserService?wsdl";
    
    public static final String DATABASE_SERVICE_B = "http://192.168.83.2:8080/DatabaseService/UserServiceCoop?wsdl";
    
}
