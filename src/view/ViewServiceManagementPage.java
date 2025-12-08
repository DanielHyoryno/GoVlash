package view;

import java.util.ArrayList;

import controller.ServiceController;
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
import model.ServiceModel;
import model.UserModel;

public class ViewServiceManagementPage {

    private UserModel currentUser;
    private BorderPane root;
    private ServiceController serviceController;
    private TableView<ServiceModel> table;

    public ViewServiceManagementPage(UserModel user) {
        this.currentUser = user;
        this.serviceController = new ServiceController();
        
        openServiceManagementPage();
        getAllServices();
    }

    public Parent getRoot() { 
    	return root; 
    }

    // Tampilan untuk lihat service
    private void openServiceManagementPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Service Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Table Setup
        table = new TableView<>();
        table.setPlaceholder(new Label("No services found"));
        
        TableColumn<ServiceModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("serviceID"));
        
        TableColumn<ServiceModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        
        TableColumn<ServiceModel, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));
        
        TableColumn<ServiceModel, Integer> durCol = new TableColumn<>("Duration");
        durCol.setCellValueFactory(new PropertyValueFactory<>("serviceDuration"));

        table.getColumns().addAll(idCol, nameCol, priceCol, durCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnAdd = new Button("Add New Service");
        Button btnEdit = new Button("Edit Selected");
        Button btnDelete = new Button("Delete Selected");
        Button btnBack = new Button("Back to Menu");

        // Buka Halaman Baru
        btnAdd.setOnAction(e -> navigateTo(new ViewAddServicePage(currentUser).getRoot()));
        
        // Buka Halaman Baru dengan data terpilih
        btnEdit.setOnAction(e -> openEditServicePage());
        btnDelete.setOnAction(e -> deleteService());
        btnBack.setOnAction(e -> navigateTo(new ViewAdminMainPage(currentUser).getRoot()));

        HBox actions = new HBox(10, btnAdd, btnEdit, btnDelete);
        actions.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, title, table, actions, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }

    // Untuk dapat semua service yang ada
    private ArrayList<ServiceModel> getAllServices() {
        table.getItems().clear();
        
        ArrayList<ServiceModel> list = serviceController.getAllServices();
        
        if (list != null) {
            table.getItems().addAll(list);
        }

        return list;
    }

    // Untuk edit service
    private void openEditServicePage() {
        ServiceModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a service to edit.");
            return;
        }
        navigateTo(new ViewEditServicePage(currentUser, selected).getRoot());
    }

    // Untuk delete service yang belum ada transaksi
    private void deleteService() {
        ServiceModel selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Select a service to delete.");
            return;
        }
        serviceController.deleteService(selected.getServiceID());
        getAllServices(); // Refresh list
    }

    // Navigasi ke parent page
    private void navigateTo(Parent page) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(page, 700, 600));
        stage.centerOnScreen();
    }

    // Tampilkan message
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.show();
    }
}