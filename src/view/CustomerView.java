package view;

import controller.CustomerController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.NotificationModel;
import model.TransactionModel;
import model.UserModel;
import java.sql.Date;

public class CustomerView { // DASHBOARD UTAMA CUSTOMER

    public UserModel currentUser;
    private BorderPane root;
    
    // Components
    public ComboBox<String> cbServices;
    public TextField txtWeight;
    public TextArea txtNotes;
    public Button btnOrder;
    
    public TableView<TransactionModel> tableHistory;
    
    public TableView<NotificationModel> tableNotif;
    public Button btnViewNotif;
    public Button btnDeleteNotif;
    
    public Label lblStatus;
    public Button btnLogout;

    public CustomerView(UserModel user) {
        this.currentUser = user;
        buildUI();
        
        // Panggil Controller untuk mengisi data & event
        new CustomerController(this);
    }

    public BorderPane getRoot() { 
        return root; 
    }

    private void buildUI() {
        Label title = new Label("Welcome, " + currentUser.getUserName());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Form Order
        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(10));
        form.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5;");
        
        cbServices = new ComboBox<>(); 
        cbServices.setPromptText("Select Service");
        cbServices.setPrefWidth(200);
        
        txtWeight = new TextField(); txtWeight.setPromptText("Weight (Kg)");
        txtNotes = new TextArea(); txtNotes.setPromptText("Notes"); txtNotes.setPrefHeight(50);
        btnOrder = new Button("Place Order");

        form.add(new Label("Create Order"), 0, 0, 2, 1);
        form.add(new Label("Service:"), 0, 1); form.add(cbServices, 1, 1);
        form.add(new Label("Weight:"), 0, 2); form.add(txtWeight, 1, 2);
        form.add(new Label("Notes:"), 0, 3); form.add(txtNotes, 1, 3);
        form.add(btnOrder, 1, 4);

        // Table History
        Label lblHistory = new Label("Transaction History");
        lblHistory.setStyle("-fx-font-weight: bold;");
        
        tableHistory = new TableView<>();
        tableHistory.setPlaceholder(new Label("No transactions yet"));
        tableHistory.setPrefHeight(200);
        
        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));
        
        TableColumn<TransactionModel, Date> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));

        TableColumn<TransactionModel, Double> colWeight = new TableColumn<>("Weight");
        colWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        
        tableHistory.getColumns().addAll(colID, colStatus, colDate, colWeight);
        tableHistory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Section Notifications
        Label lblNotif = new Label("Notifications");
        lblNotif.setStyle("-fx-font-weight: bold;");

        tableNotif = new TableView<>();
        tableNotif.setPlaceholder(new Label("No notifications"));
        tableNotif.setPrefHeight(150);

        TableColumn<NotificationModel, String> colMsg = new TableColumn<>("Message");
        colMsg.setCellValueFactory(new PropertyValueFactory<>("notificationMessage"));
        
        TableColumn<NotificationModel, String> colRead = new TableColumn<>("Status");
        
        // Custom Cell Factory untuk menampilkan "Read" atau "Unread"
        colRead.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().isRead() ? "Read" : "Unread")
        );

        tableNotif.getColumns().addAll(colMsg, colRead);
        tableNotif.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        btnViewNotif = new Button("View Detail");
        btnDeleteNotif = new Button("Delete");
        
        // HBox untuk tombol notifikasi 
        HBox notifActions = new HBox(10, btnViewNotif, btnDeleteNotif);
        
        
        lblStatus = new Label();
        btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> handleLogout());

        VBox centerBox = new VBox(
                15, 
                title,
                form,
                new Separator(),
                lblHistory,
                tableHistory,
                new Separator(),
                lblNotif, 
                tableNotif, 
                notifActions, 
                lblStatus
        );
        centerBox.setPadding(new Insets(15));
        
        // Agar bisa discroll jika layar kecil
        ScrollPane scrollPane = new ScrollPane(centerBox);
        scrollPane.setFitToWidth(true);

        root = new BorderPane();
        root.setCenter(scrollPane);
        
        HBox topBar = new HBox(btnLogout);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(5));
        root.setTop(topBar);
    }
    
    // Untuk customer logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            view.LoginView loginView = new view.LoginView();
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.setTitle("Login");
            stage.centerOnScreen();
        } catch(Exception e) { e.printStackTrace(); }
    }
}