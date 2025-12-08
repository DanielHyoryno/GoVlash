package view;

import java.util.ArrayList;

import controller.NotificationController;
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
import model.NotificationModel;
import model.UserModel;

public class ViewNotification {
	private UserModel currentUser;
    private BorderPane root;
    private NotificationController notifController;
    private TableView<NotificationModel> table;

    public ViewNotification(UserModel user) {
        this.currentUser = user;
        this.notifController = new NotificationController();
        
        buildUI();
        getNotificationsByRecipientID(currentUser.getUserID());
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan untuk customer liat notifikasi
    private void buildUI() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("My Notifications");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No notifications"));

        TableColumn<NotificationModel, String> colMsg = new TableColumn<>("Message");
        colMsg.setCellValueFactory(new PropertyValueFactory<>("notificationMessage"));
        
        TableColumn<NotificationModel, String> colRead = new TableColumn<>("Status");
        colRead.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().isRead() ? "Read" : "Unread")
        );

        table.getColumns().addAll(colMsg, colRead);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnDetail = new Button("View Detail");
        Button btnDelete = new Button("Delete");
        Button btnBack = new Button("Back to Menu");

        btnDetail.setOnAction(e -> openNotificationDetailPage());
        btnDelete.setOnAction(e -> deleteNotfication());
        btnBack.setOnAction(e -> goBack());

        HBox actions = new HBox(10, btnDetail, btnDelete);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, title, table, actions, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }
    
    // Dapet notification based recipient(customer) ID
    private ArrayList<NotificationModel> getNotificationsByRecipientID(int recipientID) {
        ArrayList<NotificationModel> list = notifController.getNotificationsByRecipientID(recipientID);
        
        // Masukkan ke Tabel
        table.getItems().clear();
        if (list != null) {
            table.getItems().addAll(list);
        }
        
        return list;
    }
    
    // Untul liat detail notifikasi nya
    private void openNotificationDetailPage() {
        NotificationModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a notification first.");
            return;
        }

        // Tampilkan Popup 
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification Detail");
        alert.setHeaderText("Sent at: " + selected.getCreatedAt());
        alert.setContentText(selected.getNotificationMessage());
        
        alert.getDialogPane().setPrefWidth(700); 
	    alert.getDialogPane().lookup(".content.label").setStyle("-fx-wrap-text: true;");
        
        alert.showAndWait();

        // Update status read
        if (!selected.isRead()) {
            notifController.markAsRead(selected.getNotificationID());
            getNotificationsByRecipientID(currentUser.getUserID());
        }
    }

    // Untuk deleteNotification
    private void deleteNotfication() {
        NotificationModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a notification first.");
            return;
        }
        notifController.deleteNotification(selected.getNotificationID());
        getNotificationsByRecipientID(currentUser.getUserID());
    }

    private void goBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(new ViewCustomerHomePage(currentUser).getRoot(), 700, 600));
    }
    
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}
