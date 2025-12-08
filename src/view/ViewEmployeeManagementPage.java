package view;

import java.util.ArrayList;

import controller.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.UserModel;

public class ViewEmployeeManagementPage {

    private UserModel currentUser;
    private BorderPane root;
    private UserController userController;
    private TableView<UserModel> table;

    public ViewEmployeeManagementPage(UserModel user) {
        this.currentUser = user;
        this.userController = new UserController();
        
        openEmployeeManagementPage();
        getAllEmployees();
    }

    public Parent getRoot() { 
    	return root; 
    }
    
    // Tampilan untuk admin manage employee
    private void openEmployeeManagementPage() {
        root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Employee Management");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No employees found"));
        
        TableColumn<UserModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        TableColumn<UserModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        TableColumn<UserModel, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("userRole"));

        table.getColumns().addAll(idCol, nameCol, roleCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnAdd = new Button("Add New Employee");
        Button btnBack = new Button("Back to Menu");

        // Buka halaman form baru
        btnAdd.setOnAction(e -> navigateTo(new ViewAddEmployeePage(currentUser).getRoot()));
        btnBack.setOnAction(e -> navigateTo(new ViewAdminMainPage(currentUser).getRoot()));

        VBox content = new VBox(15, title, table, btnAdd, btnBack);
        content.setAlignment(Pos.CENTER);
        root.setCenter(content);
    }

    // Ambil data semua employee yang terdaftar
    private ArrayList<UserModel> getAllEmployees() {
        // Ambil data dari Controller
        ArrayList<UserModel> employees = userController.getAllEmployees();
        
        // Update Tabel 
        table.getItems().clear();
        table.getItems().addAll(employees);

        return employees;
    }

    // Navigasi ke parent page nya
    private void navigateTo(Parent page) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(page, 700, 600));
        stage.centerOnScreen();
    }
}