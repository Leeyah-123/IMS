package com.inventory.ims;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import others.Customer;
import others.Toast;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomersController implements Initializable {
    @FXML
    private TableView<Customer> tableView;
    @FXML
    TableColumn<Customer, Integer> cID;
    @FXML
    TableColumn<Customer, String> fName;
    @FXML
    TableColumn<Customer, String> lName;
    @FXML
    TableColumn<Customer, String> phoneNum;
    @FXML
    TextField txtPhonenum, txtFirstname, txtLastname, txtSearch;
    @FXML
    Button btnBack, btnSave;
    @FXML
    ChoiceBox<String> txtSearchBy;
    Connection con = others.DBConnection.getConnection();
    int selectedID;
    ObservableList<Customer> list = FXCollections.observableArrayList();

    public void back(ActionEvent event) throws IOException {
        EmployeesController.confirmExit(btnBack);
    }

    public void fetchCustomers() {
        try {
            String SQL = "SELECT * FROM customers;";
            ResultSet rs = con.createStatement().executeQuery(SQL);
            fetchData(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomer() throws SQLException {
        boolean flag = false;
        String firstname = txtFirstname.getText();
        String lastname = txtLastname.getText();
        String phone_num = txtPhonenum.getText();

        if (firstname.equals("") || lastname.equals("") || phone_num.equals("")){
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!firstname.matches( "[A-Z][a-z]+" )) {
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Invalid Firstname!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!lastname.matches( "[A-Z][a-z]+" )) {
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Invalid Lastname!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!phone_num.matches( "0[7-9][0-1][0-9]{8}" )) {
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Invalid Phone Number!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else {
            flag = true;
        }

        if (flag) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Customer");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to save this customer?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                String SQL = "INSERT INTO customers(first_name, last_name, phone_num) VALUES (?, ?, ?);";
                PreparedStatement stmt = con.prepareStatement(SQL);
                stmt.setString(1, firstname);
                stmt.setString(2, lastname);
                stmt.setString(3, phone_num);
                stmt.executeUpdate();
                Toast.makeText((Stage) btnSave.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                clear();
            }
        }
    }

    public void editCustomer(ActionEvent event) throws SQLException {
        boolean flag = false;
        String firstname = txtFirstname.getText();
        String lastname = txtLastname.getText();
        String phone_num = txtPhonenum.getText();

        if (firstname.equals("") || lastname.equals("") || phone_num.equals("")){
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!firstname.matches( "[A-Z][a-z]+" )) {
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Invalid Firstname!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!lastname.matches( "[A-Z][a-z]+" )) {
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Invalid Lastname!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!phone_num.matches( "0[7-9][0-1][0-9]{8}" )) {
            Toast.makeText((Stage)btnSave.getScene().getWindow(), "Invalid Phone Number!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else {
            flag = true;
        }

        if (flag) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Update Customer");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to update this customer?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                String SQL = "UPDATE customers SET first_name = ?, last_name = ?, phone_num = ? WHERE customer_id = ?;";
                PreparedStatement stmt = con.prepareStatement(SQL);
                stmt.setString(1, firstname);
                stmt.setString(2, lastname);
                stmt.setString(3, phone_num);
                stmt.setInt(4, this.selectedID);
                stmt.executeUpdate();
                Toast.makeText((Stage) btnSave.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                clear();
            }
        }
    }

    public void deleteCustomer(ActionEvent event) throws SQLException {
        String firstname = txtFirstname.getText();
        String lastname = txtLastname.getText();
        String phone_num = txtPhonenum.getText();

        if (firstname.equals("") || lastname.equals("") || phone_num.equals("")){
        Toast.makeText((Stage)btnSave.getScene().getWindow(), "Please select customer from treeview!", 500, 500, 500, "rgba(246,10,10,0.2)");
        return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Customer");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to delete this customer?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String SQL = "DELETE FROM customers WHERE customer_id = ?;";
            PreparedStatement stmt = con.prepareStatement(SQL);
            stmt.setInt(1, this.selectedID);
            stmt.executeUpdate();
            Toast.makeText((Stage) btnSave.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
            clear();
        }
    }

    public void clear() {
        txtPhonenum.setText("");
        txtFirstname.setText("");
        txtLastname.setText("");
    }

    public void refreshTable(ActionEvent event) {
        list.clear();
        String SQL = "SELECT * FROM customers;";
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
            Customer customer = new Customer();
            customer.setId(rs.getInt("customer_id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setPhone(rs.getString("phone_num"));

            list.add(customer);
        }
        tableView.setItems(list);
    }

    public void search() throws SQLException {
        String searchBy = txtSearchBy.getValue();
        String search = txtSearch.getText();
        if (searchBy.equals("name")) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM customers WHERE first_name ~* ? OR last_name ~* ?;");
            stmt.setString(1, search);
            stmt.setString(2, search);
            ResultSet rs = stmt.executeQuery();
            list.clear();
            fetchData(rs);
        } else {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM customers WHERE phone_num ~* ?;");
            stmt.setString(1, search);
            ResultSet rs = stmt.executeQuery();
            list.clear();
            fetchData(rs);
        }
    }

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> searchBy = FXCollections.observableArrayList();
        searchBy.addAll("name", "phone_num");
        txtSearchBy.setItems(searchBy);
        txtSearchBy.setValue("name");

        assert tableView != null : "No registered customer!";
        cID.setCellValueFactory(new PropertyValueFactory<>("id"));
        fName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        phoneNum.setCellValueFactory(new PropertyValueFactory<>("phone"));

        fetchCustomers();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtPhonenum.setText(newVal.getPhone());
                txtFirstname.setText(newVal.getFirstName());
                txtLastname.setText(newVal.getLastName());
                this.selectedID = newVal.getId();
            }
        });
    }
}
