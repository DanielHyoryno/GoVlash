package view;

import controller.ServiceController;
import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ServiceModel;
import model.TransactionModel;
import model.UserModel;
import java.util.ArrayList;

public class LaundryStaffView {

    private UserModel currentUser;
    private BorderPane root;
    
    private TransactionController transController;
    private ServiceController serviceController; 

    private TableView<TransactionModel> tableAssigned;
    private Button btnFinish, btnRefresh, btnLogout;
    
    private ArrayList<ServiceModel> serviceList;

    public LaundryStaffView(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        this.serviceController = new ServiceController();
        
        // Load data service
        this.serviceList = serviceController.getAllServices();
        
        buildUI();
        refreshData();
    }

    public BorderPane getRoot() { return root; }

    private void buildUI() {
        // Header
        Label title = new Label("Laundry Staff - " + currentUser.getUserName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Table
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
        btnLogout = new Button("Logout");

        HBox actions = new HBox(10, btnRefresh, btnFinish);
        actions.setAlignment(Pos.CENTER);

        // Layout
        VBox centerBox = new VBox(15, title, new Label("Your Job List:"), tableAssigned, actions);
        centerBox.setPadding(new Insets(15));

        root = new BorderPane();
        root.setCenter(centerBox);
        
        HBox topBar = new HBox(btnLogout);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        btnRefresh.setOnAction(e -> refreshData());
        btnFinish.setOnAction(e -> handleFinish());
        btnLogout.setOnAction(e -> handleLogout());
    }

    private void refreshData() {
        tableAssigned.getItems().clear();
        tableAssigned.getItems().addAll(transController.getTransactionsByRole("Laundry Staff", currentUser.getUserID()));
    }
    
    // Jika transaction sudah selesai
    private void handleFinish() {
        TransactionModel selected = tableAssigned.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select a job to finish!");
            return;
        }

        // Panggil Controller untuk update status
        transController.updateTransactionStatus(selected.getTransactionID(), selected.getTransactionStatus());
        
        showAlert(Alert.AlertType.INFORMATION, "Great job! Order finished.");
        refreshData(); // Refresh agar data hilang dari list pending
    }

    private String getServiceNameByID(int id) {
        for (ServiceModel s : serviceList) {
            if (s.getServiceID() == id) return s.getServiceName();
        }
        return "Unknown";
    }
    
    // Untuk staff logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            LoginView loginView = new LoginView();
            new controller.UserController(loginView);
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.setTitle("Login");
            stage.centerOnScreen();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.show();
    }
}