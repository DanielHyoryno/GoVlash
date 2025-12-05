package view;

import java.util.ArrayList;

import controller.TransactionController;
import controller.UserController;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.TransactionModel;
import model.UserModel;

public class TransactionView {
	private UserModel currentUser;
    private TransactionController transController;
    private UserController userController; 

    private BorderPane root;
    private TableView<TransactionModel> table;
    
    public TransactionView(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        this.userController = new UserController();
        buildUI();
        loadData();
    }

    public Parent getRoot() {
        return root;
    }

    private void buildUI() {
        root = new BorderPane();
        root.setPadding(new Insets(10));

        Label title = new Label("Transaction Management - " + currentUser.getUserRole());
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        root.setTop(title);

        // Table
        table = new TableView<>();
        
        // Display Empty Message jika data kosong
        table.setPlaceholder(new Label("No transactions found")); 

        setupTableColumns();
        root.setCenter(table);

        HBox bottomBox = new HBox(10);
        bottomBox.setPadding(new Insets(10, 0, 0, 0));
        
        Button refreshBtn = new Button("Refresh / Show All");
        refreshBtn.setOnAction(e -> loadData());

        bottomBox.getChildren().add(refreshBtn);

        // Button sesuai role
        if (currentUser.getUserRole().equals("Receptionist")) {
            // Button Assign
            Button assignBtn = new Button("Assign to Laundry Staff");
            assignBtn.setOnAction(e -> handleAssign());
            bottomBox.getChildren().add(assignBtn);
            
        } else if (currentUser.getUserRole().equals("Laundry Staff")) {
            // Button Finish
            Button finishBtn = new Button("Mark as Finished");
            finishBtn.setOnAction(e -> handleFinish());
            bottomBox.getChildren().add(finishBtn);
            
        } else if (currentUser.getUserRole().equals("Admin")) {
            // Button Filter Finished
            Button filterBtn = new Button("Show Finished Transactions");
            filterBtn.setOnAction(e -> loadFinishedData());
            bottomBox.getChildren().add(filterBtn);
        }

        root.setBottom(bottomBox);
    }

    private void setupTableColumns() {
        TableColumn<TransactionModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("transactionID"));

        TableColumn<TransactionModel, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));
        
        TableColumn<TransactionModel, Double> weightCol = new TableColumn<>("Weight (Kg)");
        weightCol.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        
        // Kolom Laundry Staff ID (Agar admin/receptionist bisa lihat siapa yg handle)
        TableColumn<TransactionModel, Integer> staffCol = new TableColumn<>("Staff ID");
        staffCol.setCellValueFactory(new PropertyValueFactory<>("laundryStaffID"));

        table.getColumns().addAll(idCol, statusCol, weightCol, staffCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Load Default Data (All for Admin, Pending for Receptionist, Assigned for Staff)
    private void loadData() {
        table.getItems().clear();
        table.getItems().addAll(transController.getTransactionsByRole(currentUser.getUserRole(), currentUser.getUserID()));
    }

    // Load data yang sudah selesai (Khusus Admin Filter)
    private void loadFinishedData() {
        table.getItems().clear();
        table.getItems().addAll(transController.getFinishedTransactions());
    }

    private void handleAssign() {
        TransactionModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a transaction first.");
            return;
        }

        ArrayList<UserModel> allEmployees = userController.getAllEmployees(); 

        class StaffWrapper {
            UserModel user;
            StaffWrapper(UserModel u) { this.user = u; }
            
            @Override
            public String toString() {
                return user.getUserName() + " (ID:" + user.getUserID() + ")";
            }
        }

        // Filter Laundry Staff & Masukkan ke List
        ArrayList<StaffWrapper> staffChoices = new ArrayList<>();
        for(UserModel u : allEmployees) {
            if("Laundry Staff".equals(u.getUserRole())) {
                staffChoices.add(new StaffWrapper(u));
            }
        }

        ChoiceDialog<StaffWrapper> dialog = new ChoiceDialog<>(null, staffChoices);
        dialog.setTitle("Assign Staff");
        dialog.setHeaderText("Choose Laundry Staff for Order #" + selected.getTransactionID());
        dialog.setContentText("Staff:");
        
        dialog.showAndWait().ifPresent(wrapper -> {
            // Ambil user asli
            transController.assignOrderToLaundryStaff(selected, wrapper.user.getUserID(), currentUser.getUserID());
            loadData();
            showAlert("Assigned successfully.");
        });
    }

    // Kalau transaction sudah selesai
    private void handleFinish() {
        TransactionModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a transaction first.");
            return;
        }
        transController.updateTransactionStatus(selected.getTransactionID(), selected.getTransactionStatus());
        loadData();
        showAlert("Order marked as finished.");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
