package controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.UserModel;
import view.CustomerView;     
import view.LoginView;        
import view.RegisterView;     
import view.TransactionView;  

public class UserController {

    private UserModel model;
    private LoginView loginView;
    private RegisterView registerView;
    
    public UserController() {
        this.model = new UserModel();
    }

    // Constructor untuk Login Page
    public UserController(LoginView view) {
    	this.model = new UserModel();
        this.loginView = view;
        
        // Untuk mengaktifkan tombol login
        initLogin();
    }

    // Constructor untuk Register Page
    public UserController(RegisterView view) {
    	this.model = new UserModel();
        this.registerView = view;
        
        // Untuk mengaktifkan tombol register
        initRegister();
    }

    // Mengatur apa yang akan dilakukan di login page saat tombol ditekan
    private void initLogin() {
        loginView.btnLogin.setOnAction(e -> handleLogin());
        
        // Untuk pindah ke Register Page
        loginView.btnRegister.setOnAction(e -> {
            try {
                Stage stage = (Stage) loginView.getRoot().getScene().getWindow();
                RegisterView regView = new RegisterView();
                
                Scene scene = new Scene(regView.getRoot(), 400, 500);
                stage.setScene(scene);
                stage.setTitle("GoVlash Laundry - Register");
                stage.centerOnScreen();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // Inti proses login, dari validasi hingga pindah page
    private void handleLogin() {
        String email = loginView.txtEmail.getText();
        String password = loginView.txtPassword.getText();

        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            loginView.lblStatus.setText("Email and password must be filled.");
            return;
        }

        UserModel loggedUser = model.loginUser(email, password);

        if (loggedUser == null) {
            loginView.lblStatus.setText("Invalid email or password.");
            return;
        }

        // Jika login sukses, akan diarahkan ke Dashboard
        try {
            Stage stage = (Stage) loginView.getRoot().getScene().getWindow();
            Scene scene;
            
            // Untuk melakukan login sesuai role dan diarahkan ke dasboard yang sesuai
            if (loggedUser.getUserRole().equals("Customer")) {
            	// Mengarahkan user ke customer dashboard
                CustomerView custView = new CustomerView(loggedUser);
                scene = new Scene(custView.getRoot(), 800, 600);
                stage.setTitle("Customer Dashboard");
                
            } else if (loggedUser.getUserRole().equals("Admin")) {
            	// Mengarahkan user ke admin dashboard
                view.AdminView adminView = new view.AdminView(loggedUser);
                scene = new Scene(adminView.getRoot(), 800, 600);
                stage.setTitle("Admin Dashboard");
                
            } else if (loggedUser.getUserRole().equals("Receptionist")) {
            	// Mengarahkan user ke receptionist dashboard
                view.ReceptionistView recepView = new view.ReceptionistView(loggedUser);
                scene = new Scene(recepView.getRoot(), 800, 600);
                stage.setTitle("Receptionist Dashboard");
                
            } else {
            	// Untuk laundry staff
            	view.LaundryStaffView staffView = new view.LaundryStaffView(loggedUser);
                scene = new Scene(staffView.getRoot(), 800, 600);
                stage.setTitle("Laundry Staff Dashboard");
            }
            
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mengaktifkan tombol daftar dan tombol kembali ke halaman login.
    private void initRegister() {
        registerView.btnRegister.setOnAction(e -> handleRegister());
        registerView.btnBackToLogin.setOnAction(e -> registerView.openLoginPage());
    }
    
    // Mengatur pendaftaran user baru
    private void handleRegister() {
    	String name = registerView.txtName.getText();
        String email = registerView.txtEmail.getText();
        String password = registerView.txtPassword.getText();
        String confirm = registerView.txtConfirm.getText();
        String gender = registerView.cbGender.getValue();
        LocalDate dobValue = registerView.dpDOB.getValue();

        // Validasi 
        String validationError = validateAddCustomer(name, email, password, confirm, gender, (dobValue != null ? Date.valueOf(dobValue) : null));
        
        if (validationError != null) {
            registerView.lblStatus.setText(validationError);
            return;
        }
        
        // Insert ke database lewat model
        // Panggil method model yang menerima parameter
        model.addUser(name, email, password, gender, Date.valueOf(dobValue), "Customer");

        // Kalau sukses, return message sukses
        registerView.lblStatus.setText("Register success.");
        clearRegisterForm();
    }
    
    // Untuk membersihkan semua isi di form pendaftaran setelah proses selesai
    private void clearRegisterForm() {
        registerView.txtName.clear();
        registerView.txtEmail.clear();
        registerView.txtPassword.clear();
        registerView.txtConfirm.clear();
        registerView.cbGender.getSelectionModel().clearSelection();
        registerView.dpDOB.setValue(null);
    }
    
    // Ambil semua employee 
    public ArrayList<UserModel> getAllEmployees() {
        return model.getUsersByRole("Employee");
    }

    // Tambah employee baru
    public String addEmployee(String name, String email, String password, String confirm, String gender, Date dob, String role) {
        String validation = validateAddEmployee(name, email, password, confirm, gender, dob, role);
        
        if(validation != null) {
        	return validation;
        }

        model.addEmployee(name, email, password, gender, dob, role);
        return null;
    }
    
    // Validate data customer yang ditambah
    private String validateAddCustomer(String name, String email, String pass, String conf, String gender, Date dob) {
        if(email == null || !email.endsWith("@email.com")) {
        	return "Email must end with @email.com";
        }
        
        return validateCommon(name, email, pass, conf, gender, dob);
    }
    
    // Validasi data employee yang ditambah
    private String validateAddEmployee(String name, String email, String pass, String conf, String gender, Date dob, String role) {
        if(email == null || !email.endsWith("@govlash.com")) {
        	return "Email must end with @govlash.com";
        }
        
        if(getAgeFromDate(dob) < 17) {
        	return "Must be at least 17 years old.";
        }
        
        if(role == null) {
        	return "Role required.";
        }
        
        return validateCommon(name, email, pass, conf, gender, dob);
    }
    
    // Validasi input umum
    private String validateCommon(String name, String email, String pass, String conf, String gender, Date dob) {
        if(name == null || name.isEmpty()) {
        	return "Name required.";
        }
        
        if(pass == null || pass.length() < 6) {
        	return "Password min 6 chars.";
        }
        
        if(!pass.equals(conf)) {
        	return "Passwords mismatch.";
        }
        
        if(gender == null) {
        	return "Gender required.";
        }
        
        if(dob == null) {
        	return "DOB required.";
        }
        
        if(getAgeFromDate(dob) < 12) {
        	return "Must be at least 12 years old.";
        }
        return null;
    }

    // Hitung umur user
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