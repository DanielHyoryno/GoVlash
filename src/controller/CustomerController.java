package controller;

import java.util.ArrayList;
import java.util.Optional;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.NotificationModel;
import model.ServiceModel;
import model.TransactionModel;
import view.CustomerView;
import view.LoginView;

public class CustomerController {

    private CustomerView view;
    
    // Sub-controllers / Models
    private TransactionController transController;
    private ServiceController serviceController;
    private NotificationController notifController;
    
    private ArrayList<ServiceModel> serviceList;

    public CustomerController(CustomerView view) {
        this.view = view;
        this.transController = new TransactionController();
        this.serviceController = new ServiceController();
        this.notifController = new NotificationController();
        
        initData();
        initEvents();
    }
    
    // Load services ke ComboBox
    private void initData() {
        serviceList = serviceController.getAllServices();
        view.cbServices.getItems().clear();
        
        for (ServiceModel s : serviceList) {
            view.cbServices.getItems().add(s.getServiceName() + " (Rp" + s.getServicePrice() + ")");
        }

        // Load tables
        refreshHistoryTable();
        refreshNotificationTable();
    }

    private void initEvents() {
        view.btnOrder.setOnAction(e -> handleOrder());
        view.btnLogout.setOnAction(e -> handleLogout());
        
        // Notification Events
        view.btnViewNotif.setOnAction(e -> handleViewNotification());
        view.btnDeleteNotif.setOnAction(e -> handleDeleteNotification());
    }

    // Untuk mengatur order yang masuk dari customer
    private void handleOrder() {
        view.lblStatus.setText("");
        
        // Validasi service yang dipilih
        int idx = view.cbServices.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            setError("Please select a service first.");
            return;
        }

        // Ambil data order yang dimasukkan
        int customerID = view.currentUser.getUserID();
        int serviceID = serviceList.get(idx).getServiceID();
        String weight = view.txtWeight.getText();
        String notes = view.txtNotes.getText();

        // Panggil TransactionController untuk menyimpan data ke database
        String error = transController.orderLaundryService(customerID, serviceID, weight, notes);

        if (error == null) {
            // Kalau berhasil, masuk database 
            view.lblStatus.setText("Order placed successfully!");
            
            // Clear form
            view.txtWeight.clear();
            view.txtNotes.clear();
            view.cbServices.getSelectionModel().clearSelection();
            
            // Refresh Table History (Newest to Oldest)
            refreshHistoryTable();
        } else {
        	// Kalau gagal, muncul error nya
            setError(error);
        }
    }
    
    // Ini akan mengambil data transaksi milik user yang sedang login,
    // lalu tableHistory akan di refresh
    private void refreshHistoryTable() {
        ArrayList<TransactionModel> history = transController.getTransactionsByRole("Customer", view.currentUser.getUserID());
        view.tableHistory.getItems().clear();
        view.tableHistory.getItems().addAll(history);
    }

    // Untuk mengambil daftar notifikasi milik user dari database.
    private void refreshNotificationTable() {
        // Load data notifikasi dan ambil berdasarkan ID
        ArrayList<NotificationModel> notifs = notifController.getNotificationsByRecipientID(view.currentUser.getUserID());
        view.tableNotif.getItems().clear();
        view.tableNotif.getItems().addAll(notifs);
    }
    
    // 
    private void handleViewNotification() {
        NotificationModel selected = view.tableNotif.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setError("Select a notification to view details.");
            return;
        }

        // Menampilkan 
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        
        alert.setTitle("Notification Detail");
        alert.setHeaderText("Sent at: " + selected.getCreatedAt());
        alert.setContentText(selected.getNotificationMessage());
        alert.showAndWait();
        
//      alert.getDialogPane().setPrefWidth(400); 
//      alert.getDialogPane().lookup(".content.label").setStyle("-fx-wrap-text: true;");

        // Jika status masih unread, update jadi Read 
        if (!selected.isRead()) {
            notifController.markAsRead(selected.getNotificationID());
            
            // Refresh tabel biar status berubah jadi "Read"
            refreshNotificationTable();
        }
    }
    
    // Menghapus notifikasi yang dipilih setelah user
    private void handleDeleteNotification() {
        NotificationModel selected = view.tableNotif.getSelectionModel().getSelectedItem();
        
        // Kalau user belum pilih row untuk lihat detailnya akan muncul alert
        if (selected == null) {
            setError("Select a notification to delete.");
            return;
        }

        // Konfirmasi hapus notifikasi
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Notification");
        confirm.setContentText("Are you sure you want to delete this?");
        Optional<ButtonType> result = confirm.showAndWait();

        // Jika user konfirmasi akan hapus notifikasi
        if (result.isPresent() && result.get() == ButtonType.OK) {
            notifController.deleteNotification(selected.getNotificationID());
            refreshNotificationTable();
            view.lblStatus.setText("Notification deleted.");
            view.lblStatus.setStyle("-fx-text-fill: green;");
        }
    }

    // Untuk customer logout
    private void handleLogout() {
        try {
            Stage stage = (Stage) view.getRoot().getScene().getWindow();
            LoginView loginView = new LoginView();
            
            new UserController(loginView);
            
            stage.setScene(new Scene(loginView.getRoot(), 400, 400));
            stage.setTitle("GoVlash Laundry - Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Tampilan untuk error
    private void setError(String msg) {
        view.lblStatus.setText(msg);
        view.lblStatus.setStyle("-fx-text-fill: red;");
    }
}