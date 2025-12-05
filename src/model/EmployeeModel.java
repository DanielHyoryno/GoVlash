package model;

import java.sql.Date;

public class EmployeeModel extends UserModel {
	
	// Subclass UserModel
	public EmployeeModel(int id, String name, String email, String pass, String gender, Date dob, String role) {
        super(
        	id, 
        	name, 
        	email, 
        	pass, 
        	gender, 
        	dob, 
        	role
        );
    }
}
