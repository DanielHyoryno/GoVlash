package view;

import database.Connect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.UserModel;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class EmployeeView {

    // Table & Data
    private TableView<UserModel> tableView = new TableView<>();
    private ObservableList<UserModel> employees = FXCollections.observableArrayList();

    // Form components
    private TextField nameField = new TextField();
    private TextField emailField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private PasswordField confirmField = new PasswordField();
    private ComboBox<String> genderBox = new ComboBox<>();
    private DatePicker dobPicker = new DatePicker();
    private ComboBox<String> roleBox = new ComboBox<>();

    private BorderPane root = new BorderPane();

    public EmployeeView() {
        buildUI();
        loadData();
    }

    public Parent getRoot() {
        return root;
    }

    private void buildUI() {
        Label title = new Label("Employee Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Table Columns
        TableColumn<UserModel, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<UserModel, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<UserModel, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("userEmail"));

        TableColumn<UserModel, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("userGender"));

        TableColumn<UserModel, Date> dobCol = new TableColumn<>("DOB");
        dobCol.setCellValueFactory(new PropertyValueFactory<>("userDOB"));

        TableColumn<UserModel, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("userRole"));

        tableView.getColumns().addAll(idCol, nameCol, emailCol, genderCol, dobCol, roleCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // === Form Input (GridPane) ===
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));

        genderBox.getItems().addAll("Male", "Female");
        roleBox.getItems().addAll("Admin", "Laundry Staff", "Receptionist");

        formPane.addRow(0, new Label("Name:"), nameField);
        formPane.addRow(1, new Label("Email:"), emailField);
        formPane.addRow(2, new Label("Password:"), passwordField);
        formPane.addRow(3, new Label("Confirm Password:"), confirmField);
        formPane.addRow(4, new Label("Gender:"), genderBox);
        formPane.addRow(5, new Label("Date of Birth:"), dobPicker);
        formPane.addRow(6, new Label("Role:"), roleBox);

        Button addButton = new Button("Add Employee");
        Button refreshButton = new Button("Refresh");

        HBox buttonBox = new HBox(10, addButton, refreshButton);
        formPane.add(buttonBox, 1, 7);

        // Layout
        root.setPadding(new Insets(10));
        root.setTop(title);
        BorderPane.setMargin(title, new Insets(10, 0, 10, 0));

        root.setCenter(tableView);
        root.setBottom(formPane);

        addButton.setOnAction(e -> insertEmployee());
        refreshButton.setOnAction(e -> loadData());
    }

    // Load Data dari Database ke TableView
    private void loadData() {
        employees.clear();

        try (Connection conn = Connect.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT * FROM users " +
                     "WHERE UserRole IN ('Admin', 'Laundry Staff', 'Receptionist') " +
                     "ORDER BY UserID DESC")) {

            while (rs.next()) {
                Date dbDob = rs.getDate("UserDOB");

                employees.add(new UserModel(
                        rs.getInt("UserID"),
                        rs.getString("UserName"),
                        rs.getString("UserEmail"),
                        rs.getString("UserPassword"),
                        rs.getString("UserGender"),
                        dbDob,
                        rs.getString("UserRole")
                ));
            }

            tableView.setItems(employees);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load employees.");
        }
    }

    // Insert Employee Baru ke Database
    private void insertEmployee() {
        String name = nameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        String confirm = confirmField.getText();
        String gender = genderBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String role = roleBox.getValue();

        // Validasi input employee
        if (name == null || name.isBlank() ||
            email == null || email.isBlank() ||
            pass == null || pass.isBlank() ||
            confirm == null || confirm.isBlank() ||
            gender == null ||
            dob == null ||
            role == null) {

            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill all fields!");
            return;
        }

        if (!email.endsWith("@govlash.com")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Employee email must end with @govlash.com");
            return;
        }

        if (pass.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Password must be at least 6 characters.");
            return;
        }

        if (!pass.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Confirm password must match.");
            return;
        }

        if (!gender.equals("Male") && !gender.equals("Female")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Gender must be Male or Female.");
            return;
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 17) {
            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Employee must be at least 17 years old.");
            return;
        }

        if (!role.equals("Admin") &&
            !role.equals("Laundry Staff") &&
            !role.equals("Receptionist")) {

            showAlert(Alert.AlertType.WARNING, "Validation Error",
                    "Role must be Admin, Laundry Staff, or Receptionist.");
            return;
        }

        try (Connection conn = Connect.getInstance().getConnection()) {

            // cek username
            String checkName = "SELECT COUNT(*) FROM users WHERE UserName = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkName)) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                            "Username already exists.");
                    return;
                }
            }

            // cek email
            String checkEmail = "SELECT COUNT(*) FROM users WHERE UserEmail = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkEmail)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert(Alert.AlertType.WARNING, "Validation Error",
                            "Email already exists.");
                    return;
                }
            }

            // Insert employee
            String insert = "INSERT INTO users " +
                    "(UserName, UserEmail, UserPassword, UserGender, UserDOB, UserRole) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insert)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, pass);
                ps.setString(4, gender);
                ps.setDate(5, Date.valueOf(dob));
                ps.setString(6, role);
                ps.executeUpdate();
            }

            showAlert(Alert.AlertType.INFORMATION, "Success", "New employee added!");
            clearForm();
            loadData();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add employee.");
        }
    }

    // =======================================================
    // 🔹 Utility Methods
    // =======================================================
    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmField.clear();
        genderBox.getSelectionModel().clearSelection();
        dobPicker.setValue(null);
        roleBox.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
