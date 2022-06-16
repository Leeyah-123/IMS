package com.inventory.ims;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import others.DBConnection;
import others.Toast;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    Button btnUsers, btnLogout, btnProfile, btnCustomers, btnSales, btnProducts;
    @FXML
    Label totCustomer, totSales, totEmp, totProduct;
    Connection con = DBConnection.getConnection();
    public void profilePage(ActionEvent event) throws IOException{
        loadPage("profile.fxml");
    }

    public void customersPage(ActionEvent event) throws IOException{
        loadPage("customers.fxml");
    }

    public void salesPage(ActionEvent event) throws IOException {
        loadPage("sales.fxml");
    }

    public void productsPage(ActionEvent event) throws IOException{
        loadPage("products.fxml");
    }

    public void usersPage(ActionEvent event) throws IOException, SQLException {
        if (authenticate()) {
            Toast.makeText((Stage)btnUsers.getScene().getWindow(), "Sorry, you do not have access to this resource", 500, 500, 500, "rgba(246,10,10,0.2)");
            return;
        }
       loadPage("employees.fxml");
    }

    public void loadPage(String page) throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage)btnUsers.getScene().getWindow();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(page)));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void logout(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to logout?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            PreparedStatement statement = con.prepareStatement("DELETE FROM logged_user;");
                statement.execute();
                Stage stage = (Stage)btnLogout.getScene().getWindow();
                stage.close();
        }
    }

    public void updateLabel(String table, Label label) throws SQLException {
        String SQL = "SELECT * FROM " + table + ";";
        int tot = 0;
        PreparedStatement preparedStatement = con.prepareStatement(SQL);
        ResultSet rs = preparedStatement.executeQuery();
        ArrayList<String> list = new ArrayList<String>();

        if (!rs.next()) return;
        tot += 1;
        while (rs.next()) {
            tot++;
        }
        label.setText(String.valueOf(tot));
    }

    public boolean authenticate() throws SQLException{
        String SQL = "SELECT role FROM users WHERE username = (SELECT username FROM logged_user);";
        PreparedStatement stmt = con.prepareStatement(SQL);
        ResultSet access = stmt.executeQuery();
        String accessLevel = null;
        while (access.next()) {
            accessLevel = access.getString("role");
        }
        if (accessLevel.equals("admin")) {return false;}
        else {
            return true;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            updateLabel("users", totEmp);
            updateLabel("products", totProduct);
            updateLabel("customers", totCustomer);
            updateLabel("sales", totSales);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
