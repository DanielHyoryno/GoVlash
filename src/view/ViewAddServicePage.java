package view;

import controller.ServiceController;
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

public class ViewAddServicePage {

    private UserModel currentUser;
    private BorderPane root;
    private ServiceController serviceController;
    
    private TextField txtName, txtPrice, txtDuration;
    private TextArea txtDesc;
    private Label lblError;

    public ViewAddServicePage(UserModel user) {
        this.currentUser = user;
        this.serviceController = new ServiceController();
        
        openAddNewServicePage();
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan saat akan add service baru
    private void openAddNewServicePage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Add New Service");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setAlignment(Pos.CENTER);

        txtName = new TextField();
        txtPrice = new TextField();
        txtDuration = new TextField();
        txtDesc = new TextArea(); txtDesc.setPrefHeight(60);
        lblError = new Label();

        form.addRow(0, new Label("Name:"), txtName);
        form.addRow(1, new Label("Price:"), txtPrice);
        form.addRow(2, new Label("Duration (Days):"), txtDuration);
        form.addRow(3, new Label("Description:"), txtDesc);

        Button btnSubmit = new Button("Add Service");
        Button btnCancel = new Button("Cancel");

        btnSubmit.setOnAction(e -> addService());
        btnCancel.setOnAction(e -> goBack());

        VBox content = new VBox(20, title, form, btnSubmit, btnCancel, lblError);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }
    
    // Untuk nambah service baru
    private void addService() {
        String name = txtName.getText();
        String price = txtPrice.getText();
        String duration = txtDuration.getText();
        String desc = txtDesc.getText();

        // Validasi
        String error = serviceController.validateAddService(name, desc, price, duration);

        if (error == null) {
            // Jika sukses, baru Add
            serviceController.addService(name, desc, price, duration);
            goBack(); // Kembali ke list service
        } else {
        	// Jika gagal, tampilkan error
            lblError.setText(error);
            lblError.setStyle("-fx-text-fill: red;");
        }
    }
    
    // Untuk kembali ke sebelumnya yaitu page "ViewServiceManagementPage"
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewServiceManagementPage(currentUser).getRoot(), 700, 600));
        stage.centerOnScreen();
    }
}