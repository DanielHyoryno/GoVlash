package view;

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
import model.TransactionModel;
import model.UserModel;
import java.util.ArrayList;

public class ViewPendingTransactionPage {

    private UserModel currentUser;
    private BorderPane root;
    private TransactionController transController;
    private TableView<TransactionModel> table;

    public ViewPendingTransactionPage(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        
        openPendingTransactionPage();
        getTransactionsByStatus("Pending");
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan untuk lihat transaction yangs edang berjalan
    private void openPendingTransactionPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Pending Transactions");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No pending transactions"));

        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, Integer> colCust = new TableColumn<>("Customer ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        
        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));

        table.getColumns().addAll(colID, colCust, colStatus, colWeight);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnAssign = new Button("Assign to Laundry Staff");
        Button btnBack = new Button("Back to Menu");

        btnAssign.setOnAction(e -> openAssignOrderPage());
        
        btnBack.setOnAction(e -> goBack());

        HBox actions = new HBox(10, btnAssign);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, title, table, actions, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }

    // Dapat transaksi sesuai statusnya -> "pending" atau "finished"
    private ArrayList<TransactionModel> getTransactionsByStatus(String status) {
        ArrayList<TransactionModel> list = transController.getTransactionsByStatus(status);

        table.getItems().clear();
        table.getItems().addAll(list);
        
        return list;
    }

    // Unutk assign order ke staff
    private void openAssignOrderPage() {
        TransactionModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a transaction first!");
            return;
        }
        
        // Membuka ViewAssignOrderPage 
        new ViewAssignOrderPage(currentUser, selected, this);
    }

    // Refresh tabel setelah sukses
    public void refreshTable() {
        getTransactionsByStatus("Pending");
    }
    
    // Kembali ke page sebelumnya
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewReceptionistMainPage(currentUser).getRoot(), 700, 600));
        stage.centerOnScreen();
    }
    
    // Untuk menampilkan message
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}