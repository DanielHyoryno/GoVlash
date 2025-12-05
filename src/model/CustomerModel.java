package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerModel extends UserModel {
    
	// Subclass UserModel
    public CustomerModel(ResultSet rs) throws SQLException {
        super(); 
        
        this.userID = rs.getInt("UserID");
        this.userName = rs.getString("UserName");
        this.userEmail = rs.getString("UserEmail");
        this.userPassword = rs.getString("UserPassword");
        this.userGender = rs.getString("UserGender");
        this.userDOB = rs.getDate("UserDOB");
        this.userRole = "Customer";
    }
}
