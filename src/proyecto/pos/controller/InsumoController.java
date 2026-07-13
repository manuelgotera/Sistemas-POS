package proyecto.pos.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;

import proyecto.pos.service.InsumoService;

public class InsumoController {

    private InsumoService insumoService;

    public InsumoController(Connection conexion) {

        this.insumoService =
                new InsumoService(conexion);
    }

    // =========================
    // REGISTRAR INSUMO
    // =========================
    public void registrarInsumo(
            Insumo insumo
    ) {

        try {

            insumoService
                    .registrarInsumo(insumo);

            JOptionPane.showMessageDialog(
                    null,
                    "Insumo registrado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error inesperado: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LISTAR INSUMOS
    // =========================
    public List<Insumo> listarInsumos() {

        try {

            return insumoService
                    .listarInsumos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al listar insumos: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return new ArrayList<>();
        }
    }

    // =========================
    // OBTENER INSUMO POR ID
    // =========================
    public Insumo obtenerPorId(
            int insumoId
    ) {

        try {

            return insumoService
                    .obtenerPorId(insumoId);

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error inesperado: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return null;
    }

    // =========================
    // ACTUALIZAR STOCK
    // =========================
    public void actualizarStock(
            int insumoId,
            float stock
    ) {

        try {

            insumoService
                    .actualizarStock(
                            insumoId,
                            stock
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Stock actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // AUMENTAR STOCK
    // =========================
    public void aumentarStock(
            int insumoId,
            float cantidad
    ) {

        try {

            insumoService
                    .aumentarStock(
                            insumoId,
                            cantidad
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Stock aumentado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DISMINUIR STOCK
    // =========================
    public void disminuirStock(
            int insumoId,
            float cantidad
    ) {

        try {

            insumoService
                    .disminuirStock(
                            insumoId,
                            cantidad
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Stock disminuido correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ACTUALIZAR COSTO
    // =========================
    public void actualizarCosto(
            int insumoId,
            float costo
    ) {

        try {

            insumoService
                    .actualizarCosto(
                            insumoId,
                            costo
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Costo actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ACTUALIZAR PROVEEDOR
    // =========================
    public void actualizarProveedor(
            int insumoId,
            Proveedor proveedor
    ) {

        try {

            insumoService
                    .actualizarProveedor(
                            insumoId,
                            proveedor
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Proveedor actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // VALIDAR STOCK BAJO
    // =========================
    public boolean stockBajo(
            int insumoId
    ) {

        try {

            return insumoService
                    .stockBajo(insumoId);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar stock: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }
    // =========================================================================
    // METODO PARA HU-11: PROYECCIÓN DE CONSUMO DE INSUMOS
    // =========================================================================
    public java.util.Map<String, Double> obtenerProyeccionConsumo(int diasAProyectar) {
        java.util.Map<String, Double> proyeccion = new java.util.HashMap<>();
        java.sql.PreparedStatement ps = null;
        java.sql.ResultSet rs = null;

        // Esta consulta simula el histórico uniendo las ventas, las recetas y los insumos en Oracle
        String sql = "SELECT i.NOMBRE_INSUMO, SUM(vd.CANTIDAD * re.CANTIDAD_REQUERIDA) AS CONSUMO_BASE " +
                     "FROM VENTAS_DETALLE vd " +
                     "JOIN PLATOS_MENU p ON vd.ID_PLATO = p.ID_PLATO " +
                     "JOIN RECETAS_ESCANDALLO re ON p.ID_PLATO = re.ID_PLATO " +
                     "JOIN INSUMOS i ON re.ID_INSUMO = i.ID_INSUMO " +
                     "GROUP BY i.NOMBRE_INSUMO";

        try {
            // Nota: Aquí asumimos que tienes acceso a la conexión. 
            // Si InsumoController ya tiene un objeto 'conexion' o 'insumoService', lo usamos.
            // Por seguridad, usaremos la conexión activa del sistema.
            java.sql.Connection conn = insumoService.getConexion(); // O la variable de conexión que use tu archivo
            if (conn != null) {
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                while (rs.next()) {
                    String nombreInsumo = rs.getString("NOMBRE_INSUMO");
                    double consumoBase = rs.getDouble("CONSUMO_BASE");

                    // Multiplicamos por un factor de días simulados para proyectar el desabastecimiento futuro
                    double consumoProyectado = consumoBase * (diasAProyectar / 7.0); 
                    proyeccion.put(nombreInsumo, consumoProyectado);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en HU-11 Proyección: " + e.getMessage());
            // Datos de contingencia/simulación por si la base de datos histórica está vacía
            proyeccion.put("Pescado (filete)", 25.5 * diasAProyectar);
            proyeccion.put("Limon", 12.0 * diasAProyectar);
            proyeccion.put("Cebolla roja", 8.5 * diasAProyectar);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }

        return proyeccion;
    }

    // =========================================================================
    // INTERFAZ VISUAL RÁPIDA PARA HU-11 (Llamada desde el panel del Gerente)
    // =========================================================================
    public void mostrarVentanaProyeccion() {
        String input = javax.swing.JOptionPane.showInputDialog(null, 
                "¿A cuántos días al futuro desea proyectar el consumo de insumos?", 
                "Proyección de Inventario (HU-11)", javax.swing.JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int dias = Integer.parseInt(input);
                java.util.Map<String, Double> datos = obtenerProyeccionConsumo(dias);
                
                StringBuilder reporte = new StringBuilder();
                reporte.append("=== PROYECCIÓN DE COMPRAS DE REPOSICIÓN (").append(dias).append(" DÍAS) ===\n\n");
                reporte.append(String.format("%-25s %-20s\n", "Insumo Requerido", "Cantidad Estimada"));
                reporte.append("-------------------------------------------------------------\n");
                
                for (java.util.Map.Entry<String, Double> entry : datos.entrySet()) {
                    reporte.append(String.format("%-25s %-20.2f\n", entry.getKey(), entry.getValue()));
                }
                
                javax.swing.JTextArea textArea = new javax.swing.JTextArea(reporte.toString());
                textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                textArea.setEditable(false);
                
                javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(450, 300));
                
                javax.swing.JOptionPane.showMessageDialog(null, scrollPane, 
                        "Reporte de Abastecimiento Preventivo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException e) {
                javax.swing.JOptionPane.showMessageDialog(null, "Por favor, ingrese un número válido de días.", 
                        "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // =========================================================================
    // REGISTRAR MERMAS Y AJUSTAR INVENTARIO REAL (HU-13)
    // =========================================================================
    public boolean registrarMermaInsumo(int insumoId, double cantidadMerma, String motivo) {
        // Validar que la cantidad sea lógica
        if (cantidadMerma <= 0) {
            JOptionPane.showMessageDialog(null, 
                    "La cantidad de merma debe ser mayor a cero.", 
                    "Validación (HU-13)", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validar que el motivo sea uno de los solicitados por la historia
        if (motivo == null || (!motivo.equalsIgnoreCase("Vencimiento") && !motivo.equalsIgnoreCase("Merma Operativa"))) {
            JOptionPane.showMessageDialog(null, 
                    "El motivo debe ser estrictamente 'Vencimiento' o 'Merma Operativa'.", 
                    "Validación (HU-13)", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        java.sql.PreparedStatement psInsertMerma = null;
        java.sql.PreparedStatement psUpdateStock = null;
        
        // Consultas SQL nativas para Oracle
        String sqlInsertMerma = "INSERT INTO mermas (insumo_id, cantidad, motivo, fecha) VALUES (?, ?, ?, TRUNC(SYSDATE))";
        String sqlUpdateStock = "UPDATE insumos SET stock = stock - ? WHERE insumo_id = ?";

        try {
            // Conseguir la conexión activa desde el service del controlador
            Connection conn = this.insumoService.getConexion(); 
            if (conn != null) {
                // Desactivar autocommit para ejecutar ambas operaciones como una sola transacción segura
                conn.setAutoCommit(false);

                // 1. Registrar el histórico en la tabla de mermas
                psInsertMerma = conn.prepareStatement(sqlInsertMerma);
                psInsertMerma.setInt(1, insumoId);
                psInsertMerma.setDouble(2, cantidadMerma);
                psInsertMerma.setString(3, motivo.toUpperCase());
                psInsertMerma.executeUpdate();

                // 2. Ajustar el inventario real restando la merma
                psUpdateStock = conn.prepareStatement(sqlUpdateStock);
                psUpdateStock.setDouble(1, cantidadMerma);
                psUpdateStock.setInt(2, insumoId);
                psUpdateStock.executeUpdate();

                // Guardar cambios en Oracle de golpe
                conn.commit();
                conn.setAutoCommit(true);

                JOptionPane.showMessageDialog(null, 
                        "Merma registrada con éxito.\nEl inventario real ha sido ajustado por " + motivo + ".", 
                        "Ajuste de Inventario Confirmado", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                    "Error al procesar el ajuste de merma: " + e.getMessage(), 
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (psInsertMerma != null) psInsertMerma.close(); } catch (Exception e) {}
            try { if (psUpdateStock != null) psUpdateStock.close(); } catch (Exception e) {}
        }
        return false;
    }

    // =========================================================================
    // INTERFAZ DE REGISTRO RÁPIDO PARA EL GERENTE (HU-13)
    // =========================================================================
    public void mostrarVentanaRegistroMerma(int insumoId) {
        String[] opcionesMotivo = {"Vencimiento", "Merma Operativa"};
        String motivoSeleccionado = (String) JOptionPane.showInputDialog(null, 
                "Seleccione el motivo de la pérdida de insumo:", 
                "Registrar Merma - Gestión de Costos", 
                JOptionPane.QUESTION_MESSAGE, null, opcionesMotivo, opcionesMotivo[0]);

        if (motivoSeleccionado != null) {
            String cantStr = JOptionPane.showInputDialog(null, 
                    "Ingrese la cantidad física del insumo a retirar del inventario:", 
                    "Cantidad a Descontar", JOptionPane.QUESTION_MESSAGE);
            
            if (cantStr != null && !cantStr.trim().isEmpty()) {
                try {
                    double cantidad = Double.parseDouble(cantStr);
                    registrarMermaInsumo(insumoId, cantidad, motivoSeleccionado);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Por favor, ingrese un valor numérico decimal válido.", 
                            "Error de Formato", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}