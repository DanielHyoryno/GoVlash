package view;

import controller.TransactionController;
import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.TransactionModel;
import model.UserModel;
import java.util.ArrayList;

public class ViewAssignOrderPage extends Stage {

    private UserModel currentUser;
    private TransactionModel transaction;
    private ViewPendingTransactionPage parentPage; 
    
    private TransactionController transController;
    private UserController userController;
    
    private ComboBox<String> cbStaff;
    private ArrayList<UserModel> staffObjects;

    public ViewAssignOrderPage(UserModel user, TransactionModel trans, ViewPendingTransactionPage parent) {
        this.currentUser = user;
        this.transaction = trans;
        this.parentPage = parent;
        
        this.transController = new TransactionController();
        this.userController = new UserController();
        
        openAssignOrderPage();
        getUsersByRole("Laundry Staff"); 
    }

    // Tampilan untuk assign order ke staff
    private void openAssignOrderPage() {
        this.setTitle("Assign Order #" + transaction.getTransactionID());
        this.initModality(Modality.APPLICATION_MODAL); 

        Label lbl = new Label("Choose Laundry Staff:");
        cbStaff = new ComboBox<>();
        cbStaff.setPromptText("Select Staff");
        
        Button btnConfirm = new Button("Assign");
        btnConfirm.setOnAction(e -> assignOrderToLaundryStaff());

        VBox layout = new VBox(15, lbl, cbStaff, btnConfirm);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 300, 200);
        this.setScene(scene);
        this.show();
    }

    // Mengisi data employee ke dalam combo box
    private void getUsersByRole(String role) {
        ArrayList<UserModel> allEmployees = userController.getUsersByRole("Employee");
        
        staffObjects = new ArrayList<>();
        cbStaff.getItems().clear();
        
        // Filter untuk Laundry Staff aja
        for (UserModel u : allEmployees) {
            if (role.equals(u.getUserRole())) {
                staffObjects.add(u);
                cbStaff.getItems().add(u.getUserName() + " (ID: " + u.getUserID() + ")");
            }
        }
    }

    // Untuk receptionist assign order ke laundry staff
    private void assignOrderToLaundryStaff() {
        int idx = cbStaff.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            showAlert("Please choose a staff!");
            return;
        }

        int staffID = staffObjects.get(idx).getUserID();

        String error = transController.assignOrderToLaundryStaff(transaction, staffID, currentUser.getUserID());
        
        if (error == null) {
            showAlert("Order assigned successfully!");
            parentPage.refreshTable();
            this.close();
        } else {
            showAlert("Error: " + error);
        }
    }

    // Untuk kasih message
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}