package model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.Connect;

public class UserModel{

	protected int userID;       
    protected String userName;
    protected String userEmail;
    protected String userPassword;
    protected String userGender;
    protected Date userDOB;
    protected String userRole;

    protected Connect db;

    public UserModel(){
        db = Connect.getInstance();
    }

    public UserModel(int userID, String userName, String userEmail,
                     String userPassword, String userGender,
                     Date userDOB, String userRole){
        this();                 // init db juga
        this.userID = userID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userGender = userGender;
        this.userDOB = userDOB;
        this.userRole = userRole;
    }

    // Getter Setter
    public int getUserID(){
        return userID;
    }
    public void setUserID(int userID){
        this.userID = userID;
    }

    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getUserEmail(){
        return userEmail;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }

    public String getUserPassword(){
        return userPassword;
    }
    public void setUserPassword(String userPassword){
        this.userPassword = userPassword;
    }

    public String getUserGender(){
        return userGender;
    }
    public void setUserGender(String userGender){
        this.userGender = userGender;
    }

    public Date getUserDOB(){
        return userDOB;
    }
    public void setUserDOB(Date userDOB){
        this.userDOB = userDOB;
    }

    public String getUserRole(){
        return userRole;
    }
    public void setUserRole(String userRole){
        this.userRole = userRole;
    }
    
    // Validasi email duplikat
    public boolean isEmailExists(String email) {
        String query = String.format("SELECT * FROM users WHERE UserEmail = '%s'", email);
        ResultSet rs = db.execQuery(query);
        try {
            if (rs.next()) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Login user 
    public UserModel loginUser(String email, String password){
        if(email == null || email.trim().isEmpty() ||
           password == null || password.trim().isEmpty()){
            return null;
        }

        String query = "SELECT * FROM users " + 
        			   "WHERE UserEmail = ? " + 
        			   "AND UserPassword = ?";

        try{
        	PreparedStatement stmt = db.getConnection().prepareStatement(query);
        	stmt.setString(1, email);
        	stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("UserRole");
                
                // Return object sesuai role
                if ("Customer".equals(role)) {
                    return new CustomerModel(rs);
                } else if ("Admin".equals(role)) {
                    return new AdminModel(rs);
                } else if ("Receptionist".equals(role)) {
                    return new ReceptionistModel(rs);
                } else if ("Laundry Staff".equals(role)) {
                    return new LaundryStaffModel(rs);
                } else {
                	return null;
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }

    // Add User (Customer)
    public void addUser(String name, String email, String password, String gender, Date dob, String role) {
        String query = "INSERT INTO users (UserName, UserEmail, UserPassword, UserGender, UserDOB, UserRole) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, gender);
            stmt.setDate(5, dob);
            stmt.setString(6, role); // Usually "Customer"
            stmt.executeUpdate();
        } catch (SQLException e) { 
        	e.printStackTrace(); 
        }
    }

    // Add Employee
    public void addEmployee(String name, String email, String password, String gender, Date dob, String role) {
        addUser(name, email, password, gender, dob, role); // Reuse logic addUser
    }
    
    // Mengambil user berdasarkan role
    public ArrayList<UserModel> getUsersByRole(String role) {
        ArrayList<UserModel> list = new ArrayList<>();
        String query;

        // Jika request employee, ambil semua data kecuali customer
        if (role.equals("Employee")) {
            query = "SELECT * FROM users WHERE UserRole IN ('Admin', 'Laundry Staff', 'Receptionist') ORDER BY UserID DESC";
        } else {
            query = String.format("SELECT * FROM users WHERE UserRole = '%s'", role);
        }

        ResultSet rs = db.execQuery(query);
        try {
            while (rs.next()) {
                list.add(new UserModel(
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("UserEmail"),
                    rs.getString("UserPassword"),
                    rs.getString("UserGender"),
                    rs.getDate("UserDOB"),
                    rs.getString("UserRole")
                ));
            }
        } catch (SQLException e) { 
        	e.printStackTrace(); 
        }
        
        return list;
    }
    
    // Mencari user berdasarkan email yang terdaftar
    public UserModel getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE UserEmail = ?";
        
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setString(1, email);
            
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("UserRole");
                
                // Return object anak sesuai role
                if ("Customer".equals(role)) {
                	return new CustomerModel(rs);
                } else if ("Admin".equals(role)) {
                	return new AdminModel(rs);
                } else if ("Receptionist".equals(role)) {
                	return new ReceptionistModel(rs);
                } else if ("Laundry Staff".equals(role)) {
                	return new LaundryStaffModel(rs);
                }
                
                return new UserModel(
                    rs.getInt("UserID"), 
                    rs.getString("UserName"), 
                    rs.getString("UserEmail"), 
                    rs.getString("UserPassword"), 
                    rs.getString("UserGender"), 
                    rs.getDate("UserDOB"), 
                    rs.getString("UserRole")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Mencari user berdasarkan nama yang terdaftar
    public UserModel getUserByName(String name) {
        String query = "SELECT * FROM users WHERE UserName = ?";
        
        try {
            PreparedStatement stmt = db.getConnection().prepareStatement(query);
            stmt.setString(1, name);
            
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("UserRole");
                
                if ("Customer".equals(role)) {
                	return new CustomerModel(rs);
                } else if ("Admin".equals(role)) {
                	return new AdminModel(rs);
                } else if ("Receptionist".equals(role)) {
                	return new ReceptionistModel(rs);
                } else if ("Laundry Staff".equals(role)) {
                	return new LaundryStaffModel(rs);
                }
                
                return new UserModel(
                    rs.getInt("UserID"), 
                    rs.getString("UserName"), 
                    rs.getString("UserEmail"), 
                    rs.getString("UserPassword"), 
                    rs.getString("UserGender"), 
                    rs.getDate("UserDOB"), 
                    rs.getString("UserRole")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; 

    }
}
