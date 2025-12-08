package view;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserModel;
import java.sql.Date;

public class ViewAddEmployeePage {

    private UserModel currentUser;
    private BorderPane root;
    private UserController userController;

    private TextField txtName, txtEmail;
    private PasswordField txtPass, txtConfirm;
    private ComboBox<String> cbGender, cbRole;
    private DatePicker dpDOB;
    private Label lblError;

    public ViewAddEmployeePage(UserModel user) {
        this.currentUser = user;
        this.userController = new UserController();
        
        openAddNewEmployeePage();
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan untuk nambah employee baru
    private void openAddNewEmployeePage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Add New Employee");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setAlignment(Pos.CENTER);

        txtName = new TextField();
        txtEmail = new TextField();
        txtPass = new PasswordField();
        txtConfirm = new PasswordField();
        
        cbGender = new ComboBox<>();
        cbGender.getItems().addAll("Male", "Female");
        
        cbRole = new ComboBox<>();
        cbRole.getItems().addAll("Admin", "Laundry Staff", "Receptionist");
        
        dpDOB = new DatePicker();
        lblError = new Label();

        form.addRow(0, new Label("Name:"), txtName);
        form.addRow(1, new Label("Email:"), txtEmail);
        form.addRow(2, new Label("Password:"), txtPass);
        form.addRow(3, new Label("Confirm Password:"), txtConfirm);
        form.addRow(4, new Label("Gender:"), cbGender);
        form.addRow(5, new Label("Role:"), cbRole);
        form.addRow(6, new Label("Date of Birth:"), dpDOB);

        Button btnAdd = new Button("Add Employee");
        Button btnCancel = new Button("Cancel");

        btnAdd.setOnAction(e -> addUser());
        btnCancel.setOnAction(e -> goBack());

        VBox content = new VBox(20, title, form, btnAdd, btnCancel, lblError);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }
    
    // Untuk nambah user saat tombol "Add Employee" ditekan
    private void addUser() {
        String name = txtName.getText();
        String email = txtEmail.getText();
        String pass = txtPass.getText();
        String confirm = txtConfirm.getText();
        String gender = cbGender.getValue();
        String role = cbRole.getValue();
        Date dob = (dpDOB.getValue() == null) ? null : Date.valueOf(dpDOB.getValue());

        // Validasi data
        String error = userController.validateAddEmployee(name, email, pass, confirm, gender, dob, role);

        if (error == null) {
            // Jika valid, add user
            userController.addEmployee(name, email, pass, confirm, gender, dob, role);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Employee added successfully!");
            alert.showAndWait();
            
            goBack();
        } else {
        	// Jika gagal, tmapilkan error
            lblError.setText(error);
            lblError.setStyle("-fx-text-fill: red;");
        }
    }
    
    // Untuk back ke page "ViewEmployeeManagementPage" setelah pencet cancel atau sudah suskes
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewEmployeeManagementPage(currentUser).getRoot(), 800, 600));
        stage.centerOnScreen();
    }
}