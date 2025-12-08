package model;

import java.sql.Date;

public class EmployeeModel extends UserModel {
	
	// Subclass UserModel
	public EmployeeModel(int id, String name, String email, String pass, String gender, Date dob, String role) {
		super();
		
        this.userID = id;
        this.userName = name;
        this.userEmail = email;
        this.userPassword = pass;
        this.userGender = gender;
        this.userDOB = dob;
        this.userRole = role;
    }
}
