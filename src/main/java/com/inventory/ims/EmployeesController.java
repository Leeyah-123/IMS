package com.inventory.ims;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import others.Employee;
import others.Toast;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeesController implements Initializable {
    @FXML
    private TableView<Employee> tableView;
    @FXML
    TableColumn<Employee, String> uName;
    @FXML
    TableColumn<Employee, String> fName;
    @FXML
    TableColumn<Employee, String> lName;
    @FXML
    TableColumn<Employee, String> uRole;
    @FXML
    TextField txtUsername, txtFirstname, txtLastname, txtSearch;
    @FXML
    ChoiceBox<String> choiceBox, txtSearchBy;
    @FXML
    Button btnBack;
    Connection con = others.DBConnection.getConnection();
    ObservableList<Employee> list = FXCollections.observableArrayList();

    public static void confirmExit(Button button) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to exit this dialog?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Stage stage;
            Parent root;

            stage = (Stage) button.getScene().getWindow();
            root = FXMLLoader.load(Objects.requireNonNull(EmployeesController.class.getResource("dashboard.fxml")));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void back(ActionEvent event) throws IOException {
        confirmExit(btnBack);
    }

    public void fetchUsers() {
        try {
            String SQL = "SELECT username, first_name, last_name, role FROM \"users\";";
            ResultSet rs = con.createStatement().executeQuery(SQL);
            fetchData(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editEmployee(ActionEvent event) throws SQLException {
        String username = txtUsername.getText();
        String role = choiceBox.getValue();

        if (username.equals("")) {
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Please select employee from treeview!", 500, 500, 500, "rgba(246,10,10,0.2)");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Update Employee");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to update this employee?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String SQL = "UPDATE users SET role = ? WHERE username = ?";
            try {
                PreparedStatement preparedStatement = con.prepareStatement(SQL);
                preparedStatement.setString(1, role);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();

                Toast.makeText((Stage)btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteEmployee(ActionEvent event) throws SQLException{
        String username = txtUsername.getText();
        if (username.equals("")) {
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Please select employee from treeview!", 500, 500, 500, "rgba(246,10,10,0.2)");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Employee");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to delete this employee?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String SQL = "DELETE FROM users WHERE username = ?";
            try {
                PreparedStatement preparedStatement = con.prepareStatement(SQL);
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();

                Toast.makeText((Stage)btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void clear() {
        txtUsername.setText("");
        txtFirstname.setText("");
        txtLastname.setText("");
        choiceBox.setValue("");
    }

    public void refreshTable(ActionEvent event) {
        list.clear();
        String SQL = "SELECT username, first_name, last_name, role FROM users;";
        ResultSet rs;
        try {
            rs = con.createStatement().executeQuery(SQL);
            fetchData(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void fetchData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Employee employee = new Employee();
            employee.setUsername(rs.getString("username"));
            employee.setFirstname(rs.getString("first_name"));
            employee.setLastname(rs.getString("last_name"));
            employee.setRole(rs.getString("role"));

            list.add(employee);
        }
        tableView.setItems(list);
    }

    public void search() throws SQLException {
        String searchBy = txtSearchBy.getValue();
        String search = txtSearch.getText();
        if (searchBy.equals("name")) {
            PreparedStatement stmt = con.prepareStatement("SELECT username, first_name, last_name, role FROM users WHERE first_name ~* ? OR last_name ~* ? OR username ~* ?;");
            stmt.setString(1, search);
            stmt.setString(2, search);
            stmt.setString(3, search);
            ResultSet rs = stmt.executeQuery();
            list.clear();
            fetchData(rs);
        } else {
            PreparedStatement stmt = con.prepareStatement("SELECT username, first_name, last_name, role FROM users WHERE role ~* ?;");
            stmt.setString(1, search);
            ResultSet rs = stmt.executeQuery();
            list.clear();
            fetchData(rs);
        }
    }

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> role = FXCollections.observableArrayList();
        role.addAll("employee", "admin");
        choiceBox.setItems(role);

        ObservableList<String> searchBy = FXCollections.observableArrayList();
        searchBy.addAll("name", "role");
        txtSearchBy.setItems(searchBy);
        txtSearchBy.setValue("name");

        assert tableView != null : "No registered employee!";
        uName.setCellValueFactory(new PropertyValueFactory<>("username"));
        fName.setCellValueFactory(new PropertyValueFactory<>("firstname"));
        lName.setCellValueFactory(new PropertyValueFactory<>("lastname"));
        uRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        fetchUsers();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtUsername.setText(newVal.getUsername());
                txtFirstname.setText(newVal.getFirstname());
                txtLastname.setText(newVal.getLastname());
                choiceBox.setValue(newVal.getRole());
            }
        });
    }
}
