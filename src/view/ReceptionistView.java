package view;

import controller.TransactionController;
import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.TransactionModel;
import model.UserModel;
import java.util.ArrayList;

public class ReceptionistView {

    private UserModel currentUser;
    private BorderPane root;
    
    private TransactionController transController;
    private UserController userController; 

    private TableView<TransactionModel> tablePending;
    private Button btnAssign, btnRefresh, btnLogout;

    public ReceptionistView(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        this.userController = new UserController();
        
        buildUI();
        refreshData();
    }

    public BorderPane getRoot() { return root; }

    private void buildUI() {
        // Title
        Label title = new Label("Receptionist Dashboard - " + currentUser.getUserName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Tabel Pending Transactions
        tablePending = new TableView<>();
        tablePending.setPlaceholder(new Label("No pending transactions"));
        
        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, Integer> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        
        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));

        tablePending.getColumns().addAll(colID, colCust, colStatus, colWeight);
        tablePending.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        btnAssign = new Button("Assign to Laundry Staff");
        btnRefresh = new Button("Refresh");
        btnLogout = new Button("Logout");

        HBox actions = new HBox(10, btnRefresh, btnAssign);
        actions.setAlignment(Pos.CENTER);

        // Layout
        VBox centerBox = new VBox(15, title, new Label("Pending Transactions:"), tablePending, actions);
        centerBox.setPadding(new Insets(15));

        root = new BorderPane();
        root.setCenter(centerBox);
        
        HBox topBar = new HBox(btnLogout);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));
        root.setTop(topBar);

        btnRefresh.setOnAction(e -> refreshData());
        btnAssign.setOnAction(e -> handleAssign());
        btnLogout.setOnAction(e -> handleLogout());
    }

    private void refreshData() {
        tablePending.getItems().clear();
        
        // Ambil pending transactions aja
        tablePending.getItems().addAll(transController.getTransactionsByRole("Receptionist", currentUser.getUserID()));
    }
    
    // Mengatur untuk assign ke staff
    private void handleAssign() {
        TransactionModel selected = tablePending.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select a transaction first!");
            return;
        }
        
        // Panggil method untuk buka window kecil
        openAssignWindow(selected);
    }

    // PopUp
    private void openAssignWindow(TransactionModel transaction) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Assign Order #" + transaction.getTransactionID());
        popupStage.initModality(Modality.APPLICATION_MODAL); 

        // Data Staff
        ArrayList<UserModel> allEmployees = userController.getAllEmployees();
        ArrayList<String> staffNames = new ArrayList<>();
        ArrayList<UserModel> staffObjects = new ArrayList<>(); 

        for (UserModel u : allEmployees) {
            if ("Laundry Staff".equals(u.getUserRole())) {
            	
                // Tampilkan Nama + ID di ComboBox 
                staffNames.add(u.getUserName() + " (ID: " + u.getUserID() + ")");
                staffObjects.add(u);
            }
        }

        if (staffNames.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No Laundry Staff available!");
            return;
        }

        // PopUp untuk pilih staff
        Label lbl = new Label("Choose Laundry Staff:");
        ComboBox<String> cbStaff = new ComboBox<>();
        cbStaff.getItems().addAll(staffNames);
        cbStaff.setPromptText("Select Staff");

        Button btnConfirm = new Button("Assign");
        
        // Tombol Confirm
        btnConfirm.setOnAction(e -> {
            int idx = cbStaff.getSelectionModel().getSelectedIndex();
            if (idx < 0) {
                showAlert(Alert.AlertType.WARNING, "Please choose a staff!");
                return;
            }

            // Ambil ID dari list object yang kita simpan tadi
            int staffID = staffObjects.get(idx).getUserID();
            
            transController.assignOrderToLaundryStaff(transaction, staffID, currentUser.getUserID());
            
            // Sukses
            showAlert(Alert.AlertType.INFORMATION, "Order assigned successfully!");
            refreshData(); 
            popupStage.close();
        });

        // Layout Popup
        VBox layout = new VBox(15, lbl, cbStaff, btnConfirm);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        popupStage.setScene(scene);
        popupStage.show();
    }
    
    // Agar receptionist bisa logout
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