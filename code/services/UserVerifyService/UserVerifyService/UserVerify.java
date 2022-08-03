/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserVerifyService;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import service.UserService;
import service.UserServiceCoop;
import service.UserServiceCoop_Service;
import service.UserService_Service;

/**
 * Verify User
 * 
 * @author Team 6
 */
@WebService(serviceName = "UserVerify")
public class UserVerify {
    
    /**
     * Web service operation: Verify user
     */
    @WebMethod(operationName = "verify")
    public Boolean verify(@WebParam(name = "tel") String tel, @WebParam(name = "passwd") String passwd) {
        // Call remote SOAP Database Service to verify user
        try {
            // Test whether one remote server is working, if it doesn't work, send request to another server
            HttpURLConnection connection = (HttpURLConnection) new URL(Destination.DATABASE_SERVICE_A).openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.connect();
            UserService_Service service = new UserService_Service();
            UserService port = service.getUserServicePort();            
            return port.login(tel, passwd);
        } catch (Exception e) {
            e.printStackTrace();
            UserServiceCoop_Service databaseService = new UserServiceCoop_Service();
            UserServiceCoop databasePort = databaseService.getUserServiceCoopPort();
            return databasePort.login(tel, passwd);
        }
    }
}
