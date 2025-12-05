package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/storecell?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";    // <-- altere aqui
    private static final String PASS = "1234";      // <-- altere aqui

    static {
        try {
            // registra driver (opcional em versões recentes do driver)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC não encontrado: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
