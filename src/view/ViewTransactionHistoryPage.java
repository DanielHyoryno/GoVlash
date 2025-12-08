package view;

import java.util.ArrayList;
import java.util.Date;

import controller.TransactionController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.TransactionModel;
import model.UserModel;

public class ViewTransactionHistoryPage {
	private UserModel currentUser;
    private BorderPane root;
    private TransactionController transController;
    private TableView<TransactionModel> table;

    public ViewTransactionHistoryPage(UserModel user) {
        this.currentUser = user;
        this.transController = new TransactionController();
        openTransactionHistoryPage();
        refreshData();
    }

    public Parent getRoot() { 
    	return root; 
    }
    
    // Tmapilan untuk liat history transaction
    private void openTransactionHistoryPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Transaction History");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No transactions yet"));

        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("Transaction ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, Date> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        
        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        
        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));
        
        table.getColumns().addAll(colID, colDate, colWeight, colStatus);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(e -> refreshData());

        Button btnBack = new Button("Back to Menu");
        btnBack.setOnAction(e -> goBack());

        VBox content = new VBox(15, title, table, btnRefresh, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }
    
    // Dapetin transaksi sesuai customer ID nya
    private ArrayList<TransactionModel> getTransactionsByCustomerID(int customerID) {
        return transController.getTransactionsByCustomerID(customerID);
    }
    
    // Untuk refresh page
    public void refreshData() {
        table.getItems().clear();

        // Panggil method lokal getTransactionsByCustomerId
        ArrayList<TransactionModel> data = getTransactionsByCustomerID(currentUser.getUserID());
        
        //Masukkan data ke tabel
        if (data != null) {
            table.getItems().addAll(data);
        }
    }

    // Kembali ke page "ViewCustomerHomePage"
    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewCustomerHomePage(currentUser).getRoot(), 700, 600));
    }
}
