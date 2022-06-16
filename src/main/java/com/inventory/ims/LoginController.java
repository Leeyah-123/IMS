package com.inventory.ims;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import others.Toast;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {
    @FXML
    public TextField txtUsername;
    @FXML
    public PasswordField txtPassword;
    @FXML
    Button signupBtn;
    @FXML
    Button loginBtn;
    @FXML
    CheckBox checkBox;
    Connection con = others.DBConnection.getConnection();
    public void login(ActionEvent event) throws SQLException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        ResultSet rs = fetchUsers();

        if (username.equals("") || password.equals("")) {
            Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Please fill out all fields", 500, 500, 500, "red");
        } else if (rs.next()) {
              String pHash = rs.getString("password");
              boolean matched = validatePassword(password, pHash);
                if (matched) {
                    String SQL = "INSERT INTO logged_user VALUES (?, ?);";
                    PreparedStatement preparedStatement = con.prepareStatement(SQL);
                    preparedStatement.setString(1, username);
                    if (checkBox.isSelected()) {
                        preparedStatement.setString(2, "true");
                    } else {
                        preparedStatement.setString(2, "false");
                    }
                    preparedStatement.execute();
                    Stage stage;
                    Parent root;

                    stage = (Stage) signupBtn.getScene().getWindow();
                    root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dashboard.fxml")));
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Invalid Password!", 500, 500, 500, "red");
                }
            } else {
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Invalid Username!", 500, 500, 500, "red");
        }
    }

    public void loadSignup(ActionEvent event) throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage)signupBtn.getScene().getWindow();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signup.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(action -> {
            action.consume();
            try {
                Main.leave(stage, con);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ResultSet fetchUsers() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String username = txtUsername.getText();

        String SQL = "SELECT password FROM users WHERE username = ?";
        PreparedStatement ps = con.prepareStatement(SQL);

        ps.setString(1, username);

        return ps.executeQuery();
    }

    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int ITERATION_INDEX = 0;
    public static final int SALT_INDEX = 1;
    public static final int PBKDF2_INDEX = 2;

    /**
     * Validates a password using a hash.
     *
     * @param   password    the password to check
     * @param   goodHash    the hash of the valid password
     * @return              true if the password is correct, false if not
     */
    public static boolean validatePassword(String password, String goodHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        return validatePassword(password.toCharArray(), goodHash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param   password    the password to check
     * @param   goodHash    the hash of the valid password
     * @return              true if the password is correct, false if not
     */
    public static boolean validatePassword(char[] password, String goodHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        // Decode the hash into its parameters
        String[] params = goodHash.split(":");
        int iterations = Integer.parseInt(params[ITERATION_INDEX]);
        byte[] salt = fromHex(params[SALT_INDEX]);
        byte[] hash = fromHex(params[PBKDF2_INDEX]);
        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method
     * is used so that password hashes cannot be extracted from an on-line
     * system using a timing attack and then attacked off-line.
     *
     * @param   a       the first byte array
     * @param   b       the second byte array
     * @return          true if both byte arrays are the same, false if not
     */
    private static boolean slowEquals(byte[] a, byte[] b)
    {
        int diff = a.length ^ b.length;
        for(int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /**
     *  Computes the PBKDF2 hash of a password.
     *
     * @param   password    the password to hash.
     * @param   salt        the salt
     * @param   iterations  the iteration count (slowness factor)
     * @param   bytes       the length of the hash to compute in bytes
     * @return              the PBDKF2 hash of the password
     */
    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        return skf.generateSecret(spec).getEncoded();
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param   hex         the hex string
     * @return              the hex string decoded into a byte array
     */
    private static byte[] fromHex(String hex)
    {
        byte[] binary = new byte[hex.length() / 2];
        for(int i = 0; i < binary.length; i++)
        {
            binary[i] = (byte)Integer.parseInt(hex.substring(2*i, 2*i+2), 16);
        }
        return binary;
    }
}
