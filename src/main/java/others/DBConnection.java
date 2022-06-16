package others;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/ims";

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, "postgres", "admin");
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return con;
    }
}
