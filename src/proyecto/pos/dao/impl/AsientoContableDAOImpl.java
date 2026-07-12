package proyecto.pos.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.dao.interfaces.AsientoContableDAO;
import proyecto.pos.model.AsientoContable;
import proyecto.pos.model.VentaResumenCaja;

/**
 * HU-07 — Implementación autocontenida: consulta ventas_cabecera directamente
 * con LEFT JOIN (no reutiliza VentaDAOImpl) para no arriesgar cambios en
 * clases ya usadas por otras pantallas del equipo.
 */
public class AsientoContableDAOImpl implements AsientoContableDAO {

    private final Connection conexion;

    public AsientoContableDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(AsientoContable asiento) {
        String sql = """
            INSERT INTO ASIENTO_CONTABLE
                (CAJA_ID, MONTO_INICIAL, TOTAL_VENTAS, MONTO_FINAL,
                 MONTO_ESPERADO, DIFERENCIA, ESTADO_SINCRONIZACION, MENSAJE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, asiento.getCajaId());
            ps.setDouble(2, asiento.getMontoInicial());
            ps.setDouble(3, asiento.getTotalVentas());
            ps.setDouble(4, asiento.getMontoFinal());
            ps.setDouble(5, asiento.getMontoEsperado());
            ps.setDouble(6, asiento.getDiferencia());
            ps.setString(7, asiento.getEstadoSincronizacion());
            ps.setString(8, asiento.getMensaje());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar asiento contable", e);
        }
    }

    @Override
    public List<AsientoContable> listarPorCaja(int cajaId) {
        List<AsientoContable> lista = new ArrayList<>();
        String sql = """
            SELECT asiento_id, caja_id, fecha_sincronizacion, monto_inicial,
                   total_ventas, monto_final, monto_esperado, diferencia,
                   estado_sincronizacion, mensaje
            FROM ASIENTO_CONTABLE
            WHERE caja_id = ?
            ORDER BY fecha_sincronizacion DESC
        """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cajaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar asientos contables de la caja " + cajaId, e);
        }
        return lista;
    }

    @Override
    public double obtenerTotalVentasPagadas(int cajaId) {
        String sql = """
            SELECT NVL(SUM(total), 0) AS total_pagado
            FROM ventas_cabecera
            WHERE caja_id = ? AND estado_pago = 'PAGADO'
        """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cajaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("total_pagado");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al calcular total de ventas de la caja " + cajaId, e);
        }
        return 0.0;
    }

    @Override
    public List<VentaResumenCaja> listarVentasDeCaja(int cajaId) {
        List<VentaResumenCaja> lista = new ArrayList<>();
        String sql = """
            SELECT v.venta_id, v.fecha_hora, c.nombre AS cliente_nombre, v.total
            FROM ventas_cabecera v
            LEFT JOIN clientes c ON v.cliente_id = c.cliente_id
            WHERE v.caja_id = ? AND v.estado_pago = 'PAGADO'
            ORDER BY v.fecha_hora DESC
        """;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, cajaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cliente = rs.getString("cliente_nombre");
                    lista.add(new VentaResumenCaja(
                        rs.getInt("venta_id"),
                        rs.getTimestamp("fecha_hora"),
                        cliente != null ? cliente : "Público General",
                        rs.getDouble("total")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas de la caja " + cajaId, e);
        }
        return lista;
    }

    private AsientoContable mapear(ResultSet rs) throws SQLException {
        AsientoContable a = new AsientoContable();
        a.setAsientoId(rs.getInt("asiento_id"));
        a.setCajaId(rs.getInt("caja_id"));
        a.setFechaSincronizacion(rs.getTimestamp("fecha_sincronizacion"));
        a.setMontoInicial(rs.getDouble("monto_inicial"));
        a.setTotalVentas(rs.getDouble("total_ventas"));
        a.setMontoFinal(rs.getDouble("monto_final"));
        a.setMontoEsperado(rs.getDouble("monto_esperado"));
        a.setDiferencia(rs.getDouble("diferencia"));
        a.setEstadoSincronizacion(rs.getString("estado_sincronizacion"));
        a.setMensaje(rs.getString("mensaje"));
        return a;
    }
}