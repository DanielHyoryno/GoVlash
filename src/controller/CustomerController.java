package controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import model.UserModel;
import view.CustomerView;

public class CustomerController{

    private UserModel model;
    private CustomerView view;

    // Bridge antara View dan Model
    public CustomerController(UserModel model, CustomerView view){
        this.model = model;
        this.view = view;
        init();
    }

    private void init(){
        view.btnRegister.setOnAction(e ->{
            handleRegister();
        });
    }

    private void handleRegister(){
        String name = view.txtName.getText();
        String email = view.txtEmail.getText();
        String password = view.txtPassword.getText();
        String confirm = view.txtConfirm.getText();
        String gender = view.cbGender.getValue();
        LocalDate dobValue = view.dpDOB.getValue();

        // ====== VALIDASI DI CONTROLLER ======

        if(name == null || name.trim().isEmpty() ||
           email == null || email.trim().isEmpty() ||
           password == null || password.isEmpty() ||
           confirm == null || confirm.isEmpty() ||
           gender == null || dobValue == null){
            view.lblStatus.setText("All fields must be filled.");
            return;
        }

        if(!email.endsWith("@email.com")){
            view.lblStatus.setText("Email must end with @email.com.");
            return;
        }

        if(password.length() < 6){
            view.lblStatus.setText("Password must be at least 6 characters.");
            return;
        }

        if(!password.equals(confirm)){
            view.lblStatus.setText("Confirm password must match.");
            return;
        }

        if(!gender.equals("Male") && !gender.equals("Female")){
            view.lblStatus.setText("Gender must be Male or Female.");
            return;
        }

        Date dob = Date.valueOf(dobValue);
        if(getAgeFromDate(dob) < 12){
            view.lblStatus.setText("Customer must be at least 12 years old.");
            return;
        }

        // ====== SET KE MODEL ======
        model.setUserName(name.trim());
        model.setUserEmail(email.trim());
        model.setUserPassword(password);
        model.setUserGender(gender);
        model.setUserDOB(dob);
        model.setUserRole("Customer");

        // ====== INSERT KE DB LEWAT MODEL ======
        // method ini sudah kamu buat di UserModel, mirip insertUser() dosen
        model.insertCustomer();

        // kalau mau lebih advanced, kamu bisa ubah insertCustomer() supaya return error string.
        view.lblStatus.setText("Register success.");
        clearForm();
    }

    private void clearForm(){
        view.txtName.clear();
        view.txtEmail.clear();
        view.txtPassword.clear();
        view.txtConfirm.clear();
        view.cbGender.getSelectionModel().clearSelection();
        view.dpDOB.setValue(null);
    }

    // hitung umur dari java.sql.Date (basic Calendar, materi Java standar)
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
