package com.inventory.ims;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import others.DBConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {
    Connection con = DBConnection.getConnection();

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        if (checkDB()) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
            loadPage(stage, fxmlLoader);
        } else {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login.fxml"));
            loadPage(stage, fxmlLoader);
        }
    }

    private void loadPage(Stage stage, FXMLLoader fxmlLoader) throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Inventory Management System");
        Image image = new Image("icon1.jpeg");

        stage.getIcons().add(image);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                exit(stage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void exit(Stage stage) throws SQLException {
        leave(stage, con);
    }

    static void leave(Stage stage, Connection con) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Confirmation");
        alert.setContentText("Are you sure you want to exit the program?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            ResultSet rs;
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM logged_user");
            rs = stmt.executeQuery();
            while (rs.next()) {
                boolean save = Boolean.parseBoolean(rs.getString("save"));
                if (save) stage.close();
                else {
                    PreparedStatement statement = con.prepareStatement("DELETE FROM logged_user;");
                    statement.execute();
                    stage.close();
                }
            }
            stage.close();
        }
    }

    public boolean checkDB() throws SQLException {
        ResultSet rs;
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM logged_user");
        rs = stmt.executeQuery();
        return rs.next();
    }

    public static void main(String[] args) {
        launch();
    }
}