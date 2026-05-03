/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.VentaDAO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;
import java.sql.*;
import java.time.ZoneId;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Cliente;
import proyecto.pos.model.ComprobantePago;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.EstadoPago;
import proyecto.pos.model.Mesa;
import proyecto.pos.model.MetodoPago;
import proyecto.pos.model.Plato;
/**
 *
 * @author HP
 */
public class VentaDAOImpl implements VentaDAO {

    private Connection conexion;
    
    public VentaDAOImpl(Connection conexion){
        this.conexion = conexion;
    }
    
    @Override
    //Comprobante de pago tiene unique en nro serie
    public void insertar(Venta venta) {
        String sqlVenta = "INSERT INTO ventas_cabecera " +
                          "(cliente_id, empleado_id, mesa_id, fecha_hora, igv, subtotal, descuento, total, estado_pago, caja_id) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlDetalle = "INSERT INTO ventas_detalle " +
                            "(venta_id, plato_id, cantidad, precio_unitario_venta, subtotal, comentario_cocina) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

        String sqlComprobante = "INSERT INTO comprobantes_pago " +
                                "(venta_id, tipo_comprobante, serie_numero, id_metodo, fecha_emision, estado) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conexion.setAutoCommit(false);
            // ----------------- Venta -----------------
            try (PreparedStatement psVenta = conexion.prepareStatement(sqlVenta, new String[]{"VENTA_ID"})){
                psVenta.setInt(1, venta.getCliente().getId());
                psVenta.setInt(2, venta.getEmpleado().getId());
                psVenta.setInt(3, venta.getMesa().getMesaId());
                psVenta.setDate(4, new java.sql.Date(venta.getFecha().getTime()));
                psVenta.setDouble(5, venta.getIgv());
                psVenta.setDouble(6, venta.getSubtotal());
                psVenta.setDouble(7, venta.getDescuento());
                psVenta.setDouble(8, venta.getTotal());
                psVenta.setString(9, venta.getEstadoPago().name());
                psVenta.setInt(10, venta.getCaja_id());
                psVenta.executeUpdate();
                System.out.println("ptmr");
                // Obtener el ID generado
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        venta.setVentaId(rs.getInt(1));
                    }
                    else {
                        throw new SQLException("No se pudo obtener el ID generado de la venta");
                    }
                }
            }
            System.out.println("ptmr1");
            // ----------------- Detalles -----------------
            for (VentaDetalle detalle : venta.getDetalles()) {
                try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle)) {
                    psDetalle.setInt(1, venta.getVentaId());
                    psDetalle.setInt(2, detalle.getPlato().getPlatoId());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setDouble(4, detalle.getPrecioUnitario());
                    psDetalle.setDouble(5, detalle.getSubtotal());
                    psDetalle.setString(6, detalle.getComentario());
                    psDetalle.executeUpdate();
                }
            }
            System.out.println("ptmr1");
            
            // ----------------- Comprobantes -----------------
            for (ComprobantePago comp : venta.getComprobantes()) {
                try (PreparedStatement psComp = conexion.prepareStatement(sqlComprobante)) {
                    psComp.setInt(1, venta.getVentaId());
                    psComp.setString(2, comp.getTipo_comprobante());
                    psComp.setString(3, comp.getSerie_numero());
                    psComp.setInt(4, comp.getMetodo_pago().getId_metodoPago());
                    psComp.setDate(5, new java.sql.Date(comp.getFecha_emision().getTime()));
                    psComp.setString(6, comp.getEstado());
                    System.out.println("owo");
                    psComp.executeUpdate();
                    System.out.println("ptmr wbn");
                }
            }
            conexion.commit();
       
        } catch (SQLException ex) {
            try {
                conexion.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Error al insertar la venta con ID temporal: " + venta.getVentaId(), ex);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<VentaDetalle> obtenerDetallesPorVentaId(int ventaId) throws SQLException {

        List<VentaDetalle> lista = new ArrayList<>();

        String sql = """
            SELECT vd.detalle_id,
                   vd.cantidad,
                   vd.precio_unitario_venta,
                   vd.subtotal,
                   p.nombre_plato
            FROM ventas_detalle vd
            JOIN platos_menu p ON vd.plato_id = p.plato_id
            WHERE vd.venta_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    VentaDetalle d = new VentaDetalle();
                    d.setDetalleId(rs.getInt("detalle_id"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getDouble("precio_unitario_venta"));
                    d.setSubtotal(rs.getDouble("subtotal"));

                    lista.add(d);
                }
            }
        }

        return lista;
    }
    
    private List<ComprobantePago> obtenerComprobantesPorVentaId(int ventaId) {
        List<ComprobantePago> lista = new ArrayList<>();

        String sql = """
            SELECT cp.comprobante_id,
                   cp.tipo_comprobante,
                   cp.serie_numero,
                   cp.id_metodo,
                   cp.fecha_emision,
                   cp.estado,
                   mp.nombre AS metodo_pago_nombre
            FROM comprobantes_pago cp
            JOIN metodos_pago mp ON cp.id_metodo = mp.id_metodo_pago
            WHERE cp.venta_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    ComprobantePago cp = new ComprobantePago();

                    cp.setComprobanteId(rs.getInt("comprobante_id"));
                    cp.setTipo_comprobante(rs.getString("tipo_comprobante"));
                    cp.setSerie_numero(rs.getString("serie_numero"));
                    cp.setFecha_emision(rs.getDate("fecha_emision"));
                    cp.setEstado(rs.getString("estado"));

                    lista.add(cp);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener comprobantes", ex);
        }

        return lista;
    }
   
    
    public void actualizar(Venta venta) {

        String sqlUpdateVenta =
            "UPDATE ventas_cabecera SET cliente_id = ?, empleado_id = ?, mesa_id = ?, " +
            "fecha_hora = ?, igv = ?, subtotal = ?, descuento = ?, total = ?, estado_pago = ?, caja_id = ? " +
            "WHERE venta_id = ?";

        String sqlDeleteDetalle =
            "DELETE FROM ventas_detalle WHERE venta_id = ?";

        String sqlInsertDetalle =
            "INSERT INTO ventas_detalle " +
            "(venta_id, plato_id, cantidad, precio_unitario_venta, subtotal, comentario_cocina) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        String sqlDeleteComprobante =
            "DELETE FROM comprobantes_pago WHERE venta_id = ?";

        String sqlInsertComprobante =
            "INSERT INTO comprobantes_pago " +
            "(venta_id, tipo_comprobante, serie_numero, id_metodo, fecha_emision, estado) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            conexion.setAutoCommit(false);

            // =========================
            // 1. UPDATE CABECERA
            // =========================
            try (PreparedStatement ps = conexion.prepareStatement(sqlUpdateVenta)) {

                ps.setInt(1, venta.getCliente().getId());
                ps.setInt(2, venta.getEmpleado().getId());
                ps.setInt(3, venta.getMesa().getMesaId());

                ps.setTimestamp(4, new java.sql.Timestamp(venta.getFecha().getTime()));

                ps.setDouble(5, venta.getIgv());
                ps.setDouble(6, venta.getSubtotal());
                ps.setDouble(7, venta.getDescuento());
                ps.setDouble(8, venta.getTotal());

                ps.setString(9, venta.getEstadoPago().name());
                ps.setInt(10, venta.getCaja_id());

                ps.setInt(11, venta.getVentaId());

                ps.executeUpdate();
            }

            // =========================
            // 2. DETALLES (REEMPLAZO COMPLETO)
            // =========================

            try (PreparedStatement ps = conexion.prepareStatement(sqlDeleteDetalle)) {
                ps.setInt(1, venta.getVentaId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conexion.prepareStatement(sqlInsertDetalle)) {

                for (VentaDetalle d : venta.getDetalles()) {

                    ps.setInt(1, venta.getVentaId());
                    ps.setInt(2, d.getPlato().getPlatoId());
                    ps.setInt(3, d.getCantidad());
                    ps.setDouble(4, d.getPrecioUnitario());
                    ps.setDouble(5, d.getSubtotal());
                    ps.setString(6, d.getComentario());

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            // =========================
            // 3. COMPROBANTES (REEMPLAZO COMPLETO)
            // =========================

            try (PreparedStatement ps = conexion.prepareStatement(sqlDeleteComprobante)) {
                ps.setInt(1, venta.getVentaId());
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conexion.prepareStatement(sqlInsertComprobante)) {

                for (ComprobantePago c : venta.getComprobantes()) {

                    ps.setInt(1, venta.getVentaId());
                    ps.setString(2, c.getTipo_comprobante());
                    ps.setString(3, c.getSerie_numero());

                    // 🔥 CORRECCIÓN IMPORTANTE SEGÚN TU BD
                    ps.setInt(4, c.getMetodo_pago().getId_metodoPago());

                    ps.setTimestamp(
                        5,
                        new java.sql.Timestamp(c.getFecha_emision().getTime())
                    );

                    ps.setString(6, c.getEstado());

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            conexion.commit();

        } catch (SQLException ex) {

            try {
                conexion.rollback();
            } catch (SQLException e) {
                throw new RuntimeException("Error en rollback de actualización", e);
            }

            throw new RuntimeException(
                "Error al actualizar venta ID: " + venta.getVentaId(),
                ex
            );

        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error al restaurar autocommit", e);
            }
        }
    }
    
    public List<Venta> listar() {
        List<Venta> ventas = new ArrayList<>();

        String sql = """
            SELECT v.venta_id,
                   v.fecha_hora,
                   v.total,
                   v.estado_pago,
                   v.caja_id,
                   c.nombre AS cliente_nombre,
                   e.nombre AS empleado_nombre,
                   m.numero_mesa
            FROM caja_diaria cd
            JOIN ventas_cabecera v ON cd.caja_id = v.caja_id
            JOIN clientes c ON v.cliente_id = c.cliente_id
            JOIN empleados e ON v.empleado_id = e.empleado_id
            JOIN mesas m ON v.mesa_id = m.mesa_id
            ORDER BY v.venta_id
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();

                v.setVentaId(rs.getInt("venta_id"));
                v.setFecha(rs.getDate("fecha_hora"));
                v.setTotal(rs.getDouble("total"));
                v.setCaja_id(rs.getInt("caja_id"));
                v.setEstadoPago(EstadoPago.valueOf(rs.getString("estado_pago")));

                Cliente c = new Cliente();
                c.setNombre(rs.getString("cliente_nombre"));
                v.setCliente(c);

                Empleado e = new Empleado();
                e.setNombre(rs.getString("empleado_nombre"));
                v.setEmpleado(e);

                Mesa m = new Mesa();
                m.setEstado_mesa(rs.getInt("numero_mesa"));
                v.setMesa(m);

                ventas.add(v);
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar ventas", ex);
        }

        return ventas;
    }
    
    public Venta obtenerPorId(int ventaId) {
        Venta venta = null;

        String sql = """
            SELECT v.venta_id,
                   v.fecha_hora,
                   v.igv,
                   v.subtotal,
                   v.descuento,
                   v.total,
                   v.estado_pago,
                   v.cliente_id,
                   v.empleado_id,
                   v.mesa_id,
                   v.caja_id
            FROM ventas_cabecera v
            WHERE v.venta_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, ventaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    venta = new Venta();

                    venta.setVentaId(rs.getInt("venta_id"));
                    venta.setFecha(rs.getTimestamp("fecha_hora"));
                    venta.setIgv(rs.getDouble("igv"));
                    venta.setSubtotal(rs.getDouble("subtotal"));
                    venta.setDescuento(rs.getDouble("descuento"));
                    venta.setTotal(rs.getDouble("total"));
                    venta.setEstadoPago(EstadoPago.valueOf(rs.getString("estado_pago")));
                    venta.setCaja_id(rs.getInt("caja_id"));

                    Cliente c = new Cliente();
                    c.setId(rs.getInt("cliente_id"));
                    venta.setCliente(c);

                    Empleado e = new Empleado();
                    e.setId(rs.getInt("empleado_id"));
                    venta.setEmpleado(e);

                    Mesa m = new Mesa();
                    m.setMesaId(rs.getInt("mesa_id"));
                    venta.setMesa(m);
                }
            }

            if (venta != null) {
                venta.setDetalles(obtenerDetallesPorVentaId(ventaId));
                venta.setComprobantes(obtenerComprobantesPorVentaId(ventaId));
            }

        } catch (SQLException ex) {
            throw new RuntimeException("Error al obtener venta", ex);
        }

        return venta;
    }
    
    public void eliminar(int ventaId) {
        String sqlEliminarDetalles = "DELETE FROM ventas_detalle WHERE venta_id = ?";
        String sqlEliminarComprobantes = "DELETE FROM comprobantes_pago WHERE venta_id = ?";
        String sqlEliminarVenta = "DELETE FROM ventas_cabecera WHERE venta_id = ?";

        try {
            conexion.setAutoCommit(false); // iniciar transacción

            // 1. Eliminar detalles
            try (PreparedStatement psDetalles = conexion.prepareStatement(sqlEliminarDetalles)) {
                psDetalles.setInt(1, ventaId);
                psDetalles.executeUpdate();
            }

            // 2. Eliminar comprobantes asociados
            try (PreparedStatement psComprobantes = conexion.prepareStatement(sqlEliminarComprobantes)) {
                psComprobantes.setInt(1, ventaId);
                psComprobantes.executeUpdate();
            }

            // 3. Eliminar la venta
            try (PreparedStatement psVenta = conexion.prepareStatement(sqlEliminarVenta)) {
                psVenta.setInt(1, ventaId);
                int filas = psVenta.executeUpdate();
                if (filas == 0) {
                    throw new RuntimeException("No se encontró la venta con ID: " + ventaId);
                }
            }

            conexion.commit(); // confirmar transacción
        } catch (SQLException ex) {
            try {
                conexion.rollback(); // revertir cambios si algo falla
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error al hacer rollback tras fallo al eliminar venta con ID: " + ventaId, rollbackEx);
            }
            throw new RuntimeException("Error al eliminar la venta con ID: " + ventaId, ex);
        } finally {
            try {
                conexion.setAutoCommit(true); // restaurar autocommit
            } catch (SQLException ex) {
                throw new RuntimeException("Error al restaurar autocommit", ex);
            }
        }
    }
    
    private Venta mapearVenta(ResultSet rs) throws SQLException {
        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNombre(rs.getString("cliente_nombre"));
        cliente.setApellidos(rs.getString("cliente_apellido"));
        cliente.setTipoCliente(rs.getString("cliente_tipo_cliente"));
        cliente.setDni(rs.getString("cliente_dni"));
        cliente.setTelefono(rs.getString("cliente_telefono"));
        cliente.setDireccion(rs.getString("cliente_direccion"));
        cliente.setFecha_registro(rs.getDate("cliente_fecha_registro"));
        cliente.setPuntosFideldiad(rs.getInt("cliente_puntos_fidelidad"));

        // Crear empleado
        Empleado empleado = new Empleado();
        empleado.setId(rs.getInt("empleado_id"));
        empleado.setNombre(rs.getString("empleado_nombre"));
        empleado.setApellidos(rs.getString("empleado_apellido"));
        empleado.setDni(rs.getString("empleado_dni"));
        empleado.setEstado(EstadoEmpleado.valueOf(rs.getString("empleado_estado")));
        empleado.setTelefono(rs.getString("empleado_telefono"));
        empleado.setEmail(rs.getString("empleado_email"));
        empleado.setFecha_contratación(rs.getDate("empleado_fecha_contratacion"));
        
        // Crear mesa
        Mesa mesa = new Mesa();
        mesa.setMesaId(rs.getInt("mesa_id"));
        mesa.setNumero_mesa(rs.getInt("mesa_numero_mesa"));
        mesa.setCapacidad(rs.getInt("mesa_capacidad"));
        mesa.setEstado_mesa(rs.getInt("mesa_estado_mesa"));
        
        // Crear venta
        Venta venta = new Venta();
        venta.setVentaId(rs.getInt("venta_id"));
        venta.setCliente(cliente);
        venta.setEmpleado(empleado);
        venta.setMesa(mesa);
        venta.setFecha(rs.getDate("venta_fecha_hora"));
        venta.setIgv(rs.getDouble("venta_igv"));  
        venta.setSubtotal(rs.getDouble("venta_subtotal"));
        venta.setDescuento(rs.getDouble("venta_descuento"));
        venta.setTotal(rs.getDouble("venta_total"));
        venta.setEstadoPago(EstadoPago.valueOf(rs.getString("venta_estado_pago")));
        venta.setCaja_id(rs.getInt("caja_id"));
        venta.setDetalles(new ArrayList<>());

        return venta;
    }
    
    private ComprobantePago mapearComprobante(ResultSet rs) throws SQLException {
        ComprobantePago cp = new ComprobantePago();
        cp.setComprobanteId(rs.getInt("comprobante_id"));
        cp.setTipo_comprobante(rs.getString("tipo_comprobante"));
        cp.setSerie_numero(rs.getString("serie_numero"));

        MetodoPago mp = new MetodoPago();
        mp.setId_metodoPago(rs.getInt("id_metodo"));
        mp.setNombre_metodoPago(rs.getString("nombre_metodo_pago"));
        cp.setMetodo_pago(mp);

        cp.setFecha_emision(rs.getDate("fecha_emision"));
        cp.setEstado(rs.getString("estado"));

        return cp;
    }
    
    private VentaDetalle mapear_venta_detalle(ResultSet rs) throws SQLException {
        VentaDetalle detalle = new VentaDetalle();

        // Mapeo del plato
        Plato plato = new Plato();
        plato.setPlatoId(rs.getInt("plato_id"));
        plato.setNombre(rs.getString("plato_nombre_plato"));
        plato.setCategoria(new CategoriaMenu(rs.getInt("categoria_id"),rs.getString("categoria_nombre_categoria")));
        plato.setPrecio(rs.getFloat("plato_precio_venta")); 
        plato.setDisponible(rs.getInt("plato_disponible"));
        detalle.setPlato(plato);

        // Mapeo del detalle de la venta
        detalle.setDetalleId(rs.getInt("detalle_id"));
        detalle.setCantidad(rs.getInt("venta_detalle_cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("venta_detalle_precio_unitario_venta"));
        detalle.setSubtotal(rs.getDouble("venta_detalle_subtotal"));
        detalle.setComentario(rs.getString("venta_detalle_comentario_cocina")); // asegúrate que la columna exista

        return detalle;
    }
}
    