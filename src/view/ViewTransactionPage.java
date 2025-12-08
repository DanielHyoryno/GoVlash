package view;

import java.util.ArrayList;

import controller.NotificationController;
import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TransactionModel;
import model.UserModel;

public class ViewTransactionPage {

    private UserModel currentUser;
    private BorderPane root;
    
    private TransactionController transController;
    private NotificationController notifController;
    
    private TableView<TransactionModel> table;

    public ViewTransactionPage(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        this.notifController = new NotificationController();
        
        openTransactionPage();
        getAllTransactions();
    }

    public Parent getRoot() { 
    	return root; 
    }

    private void openTransactionPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Transaction Report");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Table Setup
        table = new TableView<>();
        table.setPlaceholder(new Label("No transactions found"));

        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, Integer> colStaff = new TableColumn<>("Staff ID");
        colStaff.setCellValueFactory(new PropertyValueFactory<>("laundryStaffID"));
        
        TableColumn<TransactionModel, Integer> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight (Kg)");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        
        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));

        table.getColumns().addAll(colID, colStaff, colCust, colWeight, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnShowAll = new Button("Show All");
        Button btnFilterFinished = new Button("Filter Finished");
        Button btnSendNotif = new Button("Send Notification");
        Button btnBack = new Button("Back to Menu");

        btnShowAll.setOnAction(e -> getAllTransactions());
        btnFilterFinished.setOnAction(e -> showFinishedTransaction());
        btnSendNotif.setOnAction(e -> sendNotification());
        btnBack.setOnAction(e -> goBack());

        HBox actions = new HBox(10, btnShowAll, btnFilterFinished, btnSendNotif);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, title, table, actions, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }

    // Dapetin semua transactions
    private ArrayList<TransactionModel> getAllTransactions() {
        ArrayList<TransactionModel> list = transController.getAllTransactions();

        table.getItems().clear();
        table.getItems().addAll(list);
        
        return list;
    }

    // Dapetin transaction based on status
    private void getTransactionsByStatus(String status) {
        table.getItems().clear();
        table.getItems().addAll(transController.getTransactionsByStatus(status));
    }
    
    // Tampilin transaction yang sudah selesai
    private void showFinishedTransaction() {
        table.getItems().clear();
        // Panggil method controller yang namanya sudah kita perbaiki tadi
        table.getItems().addAll(transController.getTransactionsByStatus("Finished"));
    }

    // Kirim notifikasi dari admin ke customer
    private void sendNotification() {
        TransactionModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Select a transaction first!");
            return;
        }

        if (!"Finished".equals(selected.getTransactionStatus())) {
            showAlert(Alert.AlertType.WARNING, "Only finished transactions can receive notifications.");
            return;
        }

        // Kirim notifikasi
        String message = "Your order is finished and ready for pickup. Thank you for choosing our service!";
        String error = notifController.sendNotification(selected.getCustomerID(), message);
        if (error == null) {
            showAlert(Alert.AlertType.INFORMATION, "Notification sent to Customer ID " + selected.getCustomerID());
        } else {
            showAlert(Alert.AlertType.ERROR, error);
        }
    }

    // Kembali ke page "ViewAdminMainPage"
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewAdminMainPage(currentUser).getRoot(), 700, 600));
        stage.centerOnScreen();
    }

    // Tampilkan message
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setContentText(msg);
        alert.show();
    }
}