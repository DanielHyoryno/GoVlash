package controller;

import model.UserModel;
import view.LoginView;

public class AuthController{

    private UserModel model;
    private LoginView view;

    // Bridge antara View dan Model
    public AuthController(UserModel model, LoginView view){
        this.model = model;
        this.view = view;
        init();
    }

    private void init(){
        // asumsi di LoginView ada: public Button btnLogin;
        view.btnLogin.setOnAction(e ->{
            handleLogin();
        });
    }

    private void handleLogin(){

        String email = view.txtEmail.getText();
        String password = view.txtPassword.getText();

        if(email == null || email.trim().isEmpty() ||
           password == null || password.trim().isEmpty()){
            view.lblStatus.setText("Email and password must be filled.");
            return;
        }

        // panggil model -> cek ke database
        UserModel loggedUser = model.loginUser(email, password);

        if(loggedUser == null){
            view.lblStatus.setText("Invalid email or password.");
            return;
        }

        // login sukses
        view.lblStatus.setText("Login success.");

    }
}
