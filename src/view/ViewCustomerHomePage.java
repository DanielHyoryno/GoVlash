package view;

import java.util.ArrayList;

import controller.TransactionController;
import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.TransactionModel;
import model.UserModel;

public class ViewCustomerHomePage {

    private UserModel currentUser;
    private BorderPane root;
    private TabPane tabPane;

    public ViewCustomerHomePage(UserModel user) {
        this.currentUser = user;
        
        openCustomerHomePage();
    }

    public Parent getRoot() {
        return root;
    }

    // Tampilan utama untuk customer
    private void openCustomerHomePage() {
        root = new BorderPane();

        HBox header = new HBox(10);
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_RIGHT);
        
        Label lblUser = new Label("Hi, " + currentUser.getUserName());
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> handleLogout());
        
        header.getChildren().addAll(lblUser, btnLogout);
        root.setTop(header);

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Tab Order
        Tab tabOrder = new Tab("Order Service");
        // Kita panggil class ViewOrderServicePage lalu ambil root-nya
        ViewOrderServicePage orderPage = new ViewOrderServicePage(currentUser);
        tabOrder.setContent(orderPage.getRoot());

        // Tab History
        Tab tabHistory = new Tab("History");
        ViewTransactionHistoryPage historyPage = new ViewTransactionHistoryPage(currentUser);
        tabHistory.setContent(historyPage.getRoot());

        // Tab Notification
        Tab tabNotif = new Tab("Notifications");
        ViewNotification notifPage = new ViewNotification(currentUser);
        tabNotif.setContent(notifPage.getRoot());

        // Masukkan semua tab
        tabPane.getTabs().addAll(tabOrder, tabHistory, tabNotif);

        root.setCenter(tabPane);
    }
    
    // Untuk customer logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            ViewLoginPage loginView = new ViewLoginPage();
            new UserController(loginView);
            
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.setTitle("GoVlash Laundry - Login");
            stage.centerOnScreen();
        } catch (Exception e) { 
        	e.printStackTrace(); 
        }
    }
}