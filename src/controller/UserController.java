package controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.UserModel;
import view.ViewCustomerHomePage;     
import view.ViewLoginPage;        
import view.ViewRegisterPage;     

public class UserController {

    private UserModel model;
    private ViewLoginPage loginView;
    private ViewRegisterPage registerView;
    
    public UserController() {
        this.model = new UserModel();
    }

    // Constructor untuk Login Page
    public UserController(ViewLoginPage view) {
    	this.model = new UserModel();
        this.loginView = view;
        
        // Untuk mengaktifkan tombol login
        initLogin();
    }

    // Constructor untuk Register Page
    public UserController(ViewRegisterPage view) {
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
                ViewRegisterPage regView = new ViewRegisterPage();
                
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
                view.ViewCustomerHomePage custView = new ViewCustomerHomePage(loggedUser);
                scene = new Scene(custView.getRoot(), 800, 600);
                stage.setTitle("Customer Dashboard");
                
            } else if (loggedUser.getUserRole().equals("Admin")) {
            	// Mengarahkan user ke admin dashboard
                view.ViewAdminMainPage adminView = new view.ViewAdminMainPage(loggedUser);
                scene = new Scene(adminView.getRoot(), 800, 600);
                stage.setTitle("Admin Dashboard");
                
            } else if (loggedUser.getUserRole().equals("Receptionist")) {
            	// Mengarahkan user ke receptionist dashboard
                view.ViewReceptionistMainPage recepView = new view.ViewReceptionistMainPage(loggedUser);
                scene = new Scene(recepView.getRoot(), 800, 600);
                stage.setTitle("Receptionist Dashboard");
                
            } else {
            	// Untuk laundry staff
            	view.ViewLaundryStaffMainPage staffView = new view.ViewLaundryStaffMainPage(loggedUser);
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
    
    // Untuk login
    public UserModel login(String email, String password) {
        return model.loginUser(email, password);
    }

    // Buat masukkin user
    public void addUser(String name, String email, String password, String confirmPassword, String gender, Date dob, String role) {
        // Model doesn't need confirmPassword, but Controller signature should match diagram
        model.addUser(name, email, password, gender, dob, role);
    }

    // Buat masukkin employee
    public void addEmployee(String name, String email, String password, String confirmPassword, String gender, Date dob, String role) {
        model.addEmployee(name, email, password, gender, dob, role);
    }

    // Dapat list user berdasarkan role
    public ArrayList<UserModel> getUsersByRole(String role) {
        return model.getUsersByRole(role);
    }

    // Helper untuk Admin View
    public ArrayList<UserModel> getAllEmployees() {
        return getUsersByRole("Employee");
    }

    // Dapat user berdasarkan email
    public UserModel getUserByEmail(String email) {
        return model.getUserByEmail(email); 
    }

    // Dapat user berdasarkan nama
    public UserModel getUserByName(String name) {
        return model.getUserByName(name);
    }
    
    // Validate data customer yang ditambah
    public String validateAddCustomer(String name, String email, String pass, String conf, String gender, Date dob) {
        if(email == null || !email.endsWith("@email.com")) {
        	return "Email must end with @email.com";
        }
        
        if(getAgeFromDate(dob) < 12) {
        	return "Must be at least 12 years old.";
        }
        
        return validateCommon(name, email, pass, conf, gender, dob);
    }
    
    // Validasi data employee yang ditambah
    public String validateAddEmployee(String name, String email, String pass, String conf, String gender, Date dob, String role) {
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