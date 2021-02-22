package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass {
    public static Connection connection;

    public Connection getConnection() {
        String url = "jdbc:mysql://puff.mnstate.edu:3306/ronni-kurtzhals_gameusers?useUnicode=true&useJDBCCompliantTimezoneShift=t" +
                "rue&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=FALSE";

        String userName = "ronni-kurtzhals"; String pw = "Ginger!68";
        //System.out.println("Loading driver...");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find driver", e);
        }

        //System.out.println("Driver loaded");
        connection = null;
        //System.out.println("Trying to connect to database...");
        try {
            connection = DriverManager.getConnection(url, userName, pw);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //System.out.println("Connected to the database");
        return connection;
    }

    public static void closeConnection() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
