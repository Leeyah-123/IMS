package com.inventory.ims;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
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

public class PromptDialogController {
    Connection con = others.DBConnection.getConnection();

    public PromptDialogController() {

        Stage stg = new Stage();
        stg.setAlwaysOnTop(true);

        stg.initModality(Modality.APPLICATION_MODAL);
        stg.setResizable(false);

        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("dialog.fxml")));
            Scene s = new Scene(root);

            PasswordField txtP = (PasswordField) root.lookup("#txtP");
            Button btnSubmit = (Button) root.lookup("#btnSubmit");

            PreparedStatement statement = con.prepareStatement("SELECT password FROM users WHERE username = (SELECT username FROM logged_user);");
            ResultSet rs = statement.executeQuery();
            final boolean[] matched = new boolean[1];
            String pHash = "";
            while (rs.next()) {
                pHash = rs.getString("password");
            }

            String finalPHash = pHash;
            String finalPHash1 = pHash;
            btnSubmit.setOnAction(event -> {
                String pWord = txtP.getText();
                try {
                    matched[0] = validatePassword(pWord, finalPHash1);
                    if (pWord.equals("")) {
                        Toast.makeText((Stage) btnSubmit.getScene().getWindow(), "Enter Password!", 1000, 500, 500, "rgba(246,10,10,0.2)");
                        return;
                    } else {
                        try {
                            if (validatePassword(pWord, finalPHash)) {
                                stg.hide();
                            } else {
                                Toast.makeText((Stage) btnSubmit.getScene().getWindow(), "Invalid Password!", 1000, 500, 500, "rgba(246,10,10,0.2)");
                                return;
                            }
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
            });

            stg.setScene(s);
            stg.show();
            stg.setOnCloseRequest(action -> {
                action.consume();
                Toast.makeText((Stage) btnSubmit.getScene().getWindow(), "Enter Password!", 1000, 500, 500, "rgba(246,10,10,0.2)");
            });

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
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
