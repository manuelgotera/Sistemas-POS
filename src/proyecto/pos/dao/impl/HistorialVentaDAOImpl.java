package proyecto.pos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.HistorialVentaDetalle;
import proyecto.pos.model.HistorialVentaItem;

/**
 * DAO para consultar el historial de ventas con sus comprobantes: BOLETA y FACTURA.
 */
public class HistorialVentaDAOImpl {

    private final Connection conexion;

    public HistorialVentaDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    public List<HistorialVentaItem> listarHistorial() {
        List<HistorialVentaItem> historial = new ArrayList<>();

        String sql = """
            SELECT v.venta_id,
                   v.fecha_hora,
                   v.subtotal,
                   v.igv,
                   v.descuento,
                   v.total,
                   v.estado_pago,
                   NVL(e.nombre, 'Sin cajero') AS cajero,
                   cp.tipo_comprobante,
                   cp.serie_numero,
                   cp.estado AS estado_comprobante,
                   NVL(mp.nombre, 'Sin método') AS metodo_pago,
                   NVL((SELECT SUM(vd.cantidad)
                        FROM ventas_detalle vd
                        WHERE vd.venta_id = v.venta_id), 0) AS total_items
            FROM ventas_cabecera v
            JOIN comprobantes_pago cp ON cp.venta_id = v.venta_id
            LEFT JOIN empleados e ON e.empleado_id = v.empleado_id
            LEFT JOIN metodos_pago mp ON mp.id_metodo_pago = cp.id_metodo
            WHERE UPPER(cp.tipo_comprobante) IN ('BOLETA', 'FACTURA')
            ORDER BY cp.fecha_emision DESC, v.venta_id DESC
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                HistorialVentaItem item = new HistorialVentaItem();
                item.setVentaId(rs.getInt("venta_id"));
                item.setFechaHora(rs.getTimestamp("fecha_hora"));
                item.setSubtotal(rs.getDouble("subtotal"));
                item.setIgv(rs.getDouble("igv"));
                item.setDescuento(rs.getDouble("descuento"));
                item.setTotal(rs.getDouble("total"));
                item.setEstado(valorNoVacio(rs.getString("estado_comprobante"), rs.getString("estado_pago")));
                item.setCajero(rs.getString("cajero"));
                item.setTipoComprobante(rs.getString("tipo_comprobante"));
                item.setSerieNumero(rs.getString("serie_numero"));
                item.setMetodoPago(rs.getString("metodo_pago"));
                item.setTotalItems(rs.getInt("total_items"));
                historial.add(item);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar historial de ventas", ex);
        }

        return historial;
    }

    public List<HistorialVentaDetalle> listarDetallesPorVenta(int ventaId) {
        List<HistorialVentaDetalle> detalles = new ArrayList<>();

        String sql = """
            SELECT NVL(p.nombre_plato, 'Producto eliminado') AS producto,
                   vd.cantidad,
                   vd.precio_unitario_venta,
                   vd.subtotal
            FROM ventas_detalle vd
            LEFT JOIN platos_menu p ON p.plato_id = vd.plato_id
            WHERE vd.venta_id = ?
            ORDER BY vd.detalle_id
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialVentaDetalle detalle = new HistorialVentaDetalle();
                    detalle.setProducto(rs.getString("producto"));
                    detalle.setCantidad(rs.getInt("cantidad"));
                    detalle.setPrecioUnitario(rs.getDouble("precio_unitario_venta"));
                    detalle.setSubtotal(rs.getDouble("subtotal"));
                    detalles.add(detalle);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar detalles de venta", ex);
        }

        return detalles;
    }

    private String valorNoVacio(String principal, String alternativo) {
        if (principal != null && !principal.trim().isEmpty()) {
            return principal;
        }
        return alternativo == null || alternativo.trim().isEmpty() ? "Completada" : alternativo;
    }
}
