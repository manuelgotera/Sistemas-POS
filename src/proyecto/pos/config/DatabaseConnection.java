package proyecto.pos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    
    // 1. CAMBIO DE SERVICIO: A tu Oracle XE local
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    
    // 2. CAMBIO DE USUARIO: A tu usuario administrador SYS
    private static final String USER = "SYS";
    
    // 3. CAMBIO DE CONTRASEÑA: La tuya
    private static final String PASSWORD = "Manuel1828."; 

    public DatabaseConnection() {}

    public Connection conectar() {
        try {
            // Cargamos el driver
            Class.forName(DRIVER); 
            
            // Configuración especial obligatoria para conectarse como SYSDBA
            Properties props = new Properties();
            props.put("user", USER);
            props.put("password", PASSWORD);
            props.put("internal_logon", "sysdba");

            Connection conn = DriverManager.getConnection(URL, props);
            System.out.println("✅ Conectado correctamente a tu base de datos XE local");
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
