package inventory.feed.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/inventorydb";
        String user = "root";
        String password = "2003";
        return DriverManager.getConnection(url, user, password);
    }
}
