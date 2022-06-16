package com.inventory.ims;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import others.Sales;
import others.Toast;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SalesController implements Initializable {
    @FXML
    private TableView<Sales> tableView;
    @FXML
    TableColumn<Sales, Integer> sales_id;
    @FXML
    TableColumn<Sales, Integer> customer_id;
    @FXML
    TableColumn<Sales, Integer> product_id;
    @FXML
    TableColumn<Sales, String> amount;
    @FXML
    TableColumn<Sales, Integer> qty;
    @FXML
    TableColumn<Sales, String> sales_date;
    @FXML
    TextField txtQty, txtAmount, txtSearch;
    @FXML
    DatePicker txtDate;
    @FXML
    ChoiceBox<String> txtCName, txtPName, txtSearchBy;
    @FXML
    Button btnBack;
    Connection con = others.DBConnection.getConnection();
    int selectedID;
    ObservableList<Sales> list = FXCollections.observableArrayList();

    public void back(ActionEvent event) throws IOException {
        EmployeesController.confirmExit(btnBack);
    }

    public void fetchSales() {
        try {
            String SQL = "SELECT * FROM sales;";
            ResultSet rs = con.createStatement().executeQuery(SQL);
            fetchData(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveSales() throws SQLException {
        try {
            boolean flag;

            String CName = "";
            if (txtCName.getValue() != null) {
                CName = txtCName.getValue();
            }

            String PName = "";
            if (txtPName.getValue() != null) {
                PName = txtPName.getValue();
            }

            int qty = 0;
            if (!txtQty.getText().equals("")) {
                qty = Integer.parseInt(txtQty.getText());
            }

            LocalDate sales_date = null;
            if (txtDate.getValue() != null) {
                sales_date = txtDate.getValue();
            }

            String amount = txtAmount.getText();

            if (txtCName.getValue() == null || txtPName.getValue() == null || txtQty.getText().equals("") || txtDate.getValue() == null || amount.equals("")) {
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            } else {
                flag = true;
            }

            if (flag) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Save Sale");
                alert.setHeaderText("Confirmation");
                alert.setContentText("Are you sure you want to save this sale record");

                if (alert.showAndWait().get() == ButtonType.OK) {
                    String SQL = "INSERT INTO sales (customer_id, product_id, quantity, sales_date, amount_paid) VALUES (?, ?, ?, ?, ?);";
                    PreparedStatement stmt = con.prepareStatement(SQL);
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT customer_id FROM customers WHERE first_name = ?");
                        statement.setString(1, CName);
                        ResultSet CNAME = statement.executeQuery();
                        while (CNAME.next()) {
                            stmt.setInt(1, CNAME.getInt("customer_id"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT product_id FROM products WHERE name = ?");
                        statement.setString(1, PName);
                        ResultSet PNAME = statement.executeQuery();
                        while (PNAME.next()) {
                            stmt.setInt(2, PNAME.getInt("product_id"));
                        }
                    } catch (SQLException e) {
                        Toast.makeText((Stage) btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "rgba(246,10,10,0.2)");
                        e.printStackTrace();
                    }
                    stmt.setInt(3, qty);
                    stmt.setDate(4, Date.valueOf(sales_date));
                    stmt.setInt(5, Integer.parseInt(amount));
                    stmt.executeUpdate();
                    Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                    clear();
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "red");
        }
    }

    public void editSales(ActionEvent event) throws SQLException {
        try {
            String CName = "";
            if (txtCName.getValue() != null) {
                CName = txtCName.getValue();
            }

            String PName = "";
            if (txtPName.getValue() != null) {
                PName = txtPName.getValue();
            }

            int qty = 0;
            if (!txtQty.getText().equals("")) {
                qty = Integer.parseInt(txtQty.getText());
            }

            LocalDate sales_date = null;
            if (txtDate.getValue() != null) {
                sales_date = txtDate.getValue();
            }

            String amount = txtAmount.getText();

            if (txtCName.getValue() == null || txtPName.getValue() == null || txtQty.getText().equals("") || txtDate.getValue() == null || amount.equals("")) {
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Please select record from treeview!!", 500, 500, 500, "rgba(246,10,10,0.2)");
                return;
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Edit Sale");
                alert.setHeaderText("Confirmation");
                alert.setContentText("Are you sure you want to edit this sale record?");

                if(alert.showAndWait().get() == ButtonType.OK) {

                    String query = "UPDATE sales SET customer_id=?, product_id=?, quantity=?, sales_date=?, amount_paid=? WHERE sales_id = ?;";

                    PreparedStatement statement = con.prepareStatement(query);
                    try {
                        PreparedStatement stmt = con.prepareStatement("SELECT customer_id FROM customers WHERE first_name = ?");
                        stmt.setString(1, CName);
                        ResultSet CNAME = stmt.executeQuery();
                        while (CNAME.next()) {
                            statement.setInt(1, CNAME.getInt("customer_id"));
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    try {
                        PreparedStatement stmt = con.prepareStatement("SELECT product_id FROM products WHERE name = ?");
                        stmt.setString(1, PName);
                        ResultSet PNAME = stmt.executeQuery();
                        while (PNAME.next()) {
                            statement.setInt(2, PNAME.getInt("product_id"));
                        }
                    } catch (SQLException e) {
                        Toast.makeText((Stage) btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "rgba(246,10,10,0.2)");
                    }
                    statement.setInt(3, qty);
                    statement.setDate(4, Date.valueOf(sales_date));
                    statement.setInt(5, Integer.parseInt(amount));
                    statement.setInt(6, this.selectedID);
                    statement.executeUpdate();
                    Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                    clear();

                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "red");
        }
    }

    public void deleteSales(ActionEvent event) throws SQLException {
        String CName = "";
        if (txtCName.getValue() != null) {
            CName = txtCName.getValue();
        }

        String PName = "";
        if (txtPName.getValue() != null) {
            PName = txtPName.getValue();
        }

        int qty = 0;
        if (!txtQty.getText().equals("")) {
            qty = Integer.parseInt(txtQty.getText());
        }

        LocalDate sales_date = null;
        if (txtDate.getValue() != null) {
            sales_date = txtDate.getValue();
        }

        String amount = txtAmount.getText();

        if (txtCName.getValue() == null || txtPName.getValue() == null || txtQty.getText().equals("") || txtDate.getValue() == null || amount.equals("")) {
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Please select record from treeview!!", 500, 500, 500, "rgba(246,10,10,0.2)");
            return;
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Sale");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to delete this sale?");

            if (alert.showAndWait().get() == ButtonType.OK) {

                String query = "DELETE FROM sales WHERE sales_id = ?;";

                PreparedStatement statement = con.prepareStatement(query);
                statement.setInt(1, this.selectedID);
                statement.executeUpdate();
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                clear();
            }
        }
    }

    public void clear() {
        txtAmount.setText("");
        txtCName.setValue("");
        txtDate.getEditor().clear();
        txtQty.setText("");
        txtPName.setValue("");
    }

    public void refreshTable(ActionEvent event) {
        list.clear();
        String SQL = "SELECT * FROM sales;";
        ResultSet rs;
        try {
            rs = con.createStatement().executeQuery(SQL);
            fetchData(rs);
        } catch (SQLException e) {
            System.out.println("Error message" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void fetchData(ResultSet rs) throws SQLException {
        while (rs.next()) {
            Sales sale = new Sales();
            sale.setSales_id(rs.getInt("sales_id"));
            sale.setCustomer_id(rs.getInt("customer_id"));
            sale.setProduct_id(rs.getInt("product_id"));
            sale.setAmount(rs.getString("amount_paid"));
            sale.setQty(rs.getInt("quantity"));
            sale.setDate(rs.getString("sales_date"));

            list.add(sale);
        }
        tableView.setItems(list);
    }

    public void search() {
        String searchBy = txtSearchBy.getValue();
        String search = txtSearch.getText();
        if (search.equals("")) {
            list.clear();
            String SQL = "SELECT * FROM sales;";
            ResultSet rs;
            try {
                rs = con.createStatement().executeQuery(SQL);
                fetchData(rs);
            } catch (SQLException e) {
                System.out.println("Error message" + e.getMessage());
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            if (searchBy.equals("sales_id")) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM sales WHERE sales_id = ?;");
                stmt.setInt(1, Integer.parseInt(search));
                ResultSet rs = stmt.executeQuery();
                list.clear();
                fetchData(rs);
            } else if (searchBy.equals("customer_id")) {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM sales WHERE customer_id = ?;");
                stmt.setInt(1, Integer.parseInt(search));
                ResultSet rs = stmt.executeQuery();
                list.clear();
                fetchData(rs);
            } else {
                PreparedStatement stmt = con.prepareStatement("SELECT * FROM sales WHERE product_id = ?;");
                stmt.setInt(1, Integer.parseInt(search));
                ResultSet rs = stmt.executeQuery();
                list.clear();
                fetchData(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "red");
        }
    }

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> customers = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT first_name FROM customers;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                customers.add(rs.getString("first_name"));
            }
            txtCName.setItems(customers);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ObservableList<String> products = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT name FROM products;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                products.add(rs.getString("name"));
            }
            txtPName.setItems(products);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ObservableList<String> searchBy = FXCollections.observableArrayList();
        searchBy.addAll("sales_id", "customer_id", "product_id");
        txtSearchBy.setItems(searchBy);
        txtSearchBy.setValue("sales_id");

        assert tableView != null : "No registered sale!";
        sales_id.setCellValueFactory(new PropertyValueFactory<>("sales_id"));
        customer_id.setCellValueFactory(new PropertyValueFactory<>("customer_id"));
        product_id.setCellValueFactory(new PropertyValueFactory<>("product_id"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        sales_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        fetchSales();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    PreparedStatement stmt = con.prepareStatement("SELECT first_name FROM customers WHERE customer_id = ?");
                    stmt.setInt(1, newVal.getCustomer_id());
                    ResultSet CID = stmt.executeQuery();
                    while (CID.next()) {
                        txtCName.setValue(CID.getString("first_name"));
                    }
                } catch ( SQLException e) {
                    e.printStackTrace();
                }

                try {
                    PreparedStatement statement = con.prepareStatement("SELECT name FROM products WHERE product_id = ?");
                    statement.setInt(1, newVal.getProduct_id());
                    ResultSet PID = statement.executeQuery();
                    while (PID.next()) {
                        txtPName.setValue(PID.getString("name"));
                    }
                } catch ( SQLException e) {
                    e.printStackTrace();
                }

                txtQty.setText(String.valueOf(newVal.getQty()));
                txtDate.setValue(LocalDate.parse(newVal.getDate()));
                txtAmount.setText(newVal.getAmount());
                this.selectedID = newVal.getSales_id();
            }
        });
    }
}
