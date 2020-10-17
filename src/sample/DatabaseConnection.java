package sample;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    public static Connection conn = null;
    public static Connection ConnectDB() {
        if (conn != null) return conn;
        else {
            try {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:dict_hh.db");
            } catch (ClassNotFoundException | SQLException | HeadlessException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static boolean isConnected() {
        Connection connection = DatabaseConnection.ConnectDB();
        return connection != null;
    }

    public static ResultSet getResultSet(String tableName) throws SQLException {
        Connection conn = DatabaseConnection.ConnectDB();
        Statement statement = conn.createStatement();
        String sql = "SELECT id, word, html, description, pronounce FROM " + tableName;
        ResultSet rs = statement.executeQuery(sql);
        return rs;
    }

    public static List<Word> getDictionary(String tableName) {

        List<Word> result = new ArrayList<>();

        if (DatabaseConnection.isConnected()) {
            try {
                ResultSet rs = DatabaseConnection.getResultSet(tableName);
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String word = rs.getString("word");
                    String html = rs.getString("html");
                    String description = rs.getString("description");
                    String pronounce = rs.getString("pronounce");

                    Word newWord = new Word(id, word, html, description, pronounce);
                    result.add(newWord);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Error while load database!");
            System.exit(1);
        }
        return result;
    }

    // Sửa lại sau khi dùng trie
    public static boolean isContains(String tableName, Word word) {
        String wordTarget = word.getWordTarget();
        try {
            ResultSet rs = DatabaseConnection.getResultSet("av");
            while (rs.next()) {
                String str = rs.getString("word");
                if (str.equals(wordTarget)) {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static int getTableSize(String tableName) throws SQLException {
        ResultSet rs = DatabaseConnection.getResultSet(tableName);
        int size = 0;

        /**
         * SQLite not support: rs.last().
         * Replace with this loop.
         */
        while (rs.next()) {
            size = rs.getInt(1);
        }
        return size;
    }

    public static void addWordToDB(String tableName, Word word) {
        Connection conn = DatabaseConnection.ConnectDB();
        try {
            Statement statement = conn.createStatement();
            int size = DatabaseConnection.getTableSize(tableName);
            word.setId(size + 1);
            String sql = "INSERT INTO " + tableName + " (id, word, html, description, pronounce) VALUES ("
                    + word.getId() + ", '" + word.getWordTarget() + "', '" + word.getHtml()
                    + "', '" + word.getDescription() + "', '" + word.getPronounce() + "')";
            statement.executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void updateWordToDB(String tableName, String word, String html, String description, String pronounce) {
        Connection conn = DatabaseConnection.ConnectDB();
        try {
            Statement statement = conn.createStatement();
            String sql = "UPDATE " + tableName + " SET html = '" + html + "' ,description = '" + description
                    + "' ,pronounce = '" + pronounce + "' WHERE word = '" + word + "'";
            statement.executeUpdate(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteWordDB(String tableName, String word) {
        Connection conn = DatabaseConnection.ConnectDB();
        try {
            Statement statement = conn.createStatement();
            String sql = "DELETE FROM " + tableName + " WHERE word='" + word + "';";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}