package controller;

import java.sql.*;

import database.Connect;
import model.UserModel;

public class EmployeeController{

    private Connect db;

    public EmployeeController(){
        db = Connect.getInstance();
    }

    public ArrayList<UserModel> getAllEmployees(){
        ArrayList<UserModel> list = new ArrayList<>();
        String query = "SELECT * FROM users " +
                       "WHERE UserRole IN ('Admin','Laundry Staff','Receptionist') " +
                       "ORDER BY UserID DESC";

        ResultSet rs = db.execQuery(query);

        try{
            while(rs.next()){
                UserModel u = new UserModel(
                    rs.getInt("UserID"),
                    rs.getString("UserName"),
                    rs.getString("UserEmail"),
                    rs.getString("UserPassword"),
                    rs.getString("UserGender"),
                    rs.getDate("UserDOB"),
                    rs.getString("UserRole")
                );
                list.add(u);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return list;
    }

    // return null kalau sukses, kalau ada error return pesan error
    public String addEmployee(String name, String email, String password,
                              String confirm, String gender, Date dob,
                              String role){

        if(name == null || name.trim().isEmpty()){
            return "Name cannot be empty.";
        }

        if(email == null || !email.endsWith("@govlash.com")){
            return "Email must end with @govlash.com.";
        }

        if(password == null || password.length() < 6){
            return "Password must be at least 6 characters.";
        }

        if(!password.equals(confirm)){
            return "Confirm password must match.";
        }

        if(!"Male".equals(gender) && !"Female".equals(gender)){
            return "Gender must be Male or Female.";
        }

        if(!"Admin".equals(role) &&
           !"Laundry Staff".equals(role) &&
           !"Receptionist".equals(role)){
            return "Role must be Admin, Laundry Staff, or Receptionist.";
        }

        if(dob == null){
            return "Date of birth must be filled.";
        }

        // cek umur minimal 17 tahun
        if(getAgeFromDate(dob) < 17){
            return "Employee must be at least 17 years old.";
        }

        try{
            Connection conn = db.getConnection();

            // cek unique username
            String checkName = "SELECT COUNT(*) FROM users WHERE UserName = ?";
            PreparedStatement psName = conn.prepareStatement(checkName);
            psName.setString(1, name.trim());
            ResultSet rsName = psName.executeQuery();
            if(rsName.next() && rsName.getInt(1) > 0){
                return "Username already exists.";
            }

            // cek unique email
            String checkEmail = "SELECT COUNT(*) FROM users WHERE UserEmail = ?";
            PreparedStatement psEmail = conn.prepareStatement(checkEmail);
            psEmail.setString(1, email.trim());
            ResultSet rsEmail = psEmail.executeQuery();
            if(rsEmail.next() && rsEmail.getInt(1) > 0){
                return "Email already exists.";
            }

            String insert = "INSERT INTO users " +
                    "(UserName, UserEmail, UserPassword, UserGender, UserDOB, UserRole) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement psInsert = conn.prepareStatement(insert);
            psInsert.setString(1, name.trim());
            psInsert.setString(2, email.trim());
            psInsert.setString(3, password);
            psInsert.setString(4, gender);
            psInsert.setDate(5, dob);
            psInsert.setString(6, role);
            psInsert.executeUpdate();

        }catch(SQLException e){
            e.printStackTrace();
            return "Database error while inserting employee.";
        }

        return null;
    }

    // hitung umur dari java.sql.Date
    private int getAgeFromDate(Date dob){
        Calendar birth = Calendar.getInstance();
        birth.setTime(dob);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if(today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        return age;
    }
}
