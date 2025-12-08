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
import model.ServiceModel;
import model.UserModel;

public class ViewEditServicePage {

    private UserModel currentUser;
    private ServiceModel serviceToEdit;
    private BorderPane root;
    private ServiceController serviceController;
    
    private TextField txtName, txtPrice, txtDuration;
    private TextArea txtDesc;
    private Label lblError;

    public ViewEditServicePage(UserModel user, ServiceModel service) {
        this.currentUser = user;
        this.serviceToEdit = service;
        this.serviceController = new ServiceController();
        
        openServiceManagementPage();
    }

    public Parent getRoot() { 
    	return root; 
    }
    
    // Tampilan edit service
    private void openServiceManagementPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Edit Service");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setAlignment(Pos.CENTER);

        txtName = new TextField(serviceToEdit.getServiceName());
        txtPrice = new TextField(String.valueOf(serviceToEdit.getServicePrice()));
        txtDuration = new TextField(String.valueOf(serviceToEdit.getServiceDuration()));
        txtDesc = new TextArea(serviceToEdit.getServiceDescription()); 
        txtDesc.setPrefHeight(60);
        
        lblError = new Label();

        form.addRow(0, new Label("Name:"), txtName);
        form.addRow(1, new Label("Price:"), txtPrice);
        form.addRow(2, new Label("Duration (Days):"), txtDuration);
        form.addRow(3, new Label("Description:"), txtDesc);

        Button btnUpdate = new Button("Update Service");
        Button btnCancel = new Button("Cancel");

        btnUpdate.setOnAction(e -> updateService());
        btnCancel.setOnAction(e -> goBack());

        VBox content = new VBox(20, title, form, btnUpdate, btnCancel, lblError);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }
    
    // Saat update button di klik
    private void updateService() {
        String name = txtName.getText();
        String price = txtPrice.getText();
        String duration = txtDuration.getText();
        String desc = txtDesc.getText();

        // Validasi
        String error = serviceController.validateEditService(name, desc, price, duration);

        if (error == null) {
            // Jika valid, jalankan Update
            serviceController.editService(serviceToEdit.getServiceID(), name, desc, price, duration);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Service updated successfully!");
            alert.showAndWait();
            
            goBack();
        } else {
        	// Jika gagal, tampilkan message error
            lblError.setText(error);
            lblError.setStyle("-fx-text-fill: red;");
        }
    }

    // Kmebali ke page sebelumnya yaitu "ViewServiceManagementPage"
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewServiceManagementPage(currentUser).getRoot(), 700, 600));
        stage.centerOnScreen();
    }
}