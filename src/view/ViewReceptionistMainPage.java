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

public class ViewReceptionistMainPage {

    private UserModel currentUser;
    private BorderPane root;

    public ViewReceptionistMainPage(UserModel user) {
        this.currentUser = user;
        buildUI();
    }

    public Parent getRoot() { return root; }
    
    // Tampilkan dashboard utama resepsionis
    private void buildUI() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Receptionist Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label welcome = new Label("Welcome, " + currentUser.getUserName());

        Button btnPending = new Button("View Pending Transactions");
        Button btnLogout = new Button("Logout");

        btnPending.setPrefWidth(250);
        btnLogout.setPrefWidth(250);

        // Navigasi ke Pending Page
        btnPending.setOnAction(e -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(new Scene(new ViewPendingTransactionPage(currentUser).getRoot(), 800, 600));
            stage.centerOnScreen();
        });

        btnLogout.setOnAction(e -> handleLogout());

        VBox menu = new VBox(20, title, welcome, btnPending, btnLogout);
        menu.setAlignment(Pos.CENTER);
        root.setCenter(menu);
    }

    // Untuk resepsionis logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            ViewLoginPage loginView = new ViewLoginPage();
            new UserController(loginView);
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.centerOnScreen();
        } catch(Exception e) { e.printStackTrace(); }
    }
}