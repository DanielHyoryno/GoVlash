package view;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.UserModel;

public class ViewLoginPage{

    public TextField txtEmail;
    public PasswordField txtPassword;
    public Button btnLogin;
    public Button btnRegister;
    public Label lblStatus;

    private BorderPane root;
    
    public BorderPane getRoot(){
        return root;
    }
    
    // Tmapilan login page
    public ViewLoginPage(){
        Label title = new Label("GoVlash Laundry - Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Form
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        Label lblEmail = new Label("Email:");
        Label lblPassword = new Label("Password:");

        txtEmail = new TextField();
        txtPassword = new PasswordField();

        btnLogin = new Button("Login");
        btnRegister = new Button("Register");

        form.add(lblEmail, 0, 0);
        form.add(txtEmail, 1, 0);
        form.add(lblPassword, 0, 1);
        form.add(txtPassword, 1, 1);
        form.add(btnLogin, 1, 2);
        form.add(btnRegister, 1, 3);

        lblStatus = new Label();

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.getChildren().addAll(title, form, lblStatus);

        root = new BorderPane();
        root.setCenter(centerBox);
        
        initController();
    }
    
    // Mengatur logika ke user model
    private void initController() {
        UserModel model = new UserModel();
        new controller.UserController(this);
    }
}
