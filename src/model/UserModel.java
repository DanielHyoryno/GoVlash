package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import database.Connect;

public class UserModel{

    private int userID;
    private String userName;
    private String userEmail;
    private String userPassword;
    private String userGender;
    private Date userDOB;
    private String userRole;

    // bridge ke database, sama kayak contoh dosen (db.executeUpdate / execQuery)
    private Connect db;

    // === Constructor kosong (dipakai Controller) ===
    public UserModel(){
        db = Connect.getInstance();
    }

    // === Constructor full (dipakai waktu SELECT / login) ===
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

    // ================== GETTER & SETTER ==================
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

    // =====================================================
    //  Insert data CUSTOMER (dipanggil controller)
    //  Mirip banget dengan insertUser() di contoh dosen
    // =====================================================
    public void insertCustomer(){
        // asumsi semua field sudah divalidasi dan di-set di controller
        String query = String.format(
            "INSERT INTO users (UserName, UserEmail, UserPassword, UserGender, UserDOB, UserRole) " +
            "VALUES ('%s', '%s', '%s', '%s', '%s', 'Customer')",
            userName, userEmail, userPassword, userGender, userDOB.toString()
        );

        db.execUpdate(query);   // sama style dengan contoh dosen
    }

    // Kalau kamu mau juga insert employee, bisa reuse:
    public void insertEmployee(){
        // di controller, userRole sudah di-set: "Admin"/"Laundry Staff"/"Receptionist"
        String query = String.format(
            "INSERT INTO users (UserName, UserEmail, UserPassword, UserGender, UserDOB, UserRole) " +
            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
            userName, userEmail, userPassword, userGender, userDOB.toString(), userRole
        );

        db.execUpdate(query);
    }

    // =====================================================
    //  Login user (dipakai AuthController)
    //  Pola mirip getLatestData() di foto dosen, tapi pakai WHERE
    // =====================================================
    public UserModel loginUser(String email, String password){
        if(email == null || email.trim().isEmpty() ||
           password == null || password.trim().isEmpty()){
            return null;
        }

        String query = "SELECT * FROM users " +
                       "WHERE UserEmail = '" + email.trim() + "' " +
                       "AND UserPassword = '" + password + "'";

        try{
            ResultSet rs = db.execQuery(query);

            if(rs.next()){
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

        }catch(SQLException e){
            e.printStackTrace();
        }

        return null;
    }
}
