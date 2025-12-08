package view;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserModel;

public class ViewAdminMainPage {

    private UserModel currentUser;
    private BorderPane root;

    public ViewAdminMainPage(UserModel user) {
        this.currentUser = user;
        
        openAdminMainPage();
    }

    public Parent getRoot() {
        return root;
    }
    
    // Dashboard admin dengan beberapa option seperti "Manage Services","Manage Employees", dll
    private void openAdminMainPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label welcome = new Label("Welcome, " + currentUser.getUserName());

        Button btnServices = new Button("Manage Services");
        Button btnEmployees = new Button("Manage Employees");
        Button btnTransactions = new Button("View Transactions");
        Button btnLogout = new Button("Logout");

        btnServices.setPrefWidth(200);
        btnEmployees.setPrefWidth(200);
        btnTransactions.setPrefWidth(200);
        btnLogout.setPrefWidth(200);

        // Navigasi ke Page Lain
        btnServices.setOnAction(e -> navigateTo(new ViewServiceManagementPage(currentUser).getRoot()));
        btnEmployees.setOnAction(e -> navigateTo(new ViewEmployeeManagementPage(currentUser).getRoot()));
        btnTransactions.setOnAction(e -> navigateTo(new ViewTransactionPage(currentUser).getRoot()));
        btnLogout.setOnAction(e -> handleLogout());

        VBox menu = new VBox(15, title, welcome, btnServices, btnEmployees, btnTransactions, btnLogout);
        menu.setAlignment(Pos.CENTER);
        root.setCenter(menu);
    }
    
    // Untuk ngatur perpindahan ke page
    private void navigateTo(Parent page) {
        Stage stage = (Stage) root.getScene().getWindow();
        
        // Ganti isinya dengan page baru
        stage.setScene(new Scene(page, 700, 600));
        stage.centerOnScreen();
    }

    // Jika admin mau logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            ViewLoginPage loginView = new ViewLoginPage();
            new UserController(loginView);
            
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.centerOnScreen();
        } catch (Exception e) { 
        	e.printStackTrace(); 
        }
    }
}