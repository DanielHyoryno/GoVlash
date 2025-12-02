package view;

import java.util.ArrayList;

import controller.ServiceController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.ServiceModel;

public class ServiceView {

    private ServiceController controller;

    private TableView<ServiceModel> tableView = new TableView<>();
    private ObservableList<ServiceModel> services = FXCollections.observableArrayList();

    private TextField nameField = new TextField();
    private TextField descField = new TextField();
    private TextField priceField = new TextField();
    private TextField durationField = new TextField();

    private BorderPane root = new BorderPane();

    public ServiceView() {
        controller = new ServiceController();
        buildUI();
        loadData();
    }

    public Parent getRoot() {
        return root;
    }

    private void buildUI() {

        // === Title ===
        Label title = new Label("Service Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // === Table Columns ===
        TableColumn<ServiceModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("serviceID"));

        TableColumn<ServiceModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));

        TableColumn<ServiceModel, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("serviceDescription"));

        TableColumn<ServiceModel, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("servicePrice"));

        TableColumn<ServiceModel, Integer> durationCol = new TableColumn<>("Duration (days)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("serviceDuration"));

        tableView.getColumns().addAll(idCol, nameCol, descCol, priceCol, durationCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // === Form Input (GridPane) ===
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));

        formPane.addRow(0, new Label("Name:"), nameField);
        formPane.addRow(1, new Label("Description:"), descField);
        formPane.addRow(2, new Label("Price:"), priceField);
        formPane.addRow(3, new Label("Duration (days):"), durationField);

        Button addButton = new Button("Add Service");
        Button refreshButton = new Button("Refresh");

        HBox buttonBox = new HBox(10, addButton, refreshButton);
        formPane.add(buttonBox, 1, 4);

        // === Layout (BorderPane) ===
        root.setPadding(new Insets(10));
        root.setTop(title);
        BorderPane.setMargin(title, new Insets(10, 0, 10, 0));

        root.setCenter(tableView);
        root.setBottom(formPane);

        // === Event Handlers ===
        addButton.setOnAction(e -> insertService());
        refreshButton.setOnAction(e -> loadData());
    }

    // =======================================================
    // 🔹 Load Data dari Database ke TableView
    // =======================================================
    private void loadData() {
        services.clear();

        ArrayList<ServiceModel> list = controller.getAllServices();
        services.addAll(list);

        tableView.setItems(services);
    }

    // =======================================================
    // 🔹 Insert Service Baru ke Database
    // =======================================================
    private void insertService() {
        String name = nameField.getText();
        String desc = descField.getText();
        String priceText = priceField.getText();
        String durationText = durationField.getText();

        String error = controller.addService(name, desc, priceText, durationText);

        if (error != null) {
            showAlert(Alert.AlertType.WARNING, "Input Error", error);
            return;
        }

        showAlert(Alert.AlertType.INFORMATION, "Success", "New service added!");
        clearForm();
        loadData();
    }

    // =======================================================
    // 🔹 Utility Methods
    // =======================================================
    private void clearForm() {
        nameField.clear();
        descField.clear();
        priceField.clear();
        durationField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Optional: method helper kalau mau test cepat standalone
    public void showInNewStage() {
        Stage stage = new Stage();
        Scene scene = new Scene(getRoot(), 800, 500);
        stage.setTitle("GoVlash Laundry - Service Management");
        stage.setScene(scene);
        stage.show();
    }
}
