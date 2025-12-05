package view;

import controller.TransactionController;
import controller.NotificationController; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ServiceModel;
import model.TransactionModel;
import model.UserModel;
import java.sql.Date;

public class AdminView {

	public UserModel currentUser;
    private BorderPane root;
    private TabPane tabPane;

    // Controllers
    public TransactionController transactionController;
    public NotificationController notificationController;

    // Service components
    public TableView<ServiceModel> tableService;
    public TextField txtServiceName;
    public TextField txtServicePrice;
    public TextField txtServiceDuration;
    public TextArea txtServiceDesc;
    
    public Button btnAddService;
    public Button btnUpdateService;
    public Button btnDeleteService;

    // Transaction components
    public TableView<TransactionModel> tableTrans;
    public Button btnShowAll;
    public Button btnFilterFinished;
    public Button btnSendNotif;

    public AdminView(UserModel user) {
        this.currentUser = user;
        this.transactionController = new TransactionController();
        this.notificationController = new NotificationController();
        
        buildUI();
        refreshTransactionData(false); // false = show all
    }

    public BorderPane getRoot() { 
    	return root; 
    }

    private void buildUI() {
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Manage Services
        Tab tabService = new Tab("Manage Services");
        ServiceView serviceView = new ServiceView(); 
        tabService.setContent(serviceView.getRoot());

        // Manage Employees
        Tab tabEmployee = new Tab("Manage Employees");
        EmployeeView empView = new EmployeeView(); 
        tabEmployee.setContent(empView.getRoot()); 

        // Transactions & Notification 
        Tab tabTrans = new Tab("Transactions");
        tabTrans.setContent(createTransactionContent());

        tabPane.getTabs().addAll(tabService, tabEmployee, tabTrans);

        // Logout
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(10));
        Button btnLogout = new Button("Logout");
        btnLogout.setOnAction(e -> handleLogout());
        topBar.getChildren().addAll(new Label("Admin: " + currentUser.getUserName()), btnLogout);

        root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(tabPane);
    }
    
    // Untuk section transactions
    private VBox createTransactionContent() {
        tableTrans = new TableView<>();
        tableTrans.setPlaceholder(new Label("No transactions"));
        
        TableColumn<TransactionModel, Integer> colID = new TableColumn<>("ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("transactionID"));
        
        TableColumn<TransactionModel, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("transactionStatus"));
        
        TableColumn<TransactionModel, Integer> colCust = new TableColumn<>("Cust ID");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        
        tableTrans.getColumns().addAll(colID, colStatus, colCust);
        tableTrans.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        btnShowAll = new Button("Show All");
        btnFilterFinished = new Button("Filter Finished");
        btnSendNotif = new Button("Send Notification");

        HBox actions = new HBox(10, btnShowAll, btnFilterFinished, btnSendNotif);
        actions.setAlignment(Pos.CENTER);

        // Events
        btnShowAll.setOnAction(e -> refreshTransactionData(false));
        btnFilterFinished.setOnAction(e -> refreshTransactionData(true));
        
        btnSendNotif.setOnAction(e -> {
            TransactionModel selectTrans = tableTrans.getSelectionModel().getSelectedItem();
            if(selectTrans == null) { 
            	showAlert("Select transaction first!"); 
            	return; 
            }
            
            if(!"Finished".equals(selectTrans.getTransactionStatus())) {
                showAlert("Only finished transactions can get notifications.");
                return;
            }
            
            // Auto Generated Message Logic
            String error = notificationController.sendNotification(selectTrans.getCustomerID());
            if(error == null) {
            	showAlert("Notification Sent: 'Your order is finished...'");
            } else {
            	showAlert(error);
            }
        });

        VBox layout = new VBox(10, tableTrans, actions);
        layout.setPadding(new Insets(10));
        return layout;
    }
    
    // Untuk refresh section transaction jika ada yang berubah
    private void refreshTransactionData(boolean onlyFinished) {
        tableTrans.getItems().clear();
        if(onlyFinished) {
            tableTrans.getItems().addAll(transactionController.getFinishedTransactions());
        } else {
            tableTrans.getItems().addAll(transactionController.getTransactionsByRole("Admin", 0));
        }
    }

    // Biar admin bisa logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) root.getScene().getWindow();
            LoginView loginView = new LoginView();
            
            Scene scene = new Scene(loginView.getRoot(), 400, 500);
            stage.setScene(scene);
            stage.setTitle("GoVlash Laundry - Login");
            stage.centerOnScreen();
        } catch(Exception e) { e.printStackTrace(); }
    }
    
    // Untuk alert
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}