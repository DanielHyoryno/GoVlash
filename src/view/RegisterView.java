package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserModel;

public class RegisterView {

    public TextField txtName;
    public TextField txtEmail;
    public PasswordField txtPassword;
    public PasswordField txtConfirm;
    public ComboBox<String> cbGender;
    public DatePicker dpDOB;
    
    public Button btnRegister;
    public Button btnBackToLogin;
    public Label lblStatus;

    private BorderPane root;

    public RegisterView(){
        // Title
        Label title = new Label("GoVlash Laundry - Register");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Form 
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        Label lblName = new Label("Name:");
        Label lblEmail = new Label("Email:");
        Label lblPassword = new Label("Password:");
        Label lblConfirm = new Label("Confirm:");
        Label lblGender = new Label("Gender:");
        Label lblDOB = new Label("DOB:");

        txtName = new TextField();
        txtEmail = new TextField();
        txtPassword = new PasswordField();
        txtConfirm = new PasswordField();
        
        cbGender = new ComboBox<>();
        cbGender.getItems().addAll("Male", "Female");
        
        dpDOB = new DatePicker();

        btnRegister = new Button("Register");
        btnBackToLogin = new Button("Back to Login");

        form.add(lblName, 0, 0);
        form.add(txtName, 1, 0);
        
        form.add(lblEmail, 0, 1);
        form.add(txtEmail, 1, 1);
        
        form.add(lblPassword, 0, 2);
        form.add(txtPassword, 1, 2);
        
        form.add(lblConfirm, 0, 3);
        form.add(txtConfirm, 1, 3);
        
        form.add(lblGender, 0, 4);
        form.add(cbGender, 1, 4);
        
        form.add(lblDOB, 0, 5);
        form.add(dpDOB, 1, 5);
        
        form.add(btnRegister, 1, 6);
        form.add(btnBackToLogin, 1, 7);

        lblStatus = new Label();

        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(20));
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.getChildren().addAll(title, form, lblStatus);

        root = new BorderPane();
        root.setCenter(centerBox);
        
        initController();
    }
    
    // Untuk mengaktifkan controller agar data tersimpan
    private void initController() {
        UserModel model = new UserModel();
        
        new controller.UserController(this);
    }
    
    // Kembali ke login page
    public void openLoginPage() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();

            LoginView loginView = new LoginView();

            Scene scene = new Scene(loginView.getRoot(), 400, 400); 
            stage.setScene(scene);
            stage.setTitle("GoVlash Laundry - Login");
            stage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public BorderPane getRoot(){
        return root;
    }
}