package proyecto.pos.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;

public class ProduccionController {

    private Connection conexion;

    public ProduccionController(Connection conexion) {
        this.conexion = conexion;
    }

    // =========================================================================
    // REGISTRAR Y APROBAR LOTE DE PRODUCCIÓN DIGITALMENTE (HU-12)
    // =========================================================================
    public boolean aprobarLoteProduccion(int subrecetaId, double cantidad, double costoInsumosBase) {
        // Criterio de Aceptación: Mantener los costos actualizados (Cálculo automático de costo total)
        double costoLoteTotal = cantidad * costoInsumosBase;

        // Criterio de Aceptación: Confirmación digital explícita y ágil para el Gerente
        int confirmacion = JOptionPane.showConfirmDialog(
                null,
                "¿Desea aprobar digitalmente el ingreso de este lote al inventario?\n\n" +
                "Resumen del Lote:\n" +
                "• ID de Sub-receta: " + subrecetaId + "\n" +
                "• Cantidad Producida: " + cantidad + "\n" +
                "• Costo Total Calculado: S/. " + String.format("%.2f", costoLoteTotal) + "\n",
                "Autorización Digital de Producción (HU-12)",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(null, 
                    "El lote no ha sido aprobado. Operación cancelada.", 
                    "Producción Rechazada", 
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        PreparedStatement ps = null;
        // Consulta nativa para la tabla PRODUCCION_LOTES en Oracle
        String sql = "INSERT INTO produccion_lotes (subreceta_id, cantidad_producida, costo_lote, fecha) " +
                     "VALUES (?, ?, ?, TRUNC(SYSDATE))";

        try {
            if (this.conexion != null) {
                ps = this.conexion.prepareStatement(sql);
                ps.setInt(1, subrecetaId);
                ps.setDouble(2, cantidad);
                ps.setDouble(3, costoLoteTotal);
                
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(
                            null, 
                            "¡Lote aprobado digitalmente e ingresado con éxito!\nCostos actualizados y trazabilidad registrada.", 
                            "Éxito en Inventario", 
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return true;
                }
            } else {
                throw new RuntimeException("La conexión a la base de datos es nula.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al registrar la aprobación digital: " + e.getMessage(),
                    "Error SQL / Base de Datos",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        } finally {
            try { 
                if (ps != null) ps.close(); 
            } catch (Exception e) {
                // Manejo silencioso de cierre de recursos
            }
        }
        return false;
    }
}
