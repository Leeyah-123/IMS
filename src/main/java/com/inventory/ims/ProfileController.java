package com.inventory.ims;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import others.Toast;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    @FXML
    Label lblCPassword;
    @FXML
    TextField txtUsername, txtFirstname, txtLastname;
    @FXML
    PasswordField txtPassword, txtCPassword;
    @FXML
    Button btnBack, btnChangeP, btnSaveP, btnEdit, btnSave;
    Connection con = others.DBConnection.getConnection();

    public void back(ActionEvent event) throws IOException {
        EmployeesController.confirmExit(btnBack);
    }

    public void editDetails(ActionEvent event) {
        txtUsername.setEditable(true);
        txtFirstname.setEditable(true);
        txtLastname.setEditable(true);
        btnEdit.setDisable(true);
        btnSave.setDisable(false);
    }

    public void saveDetails(ActionEvent event) {
        boolean flag = false;

        if (txtUsername.getText().equals("") || txtFirstname.getText().equals("") || txtLastname.getText().equals("")) {
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Edit");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to edit your details?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                saveUser();
                flag = true;
            }
        }
        if (flag) {
            txtUsername.setEditable(false);
            txtFirstname.setEditable(false);
            txtLastname.setEditable(false);
            btnEdit.setDisable(false);
            btnSave.setDisable(true);
            ResultSet rs;
            try {
                PreparedStatement stmt = con.prepareStatement("SELECT username, first_name, last_name FROM users WHERE username = (SELECT username FROM logged_user);");
                rs = stmt.executeQuery();
                fetchData(rs);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void fetchData(ResultSet rs) {
        try {
            while (rs.next()) {
                txtUsername.setText(rs.getString("username"));
                txtFirstname.setText(rs.getString("first_name"));
                txtLastname.setText(rs.getString("last_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveUser() {
        try {
            String firstname = txtFirstname.getText();
            String lastname = txtLastname.getText();
            String username = txtUsername.getText();

            if (firstname.equals("") || lastname.equals("") || username.equals("")){
                Toast.makeText((Stage)btnBack.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
                return;
            }

                String sql1 = "UPDATE users SET first_name = ?, last_name = ?, username = ? WHERE username = (SELECT username FROM logged_user);";

                PreparedStatement preparedStatement = con.prepareStatement(sql1);
                preparedStatement.setString(1, firstname);
                preparedStatement.setString(2, lastname);
                preparedStatement.setString(3, username);
                preparedStatement.executeUpdate();
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");

        } catch (SQLException e) {
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Username already taken, please try another!", 1000, 500, 500, "rgba(246,10,10,0.2)");
        }
    }

    public void changePassword() {
        new PromptDialogController();
        txtPassword.setEditable(true);
        txtCPassword.setEditable(true);
        btnChangeP.setDisable(true);
        btnSaveP.setDisable(false);
    }

    public void savePassword() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        boolean flag = false;

        String password = txtPassword.getText();
        String cPassword = txtCPassword.getText();

        if (password.equals("") || cPassword.equals("")) {
            Toast.makeText((Stage) btnBack.getScene().getWindow(), "Please fill out both fields!", 1000, 500, 500, "rgba(246,10,10,0.2)");
        } else if (!txtPassword.getText().matches(  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,15}$" )) {
            Toast.makeText((Stage)btnChangeP.getScene().getWindow(), "Password must contain at least one UpperCase letter, LowerCase letter, special character and number, 8-15 characters", 1000, 500, 500, "rgba(246,10,10,0.2)");
        } else {
            flag = true;
        }

        if (flag) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Change Password");
            alert.setHeaderText("Confirmation");
            alert.setContentText("Are you sure you want to change your password?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                String query = "UPDATE users SET password = ? WHERE username = (SELECT username FROM logged_user);";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, createHash(password));
                stmt.executeUpdate();
                Toast.makeText((Stage) btnBack.getScene().getWindow(), "Operation Successful!", 500, 500, 500, "green");

                btnSaveP.setDisable(true);
                btnChangeP.setDisable(false);
                txtPassword.setText("");
                txtCPassword.setText("");
            }
        }
    }

    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int SALT_BYTES = 24;
    public static final int PBKDF2_ITERATIONS = 1000;

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param   password    the password to hash
     * @return              a salted PBKDF2 hash of the password
     */
    public static String createHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return createHash(password.toCharArray());
    }

    /**
     * Returns a salted PBKDF2 hash of the password.
     *
     * @param   password    the password to hash
     * @return              a salted PBKDF2 hash of the password
     */
    public static String createHash(char[] password)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt);
        // format iterations:salt:hash
        return PBKDF2_ITERATIONS + ":" + toHex(salt) + ":" +  toHex(hash);
    }

    /**
     * Computes the PBKDF2 hash of a password.
     *
     * @param password the password to hash.
     * @param salt     the salt
     * @return the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, SignupController.PBKDF2_ITERATIONS, SignupController.HASH_BYTES * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT username, first_name, last_name FROM users WHERE username = (SELECT username FROM logged_user);");
            ResultSet rs = stmt.executeQuery();
            fetchData(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
