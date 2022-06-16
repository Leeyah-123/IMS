package com.inventory.ims;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import others.Toast;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class SignupController {
    @FXML
    Button signupBtn, loginBtn;
    @FXML
    TextField txtFirstname, txtLastname, txtUsername;
    @FXML
    PasswordField txtPassword, txtCPassword;
    boolean flag = false;
    Connection con = others.DBConnection.getConnection();

    public void signup(ActionEvent event) throws IOException {
        Validate();
    }

    public void loginPage(ActionEvent event) throws IOException {
        loadLogin();
    }

    private void loadLogin() throws IOException {
        Stage stage;
        Parent root;

        stage = (Stage)signupBtn.getScene().getWindow();
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                Main.leave(stage, con);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void Validate() throws IOException {
        try{
            if (txtUsername.getText().equals("") || txtFirstname.getText().equals("") || txtLastname.getText().equals("") || txtPassword.getText().equals("")){
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Please fill out all fields!!", 500, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            } else if (!txtFirstname.getText().matches( "[A-Z][a-z]+" )) {
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Invalid Firstname!", 500, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            } else if (!txtLastname.getText().matches( "[A-Z][a-z]+" )) {
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Invalid Lastname!", 500, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            } else if (!txtUsername.getText().matches( "[A-Z][a-z]+" )) {
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Invalid Username!", 500, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            } else if (!txtPassword.getText().matches(  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,15}$" )) {
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Password must contain at least one UpperCase letter, LowerCase letter, special character and number, 8-15 characters", 1000, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            } else if (!txtPassword.getText().equals(txtCPassword.getText())) {
                Toast.makeText((Stage)loginBtn.getScene().getWindow(), "Password does not match!", 1000, 500, 500, "rgba(246,10,10,0.2)");
                flag = false;
            }
            else {
                saveUser();
                Toast.makeText((Stage)signupBtn.getScene().getWindow(), "SignUp Successful!", 500, 500, 500, "green");
                flag = true;
            }
        } catch(SQLException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            Toast.makeText((Stage)loginBtn.getScene().getWindow(), "This username is already taken, please try another!", 500, 500, 500, "rgba(246,10,10,0.2)");
        }

        if (flag) {
            loadLogin();
        }
    }
    private void saveUser() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        String firstname = txtFirstname.getText();
        String lastname = txtLastname.getText();
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String pHash = createHash(password);

        String sql1 = "INSERT INTO users(first_name, last_name, username, password) VALUES (?,?,?,?);";
        PreparedStatement preparedStatement = con.prepareStatement(sql1);
        preparedStatement.setString(1, firstname);
        preparedStatement.setString(2, lastname);
        preparedStatement.setString(3, username);
        preparedStatement.setString(4, pHash);
        preparedStatement.executeUpdate();
    }

    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int SALT_BYTES = 24;
    public static final int HASH_BYTES = 24;
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
}
