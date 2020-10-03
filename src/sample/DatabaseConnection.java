package sample;

import java.awt.*;
import java.sql.*;

public class DatabaseConnection {

    public static Connection ConnectDB(){
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
        } catch (ClassNotFoundException | SQLException | HeadlessException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static boolean isConnected() {
        Connection connection = DatabaseConnection.ConnectDB();
        return connection != null;
    }

    public  static ResultSet getResultSet() throws SQLException {
        Connection conn = DatabaseConnection.ConnectDB();
        // ReadOnly
        Statement statement = (Statement) conn.createStatement();
        String sql = "SELECT id, word, html, description, pronounce FROM av";
        ResultSet rs = statement.executeQuery(sql);
        return rs;
    }
}
