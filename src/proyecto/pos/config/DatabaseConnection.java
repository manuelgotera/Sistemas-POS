package proyecto.pos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";

    // Mantiene tu mismo servicio local (asegúrate de que Tableau use localhost o la misma IP)
// Cambia el ":" final por "/" y escribe xedbc1
private static final String URL = "jdbc:oracle:thin:@localhost:1521/XEPDB1";    // 1. CAMBIO DE USUARIO: Al usuario unificado con Tableau
    private static final String USER = "gustavo_alva";

    // 2. CAMBIO DE CONTRASEÑA: La contraseña de ese analista
    private static final String PASSWORD = "gustavo456";

    public DatabaseConnection() {}

    public Connection conectar() {
        try {
            // Cargamos el driver
            Class.forName(DRIVER);

            // 3. CONEXIÓN DIRECTA: Ya no se necesitan Properties ni "sysdba"
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conectado correctamente como MANU_ANALISTA (Esquema compartido)");
            return conn;

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void desconectar(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("🔌 Conexión cerrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}