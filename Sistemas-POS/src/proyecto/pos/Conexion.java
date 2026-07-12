package proyecto.pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Connection contacto = null;

    public static Connection getConexion() {
        String url = "jdbc:oracle:thin:@localhost:1521/FREEPDB1";
        try {
            // Solo conectamos si no existe una conexión previa
            if (contacto == null || contacto.isClosed()) {
                contacto = DriverManager.getConnection(url, "GUSTAVO_ALVA", "Gustavo456");
            }
        } catch (SQLException e) {
            System.err.println("Error en clase conexión: " + e.getMessage());
        }
        return contacto;
    }
}

