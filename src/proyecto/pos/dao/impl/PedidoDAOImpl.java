/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;

/**
 *
 * @author USER
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.dao.interfaces.PedidoDAO;
import proyecto.pos.model.Pedido;
import proyecto.pos.model.PedidoDetalle;
import proyecto.pos.model.Plato;    
import proyecto.pos.model.Repartidor;

public class PedidoDAOImpl implements PedidoDAO {

    private Connection conexion;

    public PedidoDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public int registrarPedido(Pedido pedido) {
        String sqlPedido = "INSERT INTO PEDIDOS "
                + "(CLIENTE_NOMBRE, TELEFONO_CLIENTE, DIRECCION_ENTREGA, ESTADO, TOTAL, OBSERVACION) "
                + "VALUES (?, ?, ?, 'PENDIENTE', ?, ?)";

        String sqlDetalle = "INSERT INTO PEDIDO_DETALLE "
                + "(PEDIDO_ID, PLATO_ID, NOMBRE_PRODUCTO, CANTIDAD, PRECIO_UNITARIO, SUBTOTAL) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String sqlHistorial = "INSERT INTO PEDIDO_HISTORIAL "
                + "(PEDIDO_ID, ESTADO, DESCRIPCION) "
                + "VALUES (?, 'PENDIENTE', 'Pedido registrado correctamente')";

        try {
            conexion.setAutoCommit(false);
            pedido.recalcularTotal();

            int pedidoId;

            try (PreparedStatement ps = conexion.prepareStatement(sqlPedido, new String[]{"PEDIDO_ID"})) {
                ps.setString(1, pedido.getClienteNombre());
                ps.setString(2, pedido.getTelefonoCliente());
                ps.setString(3, pedido.getDireccionEntrega());
                ps.setDouble(4, pedido.getTotal());
                ps.setString(5, pedido.getObservacion());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pedidoId = rs.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID del pedido.");
                    }
                }
            }

            if (pedido.getDetalles() != null) {
                try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                    for (PedidoDetalle detalle : pedido.getDetalles()) {
                        psDetalle.setInt(1, pedidoId);

                        if (detalle.getPlato() != null) {
                            psDetalle.setInt(2, detalle.getPlato().getPlatoId());
                        } else {
                            psDetalle.setNull(2, Types.INTEGER);
                        }

                        psDetalle.setString(3, detalle.getNombreProducto());
                        psDetalle.setInt(4, detalle.getCantidad());
                        psDetalle.setDouble(5, detalle.getPrecioUnitario());
                        psDetalle.setDouble(6, detalle.getSubtotal());
                        psDetalle.addBatch();
                    }

                    psDetalle.executeBatch();
                }
            }

            try (PreparedStatement psHistorial = conexion.prepareStatement(sqlHistorial)) {
                psHistorial.setInt(1, pedidoId);
                psHistorial.executeUpdate();
            }

            conexion.commit();
            conexion.setAutoCommit(true);
            return pedidoId;

        } catch (SQLException e) {
            try {
                conexion.rollback();
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            throw new RuntimeException("Error al registrar pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> listarPedidos() {
        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT "
                + "p.PEDIDO_ID, p.CLIENTE_NOMBRE, p.TELEFONO_CLIENTE, p.DIRECCION_ENTREGA, "
                + "p.FECHA_PEDIDO, p.ESTADO, p.TOTAL, p.REPARTIDOR_ID, p.FECHA_ASIGNACION, "
                + "p.FECHA_SALIDA, p.FECHA_ENTREGA, p.EVIDENCIA, p.MOTIVO_NO_ENTREGA, p.OBSERVACION, "
                + "r.NOMBRE AS REPARTIDOR_NOMBRE "
                + "FROM PEDIDOS p "
                + "LEFT JOIN REPARTIDORES r ON p.REPARTIDOR_ID = r.REPARTIDOR_ID "
                + "ORDER BY p.PEDIDO_ID DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPedidoConRepartidor(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pedidos: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public List<Pedido> listarPedidosPendientes() {
        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT "
                + "PEDIDO_ID, CLIENTE_NOMBRE, TELEFONO_CLIENTE, DIRECCION_ENTREGA, "
                + "FECHA_PEDIDO, ESTADO, TOTAL, REPARTIDOR_ID, FECHA_ASIGNACION, "
                + "FECHA_SALIDA, FECHA_ENTREGA, EVIDENCIA, MOTIVO_NO_ENTREGA, OBSERVACION "
                + "FROM PEDIDOS "
                + "WHERE ESTADO = 'PENDIENTE' "
                + "ORDER BY PEDIDO_ID ASC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPedidoBasico(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pedidos pendientes: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public boolean asignarRepartidor(int pedidoId, int repartidorId) {
        String sql = "UPDATE PEDIDOS "
                + "SET REPARTIDOR_ID = ?, ESTADO = 'ASIGNADO', FECHA_ASIGNACION = CURRENT_TIMESTAMP "
                + "WHERE PEDIDO_ID = ?";

        String sqlHistorial = "INSERT INTO PEDIDO_HISTORIAL "
                + "(PEDIDO_ID, ESTADO, DESCRIPCION) "
                + "VALUES (?, 'ASIGNADO', 'Pedido asignado a repartidor')";

        try {
            conexion.setAutoCommit(false);

            int filas;

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, repartidorId);
                ps.setInt(2, pedidoId);
                filas = ps.executeUpdate();
            }

            try (PreparedStatement psHistorial = conexion.prepareStatement(sqlHistorial)) {
                psHistorial.setInt(1, pedidoId);
                psHistorial.executeUpdate();
            }

            conexion.commit();
            conexion.setAutoCommit(true);
            return filas > 0;

        } catch (SQLException e) {
            try {
                conexion.rollback();
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            throw new RuntimeException("Error al asignar repartidor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> listarPedidosPorRepartidor(int repartidorId) {
        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT "
                + "p.PEDIDO_ID, p.CLIENTE_NOMBRE, p.TELEFONO_CLIENTE, p.DIRECCION_ENTREGA, "
                + "p.FECHA_PEDIDO, p.ESTADO, p.TOTAL, p.REPARTIDOR_ID, p.FECHA_ASIGNACION, "
                + "p.FECHA_SALIDA, p.FECHA_ENTREGA, p.EVIDENCIA, p.MOTIVO_NO_ENTREGA, p.OBSERVACION, "
                + "r.NOMBRE AS REPARTIDOR_NOMBRE "
                + "FROM PEDIDOS p "
                + "LEFT JOIN REPARTIDORES r ON p.REPARTIDOR_ID = r.REPARTIDOR_ID "
                + "WHERE p.REPARTIDOR_ID = ? "
                + "AND p.ESTADO IN ('ASIGNADO', 'EN_CAMINO', 'REPROGRAMADO') "
                + "ORDER BY p.PEDIDO_ID ASC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, repartidorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPedidoConRepartidor(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar pedidos del repartidor: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public boolean cambiarEstadoPedido(int pedidoId, String nuevoEstado, String evidencia, String motivo) {
        String sql = "UPDATE PEDIDOS SET ESTADO = ?, EVIDENCIA = ?, MOTIVO_NO_ENTREGA = ?, "
                + "FECHA_SALIDA = CASE WHEN ? = 'EN_CAMINO' THEN CURRENT_TIMESTAMP ELSE FECHA_SALIDA END, "
                + "FECHA_ENTREGA = CASE WHEN ? = 'ENTREGADO' THEN CURRENT_TIMESTAMP ELSE FECHA_ENTREGA END "
                + "WHERE PEDIDO_ID = ?";

        String sqlHistorial = "INSERT INTO PEDIDO_HISTORIAL "
                + "(PEDIDO_ID, ESTADO, DESCRIPCION) "
                + "VALUES (?, ?, ?)";

        try {
            conexion.setAutoCommit(false);

            int filas;

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, nuevoEstado);
                ps.setString(2, evidencia);
                ps.setString(3, motivo);
                ps.setString(4, nuevoEstado);
                ps.setString(5, nuevoEstado);
                ps.setInt(6, pedidoId);
                filas = ps.executeUpdate();
            }

            try (PreparedStatement psHistorial = conexion.prepareStatement(sqlHistorial)) {
                psHistorial.setInt(1, pedidoId);
                psHistorial.setString(2, nuevoEstado);
                psHistorial.setString(3, "Estado actualizado a " + nuevoEstado);
                psHistorial.executeUpdate();
            }

            conexion.commit();
            conexion.setAutoCommit(true);
            return filas > 0;

        } catch (SQLException e) {
            try {
                conexion.rollback();
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            throw new RuntimeException("Error al cambiar estado del pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Repartidor> listarRepartidores() {
        List<Repartidor> lista = new ArrayList<>();

        String sql = "SELECT r.REPARTIDOR_ID, r.NOMBRE, r.TELEFONO, r.DISPONIBLE, "
                + "COUNT(p.PEDIDO_ID) AS PEDIDOS_ASIGNADOS "
                + "FROM REPARTIDORES r "
                + "LEFT JOIN PEDIDOS p ON r.REPARTIDOR_ID = p.REPARTIDOR_ID "
                + "AND p.ESTADO IN ('ASIGNADO', 'EN_CAMINO', 'REPROGRAMADO') "
                + "WHERE r.DISPONIBLE = 'S' "
                + "GROUP BY r.REPARTIDOR_ID, r.NOMBRE, r.TELEFONO, r.DISPONIBLE "
                + "ORDER BY PEDIDOS_ASIGNADOS ASC, r.NOMBRE ASC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Repartidor repartidor = new Repartidor();
                repartidor.setRepartidorId(rs.getInt(1));
                repartidor.setNombre(rs.getString(2));
                repartidor.setTelefono(rs.getString(3));
                repartidor.setDisponible("S".equals(rs.getString(4)));
                repartidor.setPedidosAsignados(rs.getInt(5));
                lista.add(repartidor);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar repartidores: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public List<Plato> listarProductosParaGestion() {
        List<Plato> lista = new ArrayList<>();

        String sql = "SELECT PLATO_ID, NOMBRE_PLATO, PRECIO_VENTA, DISPONIBLE, IMAGEN "
                + "FROM PLATOS_MENU "
                + "ORDER BY NOMBRE_PLATO ASC";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Plato plato = new Plato();
                plato.setPlatoId(rs.getInt(1));
                plato.setNombre(rs.getString(2));
                plato.setPrecio(rs.getFloat(3));
                plato.setDisponible(rs.getInt(4));
                plato.setImagen(rs.getString(5));
                lista.add(plato);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public boolean actualizarDisponibilidadProducto(int platoId, int disponible) {
        String sql = "UPDATE PLATOS_MENU SET DISPONIBLE = ? WHERE PLATO_ID = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, disponible);
            ps.setInt(2, platoId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar disponibilidad del producto: " + e.getMessage(), e);
        }
    }

    private Pedido mapearPedidoBasico(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();

        pedido.setPedidoId(rs.getInt(1));
        pedido.setClienteNombre(rs.getString(2));
        pedido.setTelefonoCliente(rs.getString(3));
        pedido.setDireccionEntrega(rs.getString(4));

        Timestamp fechaPedido = rs.getTimestamp(5);
        if (fechaPedido != null) {
            pedido.setFechaPedido(fechaPedido);
        }

        pedido.setEstado(rs.getString(6));
        pedido.setTotal(rs.getDouble(7));

        int repartidorId = rs.getInt(8);
        if (!rs.wasNull()) {
            pedido.setRepartidorId(repartidorId);
        }

        Timestamp fechaAsignacion = rs.getTimestamp(9);
        if (fechaAsignacion != null) {
            pedido.setFechaAsignacion(fechaAsignacion);
        }

        Timestamp fechaSalida = rs.getTimestamp(10);
        if (fechaSalida != null) {
            pedido.setFechaSalida(fechaSalida);
        }

        Timestamp fechaEntrega = rs.getTimestamp(11);
        if (fechaEntrega != null) {
            pedido.setFechaEntrega(fechaEntrega);
        }

        pedido.setEvidencia(rs.getString(12));
        pedido.setMotivoNoEntrega(rs.getString(13));
        pedido.setObservacion(rs.getString(14));

        return pedido;
    }

    private Pedido mapearPedidoConRepartidor(ResultSet rs) throws SQLException {
        Pedido pedido = mapearPedidoBasico(rs);
        pedido.setRepartidorNombre(rs.getString(15));
        return pedido;
    }
}