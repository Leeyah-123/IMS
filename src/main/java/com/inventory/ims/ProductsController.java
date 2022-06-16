package com.inventory.ims;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import others.Product;
import others.Toast;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProductsController implements Initializable {
    @FXML
    private TableView<Product> tableView;
    @FXML
    TableColumn<Product, Integer> pID;
    @FXML
    TableColumn<Product, String> name;
    @FXML
    TableColumn<Product, String> type;
    @FXML
    TableColumn<Product, String> price;
    @FXML
    TableColumn<Product, Integer> qty;
    @FXML
    TextField txtName, txtPrice, txtQty, txtSearch;
    @FXML
    ChoiceBox<String> choiceBox, txtSearchBy;
    @FXML
    Button btnBack;
    Connection con = others.DBConnection.getConnection();
    int selectedID;
    ObservableList<Product> list = FXCollections.observableArrayList();
    public void back(ActionEvent event) throws IOException {
        EmployeesController.confirmExit(btnBack);
    }

    public void fetchProducts() {
        try {
            String SQL = "SELECT * FROM \"products\";";
            ResultSet rs = con.createStatement().executeQuery(SQL);
            fetchData(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveProduct() throws SQLException {
        boolean flag;
        String name = txtName.getText();
        String type = choiceBox.getValue();
        String price = txtPrice.getText();
        int qty = 0;
        if (!txtQty.getText().equals("")) {
            qty = Integer.parseInt(txtQty.getText());
        }

        if (name.equals("") || type.equals("") || price.equals("") || txtQty.getText().equals("")){
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!name.matches( "[A-Z][a-z]+" )) {
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Invalid name!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else {
            flag = true;
        }

        if (flag) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save Product");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to save this product?");

            try {
                if (alert.showAndWait().get() == ButtonType.OK) {
                    String SQL = "INSERT INTO products(name, type, price, qty_in_stock) VALUES (?, ?, ?, ?);";
                    PreparedStatement stmt = con.prepareStatement(SQL);
                    stmt.setString(1, name);
                    stmt.setString(2, type);
                    stmt.setInt(3, Integer.parseInt(price));
                    stmt.setInt(4, qty);
                    stmt.executeUpdate();
                    Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                    clear();
                }
            } catch (NumberFormatException e) {
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "red");
            }
        }
    }

    public void editProduct(ActionEvent event) throws SQLException {
        boolean flag;
        String name = txtName.getText();
        String type = choiceBox.getValue();
        String price = txtPrice.getText();
        int qty = 0;
        if (!txtQty.getText().equals("")) {
             qty = Integer.parseInt(txtQty.getText());
        }

        if (name.equals("") || type.equals("") || price.equals("") || txtQty.getText().equals("")){
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Please select product from treeview!!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else if (!name.matches( "[A-Z][a-z]+" )) {
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Name!", 500, 500, 500, "rgba(246,10,10,0.2)");
            flag = false;
        } else {
            flag = true;
        }

        if (flag) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Update Product");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to update this product?");

            try {
                if (alert.showAndWait().get() == ButtonType.OK) {
                    String SQL = "UPDATE products SET name = ?, type = ?, price = ?, qty_in_stock = ? WHERE product_id = ?;";
                    PreparedStatement stmt = con.prepareStatement(SQL);
                    stmt.setString(1, name);
                    stmt.setString(2, type);
                    stmt.setInt(3, Integer.parseInt(price));
                    stmt.setInt(4, qty);
                    stmt.setInt(5, this.selectedID);
                    stmt.executeUpdate();
                    Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
                    clear();
                }
            } catch (NumberFormatException e) {
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Invalid Input!", 500, 500, 500, "red");
            }
        }
    }

    public void deleteProduct(ActionEvent event) throws SQLException {
        String name = txtName.getText();
        String type = choiceBox.getValue();
        String price = txtPrice.getText();

        if (name.equals("") || type.equals("") || price.equals("") || txtQty.getText().equals("")){
            Toast.makeText((Stage)btnBack.getScene().getWindow(), "Please select product from treeview!!", 500, 500, 500, "rgba(246,10,10,0.2)");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to delete this product?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            String SQL = "DELETE FROM products WHERE product_id = ?;";
            PreparedStatement stmt = con.prepareStatement(SQL);
            stmt.setInt(1, this.selectedID);
            stmt.executeUpdate();
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");
            clear();
        }
    }

    public void clear() {
        txtName.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        choiceBox.setValue("");
    }

    public void refreshTable(ActionEvent event) {
        list.clear();
        String SQL = "SELECT * FROM products;";
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
            Product product = new Product();
            product.setId(rs.getInt("product_id"));
            product.setName(rs.getString("name"));
            product.setType(rs.getString("type"));
            product.setPrice(rs.getString("price"));
            product.setQty(rs.getInt("qty_in_stock"));

            list.add(product);
        }
        tableView.setItems(list);
    }

    public void search() throws SQLException {
        String searchBy = txtSearchBy.getValue();
        String search = txtSearch.getText();
        if (searchBy.equals("name")) {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM products WHERE name ~* ?;");
            stmt.setString(1, search);
            ResultSet rs = stmt.executeQuery();
            list.clear();
            fetchData(rs);
        } else {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM products WHERE type ~* ?;");
            stmt.setString(1, search);
            ResultSet rs = stmt.executeQuery();
            list.clear();
            fetchData(rs);
        }
    }

    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> types = FXCollections.observableArrayList();
        types.addAll("electronics", "furniture", "cutlery", "agriculture", "food/drink");
        choiceBox.setItems(types);

        ObservableList<String> searchBy = FXCollections.observableArrayList();
        searchBy.addAll("name", "type");
        txtSearchBy.setItems(searchBy);
        txtSearchBy.setValue("name");

        assert tableView != null : "No registered product!";
        pID.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));

        fetchProducts();

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtName.setText(newVal.getName());
                choiceBox.setValue(newVal.getType());
                txtPrice.setText(newVal.getPrice());
                txtQty.setText(String.valueOf(newVal.getQty()));
                this.selectedID = newVal.getId();
            }
        });
    }
}
