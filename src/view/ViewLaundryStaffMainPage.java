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

public class ViewLaundryStaffMainPage {

    private UserModel currentUser;
    private BorderPane root;

    public ViewLaundryStaffMainPage(UserModel user) {
        this.currentUser = user;
        
        openLaundryStaffMainPage();
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan utama laundry staff
    private void openLaundryStaffMainPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Laundry Staff Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label welcome = new Label("Welcome, " + currentUser.getUserName());

        Button btnViewTask = new Button("View Assigned Orders");
        Button btnLogout = new Button("Logout");

        btnViewTask.setPrefWidth(250);
        btnLogout.setPrefWidth(250);

        // Navigasi ke Assigned Orders Page (Sesuai Diagram ViewAssignedOrders Message 1)
        btnViewTask.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(new ViewAssignedOrderPage(currentUser).getRoot(), 700, 600));
            stage.centerOnScreen();
        });

        btnLogout.setOnAction(e -> handleLogout());

        VBox menu = new VBox(20, title, welcome, btnViewTask, btnLogout);
        menu.setAlignment(Pos.CENTER);
        root.setCenter(menu);
    }

    // Kalau laundry staff mau logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            ViewLoginPage loginView = new ViewLoginPage();
            new UserController(loginView);
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.centerOnScreen();
        } catch(Exception e) { 
        	e.printStackTrace(); 
        }
    }
}