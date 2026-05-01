package proyecto.pos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER = "GUSTAVO_ALVA";
    private static final String PASSWORD = "gustavo456";

    // 🔹 Constructor vacío
    public DatabaseConnection() {}

    // 🔹 Método conectar
    /*public Connection conectar() {
           try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la BD", e);
        }
    }*/
    public Connection conectar() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conectado correctamente");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace(); // 👈 ESTO ES LO IMPORTANTE
            return null;
        }
    }

    // 🔹 Método desconectar
    public void desconectar(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}