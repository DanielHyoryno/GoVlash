package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ReceptionistModel extends EmployeeModel {
	
	//Subclass EmployeeModel
    public ReceptionistModel(ResultSet rs) throws SQLException {
        super(
            rs.getInt("UserID"), 
            rs.getString("UserName"), 
            rs.getString("UserEmail"),
            rs.getString("UserPassword"),
            rs.getString("UserGender"),
            rs.getDate("UserDOB"), "Receptionist"
        );
    }
}