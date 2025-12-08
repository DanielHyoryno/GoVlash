package view;

import controller.ServiceController;
import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ServiceModel;
import model.TransactionModel;
import model.UserModel;
import java.util.ArrayList;

public class ViewAssignedOrderPage {

    private UserModel currentUser;
    private BorderPane root;
    
    private TransactionController transController;
    private ServiceController serviceController; 

    private TableView<TransactionModel> tableAssigned;
    private Button btnFinish, btnRefresh, btnBack;
    
    private ArrayList<ServiceModel> serviceList;

    public ViewAssignedOrderPage(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        this.serviceController = new ServiceController();
        
        // Load data service untuk mapping nama
        this.serviceList = serviceController.getAllServices();
        
        openAssignedOrderPage();
        getAssignedOrdersByLaundryStaffID(currentUser.getUserID());
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan untuk staff melihat order yang di assign ke dia
    private void openAssignedOrderPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("My Assigned Orders");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Table Setup
        tableAssigned = new TableView<>();
        tableAssigned.setPlaceholder(new Label("No active jobs assigned to you"));
        
        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        // Nama Service
        TableColumn<TransactionModel, String> colService = new TableColumn<>("Service");
        colService.setCellValueFactory(cell -> {
            int sid = cell.getValue().getServiceID();
            return new javafx.beans.property.SimpleStringProperty(getServiceNameByID(sid));
        });
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight (Kg)");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        
        TableColumn<TransactionModel, String> colNotes = new TableColumn<>("Notes");
        colNotes.setCellValueFactory(new PropertyValueFactory<>("transactionNotes"));

        tableAssigned.getColumns().addAll(colID, colService, colWeight, colNotes);
        tableAssigned.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        btnFinish = new Button("Mark as Finished");
        btnRefresh = new Button("Refresh");
        btnBack = new Button("Back to Menu");

        HBox actions = new HBox(10, btnRefresh, btnFinish);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, title, tableAssigned, actions, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);

        btnRefresh.setOnAction(e -> getAssignedOrdersByLaundryStaffID(currentUser.getUserID()));
        btnFinish.setOnAction(e -> finishOrder());
        btnBack.setOnAction(e -> goBack());
    }

    // Mengambil data order milik staff tertentu dari database dan ditampilkan ke tabel
    private ArrayList<TransactionModel> getAssignedOrdersByLaundryStaffID(int staffID) {
        ArrayList<TransactionModel> list = transController.getAssignedOrdersByLaundryStaffID(staffID);
        
        // Update UI
        tableAssigned.getItems().clear();
        tableAssigned.getItems().addAll(list);
        
        return list;
    }
    
    // Untuk nandain kalau order sudah selesai dikerjain
    private void finishOrder() {
        TransactionModel selected = tableAssigned.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select a job to finish!");
            return;
        }

        // Kirim status baru "Finished"
        transController.updateTransactionStatus(selected.getTransactionID(), "Finished");
        
        showAlert(Alert.AlertType.INFORMATION, "Great job! Order finished.");
        getAssignedOrdersByLaundryStaffID(currentUser.getUserID()); // Refresh list
    }
    
    
    // Dapat service sesuai id nya
    private String getServiceNameByID(int id) {
        for (ServiceModel s : serviceList) {
            if (s.getServiceID() == id) {
            	return s.getServiceName();
            }
        }
        return "Unknown";
    }
    
    // Kembali ke page sebelumnya yaitu "ViewLaundryStaffMainPage"
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewLaundryStaffMainPage(currentUser).getRoot(), 700, 600));
        stage.centerOnScreen();
    }
    
    // Untuk nampilin pesan
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.show();
    }
}