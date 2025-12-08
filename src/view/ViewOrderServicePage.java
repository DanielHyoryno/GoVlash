package view;

import java.util.ArrayList;

import controller.ServiceController;
import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ServiceModel;
import model.UserModel;

public class ViewOrderServicePage {
	
	private UserModel currentUser;
    private BorderPane root;
    
    // Controllers
    private TransactionController transController;
    private ServiceController serviceController;
    
    private ComboBox<String> cbServices;
    private TextField txtWeight;
    private TextArea txtNotes;
    private Label lblStatus;
    
    private ArrayList<ServiceModel> serviceList;

    public ViewOrderServicePage(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        this.serviceController = new ServiceController();
        
        openOrderServicePage();
    }

    public Parent getRoot() { 
    	return root; 
    }
    
    // Tampilan untuk order service
    private void openOrderServicePage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Order Laundry Service");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setAlignment(Pos.CENTER);

        cbServices = new ComboBox<>();
        getAllServices(); // Load data service

        txtWeight = new TextField(); txtWeight.setPromptText("Weight (Kg)");
        txtNotes = new TextArea(); txtNotes.setPromptText("Notes"); txtNotes.setPrefHeight(60);
        lblStatus = new Label();

        form.addRow(0, new Label("Service:"), cbServices);
        form.addRow(1, new Label("Weight:"), txtWeight);
        form.addRow(2, new Label("Notes:"), txtNotes);

        Button btnSubmit = new Button("Submit Order");
        Button btnBack = new Button("Back to Menu");

        btnSubmit.setOnAction(e -> openOrderFormPage());
        btnBack.setOnAction(e -> goBack());

        VBox content = new VBox(20, title, form, btnSubmit, btnBack, lblStatus);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }

    // Untuk dapet semua service yang disediakan
    private ArrayList<ServiceModel> getAllServices() {
        serviceList = serviceController.getAllServices();
        
        cbServices.getItems().clear();
        
        if (serviceList != null) {
            for (ServiceModel s : serviceList) {
                cbServices.getItems().add(s.getServiceName() + " (Rp" + s.getServicePrice() + ")");
            }
        }
        
        return serviceList;
    }

    // Untuk customer order service
    private void openOrderFormPage() {
        int idx = cbServices.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            lblStatus.setText("Please select a service.");
            return;
        }
        
        int serviceID = serviceList.get(idx).getServiceID();
        String weight = txtWeight.getText();
        String notes = txtNotes.getText();
        
        // Cek validasi
        String error = transController.orderLaundryService(currentUser.getUserID(), serviceID, weight, notes);

        if (error == null) {
            lblStatus.setText("Order placed successfully!");
            lblStatus.setStyle("-fx-text-fill: green;");
            txtWeight.clear(); txtNotes.clear();
        } else {
            lblStatus.setText(error);
            lblStatus.setStyle("-fx-text-fill: red;");
        }
    }

    // Kembali ke page utama "ViewCustomerHomePage"
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewCustomerHomePage(currentUser).getRoot(), 700, 600));
    }
    
}
