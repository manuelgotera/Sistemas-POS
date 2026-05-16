package proyecto.pos.dao.impl;

import proyecto.pos.model.Transaccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDAOImpl {
    private Connection conexion;

    public TransaccionDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    public List<Transaccion> listarHistorial() {
        List<Transaccion> lista = new ArrayList<>();
        
        // Consulta SQL calibrada exactamente con las columnas de tu tabla VENTAS_CABECERA
        String sql = "SELECT c.VENTA_ID, c.FECHA_HORA, c.EMPLEADO_ID, c.TOTAL, c.ESTADO_PAGO, " +
                     "(SELECT COUNT(*) FROM VENTAS_DETALLE d WHERE d.VENTA_ID = c.VENTA_ID) AS TOTAL_ITEMS " +
                     "FROM VENTAS_CABECERA c " +
                     "ORDER BY c.FECHA_HORA DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setIdVenta(rs.getInt("VENTA_ID"));
                t.setFechaHora(rs.getTimestamp("FECHA_HORA"));
                t.setCajero("ID Emp: " + rs.getInt("EMPLEADO_ID")); 
                t.setMontoTotal(rs.getDouble("TOTAL"));
                t.setEstado(rs.getString("ESTADO_PAGO"));
                t.setTotalItems(rs.getInt("TOTAL_ITEMS"));
                
                // Fallbacks seguros debido a que estas dos columnas no están explícitas en VENTAS_CABECERA
                t.setComprobante("Boleta"); 
                t.setMetodoPago("Efectivo");
                
                lista.add(t);
            }
        } catch (Exception e) {
            System.err.println("❌ Error en TransaccionDAOImpl al listar historial: " + e.getMessage());
        }
        return lista;
    }
}