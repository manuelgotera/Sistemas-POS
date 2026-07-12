package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.VentaDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Cliente;
import proyecto.pos.model.ComprobantePago;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.EstadoPago;
import proyecto.pos.model.Mesa;
import proyecto.pos.model.MetodoPago;
import proyecto.pos.model.Plato;
import java.util.Date;

public class VentaDAOImpl implements VentaDAO {

    private Connection conexion;
    
    public VentaDAOImpl(Connection conexion){
        this.conexion = conexion;
    }
    
    @Override
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

        PreparedStatement psVenta = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psComp = null;
        ResultSet rs = null;

        try {
            conexion.setAutoCommit(false);
            
            // ----------------- Venta (Cabecera) -----------------
            psVenta = conexion.prepareStatement(sqlVenta, new String[]{"VENTA_ID"});
            
            // Validaciones para evitar NullPointer si es Público General
            if (venta.getCliente() != null && venta.getCliente().getId() > 0) {
                psVenta.setInt(1, venta.getCliente().getId());
            } else {
                psVenta.setNull(1, Types.INTEGER);
            }

            psVenta.setInt(2, venta.getEmpleado().getId());
            
            if (venta.getMesa() != null && venta.getMesa().getMesaId() > 0) {
                psVenta.setInt(3, venta.getMesa().getMesaId());
            } else {
                psVenta.setNull(3, Types.INTEGER);
            }

            // CORRECCIÓN: Usar Timestamp para guardar Fecha y Hora exacta en Oracle
            psVenta.setTimestamp(4, new java.sql.Timestamp(venta.getFecha().getTime()));
            psVenta.setDouble(5, venta.getIgv());
            psVenta.setDouble(6, venta.getSubtotal());
            psVenta.setDouble(7, venta.getDescuento());
            psVenta.setDouble(8, venta.getTotal());
            psVenta.setString(9, venta.getEstadoPago().name());
            psVenta.setInt(10, venta.getCaja_id());
            
            psVenta.executeUpdate();
            
            // Obtener el ID generado
            rs = psVenta.getGeneratedKeys();
            if (rs.next()) {
                venta.setVentaId(rs.getInt(1));
            } else {
                throw new SQLException("No se pudo obtener el ID generado de la venta");
            }
            
            // ----------------- Detalles (Platos) -----------------
            psDetalle = conexion.prepareStatement(sqlDetalle);
            for (VentaDetalle detalle : venta.getDetalles()) {
                psDetalle.setInt(1, venta.getVentaId());
                psDetalle.setInt(2, detalle.getPlato().getPlatoId());
                psDetalle.setInt(3, detalle.getCantidad());
                psDetalle.setDouble(4, detalle.getPrecioUnitario());
                psDetalle.setDouble(5, detalle.getSubtotal());
                psDetalle.setString(6, detalle.getComentario() != null ? detalle.getComentario() : "");
                psDetalle.addBatch();
            }
            psDetalle.executeBatch();
            
            // ----------------- Comprobantes -----------------
            psComp = conexion.prepareStatement(sqlComprobante);
            for (ComprobantePago comp : venta.getComprobantes()) {
                psComp.setInt(1, venta.getVentaId());
                psComp.setString(2, comp.getTipo_comprobante());
                psComp.setString(3, comp.getSerie_numero());
                psComp.setInt(4, comp.getMetodo_pago().getId_metodoPago());
                psComp.setTimestamp(5, new java.sql.Timestamp(comp.getFecha_emision().getTime()));
                psComp.setString(6, comp.getEstado());
                psComp.addBatch();
            }
            psComp.executeBatch();
            
            // Si todo fue perfecto, guardamos definitivamente
            conexion.commit();
       
        } catch (SQLException ex) {
            System.err.println("❌ ERROR SQL AL INSERTAR VENTA: " + ex.getMessage());
            ex.printStackTrace();
            try {
                if(conexion != null) conexion.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Error en BD al guardar venta: " + ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) rs.close();
                if (psVenta != null) psVenta.close();
                if (psDetalle != null) psDetalle.close();
                if (psComp != null) psComp.close();
                if (conexion != null) conexion.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<VentaDetalle> obtenerDetalles(int id) throws SQLException {
            List<VentaDetalle> lista = new ArrayList<>();
            // Consultamos el detalle y hacemos un JOIN con platos_menu para traer el nombre
            String sql = "SELECT vd.*, p.nombre_plato FROM ventas_detalle vd " +
                         "JOIN platos_menu p ON vd.plato_id = p.plato_id " +
                         "WHERE vd.venta_id = ?";

            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // 1. Creamos el objeto Plato y le seteamos el nombre que viene de la BD
                        Plato p = new Plato();
                        p.setNombre(rs.getString("nombre_plato"));
                        p.setPlatoId(rs.getInt("plato_id"));

                        // 2. Creamos el detalle de la venta
                        VentaDetalle d = new VentaDetalle();
                        d.setPlato(p); // ¡ESTA ES LA LÍNEA QUE FALTABA! Vinculamos el plato al detalle
                        d.setCantidad(rs.getInt("cantidad"));
                        d.setPrecioUnitario(rs.getDouble("precio_unitario_venta"));
                        d.setSubtotal(rs.getDouble("subtotal"));
                        d.setComentario(rs.getString("comentario_cocina"));

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
   
    @Override
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
                    ps.setInt(4, c.getMetodo_pago().getId_metodoPago());
                    ps.setTimestamp(5, new java.sql.Timestamp(c.getFecha_emision().getTime()));
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
            throw new RuntimeException("Error al actualizar venta ID: " + venta.getVentaId(), ex);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error al restaurar autocommit", e);
            }
        }
    }
    
    @Override
    public List<Venta> listar() {
        List<Venta> ventas = new ArrayList<>();

        String sql = """
            SELECT v.venta_id,
                   v.fecha_hora,
                   v.subtotal,
                   v.igv,
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
            LEFT JOIN mesas m ON v.mesa_id = m.mesa_id
            ORDER BY v.venta_id
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Venta v = new Venta();
                v.setVentaId(rs.getInt("venta_id"));
                v.setFecha(rs.getTimestamp("fecha_hora"));
                v.setSubtotal(rs.getDouble("subtotal"));
                v.setIgv(rs.getDouble("igv"));
                v.setTotal(rs.getDouble("total"));
                v.setCaja_id(rs.getInt("caja_id"));
                v.setEstadoPago(EstadoPago.valueOf(rs.getString("estado_pago").trim().toUpperCase()));
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
    
    @Override
    public List<Venta> listarPorRangoFecha(java.util.Date inicio, java.util.Date fin) {
        List<Venta> ventas = new ArrayList<>();

        String sql = """
            SELECT v.venta_id,
                   v.fecha_hora,
                   v.subtotal,
                   v.igv,
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
            LEFT JOIN mesas m ON v.mesa_id = m.mesa_id
            WHERE v.fecha_hora BETWEEN ? AND ?
            ORDER BY v.venta_id
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(fin.getTime()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Venta v = new Venta();
                    v.setVentaId(rs.getInt("venta_id"));
                    v.setFecha(rs.getTimestamp("fecha_hora"));
                    v.setSubtotal(rs.getDouble("subtotal"));
                    v.setIgv(rs.getDouble("igv"));
                    v.setTotal(rs.getDouble("total"));
                    v.setCaja_id(rs.getInt("caja_id"));
                    v.setEstadoPago(EstadoPago.valueOf(rs.getString("estado_pago").trim().toUpperCase()));
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
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar ventas", ex);
        }

        return ventas;
    }
    
       @Override
    public Venta obtenerPorId(int id) {
        Venta v = null;
        String sql = "SELECT * FROM ventas_cabecera WHERE venta_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    v = new Venta();
                    v.setVentaId(rs.getInt("venta_id"));
                    v.setFecha(rs.getTimestamp("fecha_hora"));
                    v.setSubtotal(rs.getDouble("subtotal"));
                    v.setIgv(rs.getDouble("igv"));
                    v.setTotal(rs.getDouble("total"));
                    v.setDescuento(rs.getDouble("descuento"));
                    v.setCaja_id(rs.getInt("caja_id"));

                    // --- CARGA DE MESA SEGURA ---
                    int idMesa = rs.getInt("mesa_id");
                    Mesa m = new Mesa();
                    m.setMesaId(idMesa);
                    m.setEstado_mesa(idMesa); // Guardamos el número
                    v.setMesa(m); // IMPORTANTE: Siempre seteamos el objeto, nunca queda null

                    // Datos básicos para evitar otros NPE
                    Empleado e = new Empleado(); e.setNombre("Cajero Principal"); v.setEmpleado(e);
                    Cliente c = new Cliente(); c.setNombre("Público General"); v.setCliente(c);

                    v.setDetalles(obtenerDetalles(id));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAO: " + e.getMessage());
        }
        return v;
    }


    public List<ComprobantePago> obtenerComprobantesPorFecha(Date inicio, Date fin) {
        List<ComprobantePago> lista = new ArrayList<>();

        String sql = """
            SELECT cp.comprobante_id,
                   cp.tipo_comprobante,
                   cp.serie_numero,
                   cp.id_metodo,
                   cp.fecha_emision,
                   cp.estado,
                   cp.venta_id,
                   mp.nombre AS metodo_pago_nombre
            FROM comprobantes_pago cp
            JOIN metodos_pago mp ON cp.id_metodo = mp.id_metodo_pago
            WHERE cp.fecha_emision BETWEEN ? AND ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setTimestamp(1, new java.sql.Timestamp(inicio.getTime()));
            ps.setTimestamp(2, new java.sql.Timestamp(fin.getTime()));

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
            throw new RuntimeException("Error al obtener comprobantes por fecha", ex);
        }

        return lista;
    }
    
    @Override
    public void eliminar(int ventaId) {
        String sqlEliminarDetalles = "DELETE FROM ventas_detalle WHERE venta_id = ?";
        String sqlEliminarComprobantes = "DELETE FROM comprobantes_pago WHERE venta_id = ?";
        String sqlEliminarVenta = "DELETE FROM ventas_cabecera WHERE venta_id = ?";

        try {
            conexion.setAutoCommit(false); 

            try (PreparedStatement psDetalles = conexion.prepareStatement(sqlEliminarDetalles)) {
                psDetalles.setInt(1, ventaId);
                psDetalles.executeUpdate();
            }

            try (PreparedStatement psComprobantes = conexion.prepareStatement(sqlEliminarComprobantes)) {
                psComprobantes.setInt(1, ventaId);
                psComprobantes.executeUpdate();
            }

            try (PreparedStatement psVenta = conexion.prepareStatement(sqlEliminarVenta)) {
                psVenta.setInt(1, ventaId);
                int filas = psVenta.executeUpdate();
                if (filas == 0) {
                    throw new RuntimeException("No se encontró la venta con ID: " + ventaId);
                }
            }

            conexion.commit(); 
        } catch (SQLException ex) {
            try {
                conexion.rollback(); 
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error al hacer rollback tras fallo al eliminar venta con ID: " + ventaId, rollbackEx);
            }
            throw new RuntimeException("Error al eliminar la venta con ID: " + ventaId, ex);
        } finally {
            try {
                conexion.setAutoCommit(true); 
            } catch (SQLException ex) {
                throw new RuntimeException("Error al restaurar autocommit", ex);
            }
        }
    }
    
    private Venta mapearVenta(ResultSet rs) throws SQLException {
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

        Empleado empleado = new Empleado();
        empleado.setId(rs.getInt("empleado_id"));
        empleado.setNombre(rs.getString("empleado_nombre"));
        empleado.setApellidos(rs.getString("empleado_apellido"));
        empleado.setDni(rs.getString("empleado_dni"));
        empleado.setEstado(EstadoEmpleado.valueOf(rs.getString("empleado_estado")));
        empleado.setTelefono(rs.getString("empleado_telefono"));
        empleado.setEmail(rs.getString("empleado_email"));
        empleado.setFecha_contratación(rs.getDate("empleado_fecha_contratacion"));
        
        Mesa mesa = new Mesa();
        mesa.setMesaId(rs.getInt("mesa_id"));
        mesa.setNumero_mesa(rs.getInt("mesa_numero_mesa"));
        mesa.setCapacidad(rs.getInt("mesa_capacidad"));
        mesa.setEstado_mesa(rs.getInt("mesa_estado_mesa"));
        
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
        venta.setEstadoPago(EstadoPago.valueOf(rs.getString("venta_estado_pago").trim().toUpperCase()));       
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
    
    // =========================================================
    // HU-06: Anulación de venta liquidada con transacción
    // Criterio 1: Validación de estado de factura no anulada
    // Criterio 2: Transacción de reversión de inventario (insumos via recetas)
    // Criterio 3: Actualización de estado de factura a "ANULADO"
    // Criterio 4: Confirmación de registro de auditoría en el sistema
    // =========================================================
    @Override
    public List<String> anularVenta(int ventaId, int empleadoId) {
        List<String> log = new ArrayList<>();

        String sqlEstado   = "SELECT estado_pago, total FROM ventas_cabecera WHERE venta_id = ?";
        String sqlDetalles = "SELECT vd.plato_id, vd.cantidad, p.nombre_plato "
                           + "FROM ventas_detalle vd "
                           + "JOIN platos_menu p ON vd.plato_id = p.plato_id "
                           + "WHERE vd.venta_id = ?";
        String sqlReceta   = "SELECT re.insumo_id, re.cantidad_requerida, i.nombre_insumo "
                           + "FROM recetas_escandallo re "
                           + "JOIN insumos i ON re.insumo_id = i.insumo_id "
                           + "WHERE re.plato_id = ?";
        String sqlStock    = "UPDATE insumos SET stock = stock + ? WHERE insumo_id = ?";
        String sqlAnular   = "UPDATE ventas_cabecera SET estado_pago = 'ANULADO' WHERE venta_id = ?";
        // Criterio 4: auditoría se registra en interfaz — no requiere tabla BD adicional

        try {
            conexion.setAutoCommit(false);

            // ── Criterio 1: Validar que la venta exista y sea PAGADO ──────────
            String estadoActual;
            double totalVenta;
            try (PreparedStatement ps = conexion.prepareStatement(sqlEstado)) {
                ps.setInt(1, ventaId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        log.add("ERROR|Venta ID " + ventaId + " no encontrada en BD.");
                        conexion.rollback();
                        conexion.setAutoCommit(true);
                        return log;
                    }
                    estadoActual = rs.getString("estado_pago").trim().toUpperCase();
                    totalVenta   = rs.getDouble("total");
                }
            }

            if ("ANULADO".equals(estadoActual)) {
                log.add("ERROR|La venta ID " + ventaId + " ya está ANULADA. No se puede anular dos veces.");
                conexion.setAutoCommit(true);
                return log;
            }
            if (!"PAGADO".equals(estadoActual)) {
                log.add("ERROR|La venta ID " + ventaId + " tiene estado '" + estadoActual
                        + "'. Solo se pueden anular ventas PAGADAS.");
                conexion.setAutoCommit(true);
                return log;
            }
            log.add("OK|Criterio 1 ✔ — Venta ID " + ventaId + " estado '" + estadoActual
                    + "' válido para anulación. Total: S/. " + String.format("%.2f", totalVenta));

            // ── Criterio 2: Reversión de inventario (insumos via receta) ──────
            log.add("INFO|Criterio 2 — Iniciando reversión de inventario...");
            int itemsRevertidos = 0;

            try (PreparedStatement psD = conexion.prepareStatement(sqlDetalles)) {
                psD.setInt(1, ventaId);
                try (ResultSet rsD = psD.executeQuery()) {
                    while (rsD.next()) {
                        int    platoId    = rsD.getInt("plato_id");
                        int    cantPlato  = rsD.getInt("cantidad");
                        String nomPlato   = rsD.getString("nombre_plato");

                        // Por cada plato, revertir sus insumos según receta
                        try (PreparedStatement psR = conexion.prepareStatement(sqlReceta)) {
                            psR.setInt(1, platoId);
                            try (ResultSet rsR = psR.executeQuery()) {
                                boolean tieneReceta = false;
                                while (rsR.next()) {
                                    tieneReceta = true;
                                    int    insumoId  = rsR.getInt("insumo_id");
                                    double cantNec   = rsR.getDouble("cantidad_requerida");
                                    String nomInsumo = rsR.getString("nombre_insumo");
                                    double cantDevolver = cantNec * cantPlato;

                                    try (PreparedStatement psS = conexion.prepareStatement(sqlStock)) {
                                        psS.setDouble(1, cantDevolver);
                                        psS.setInt(2, insumoId);
                                        psS.executeUpdate();
                                    }
                                    log.add("OK|  → '" + nomPlato + "' (x" + cantPlato + "): reintegrado "
                                            + String.format("%.2f", cantDevolver)
                                            + " de '" + nomInsumo + "' al inventario.");
                                    itemsRevertidos++;
                                }
                                if (!tieneReceta) {
                                    log.add("INFO|  → '" + nomPlato + "' sin receta registrada. Omitido en reversión.");
                                }
                            }
                        }
                    }
                }
            }
            log.add("OK|Criterio 2 ✔ — " + itemsRevertidos + " insumo(s) reintegrados al inventario.");


            // ── Ajuste de saldo de caja (razón/resultado HU-06) ──────────────
            String sqlCaja = "UPDATE caja_diaria SET total_ventas = total_ventas - ? "
                           + "WHERE caja_id = (SELECT caja_id FROM ventas_cabecera WHERE venta_id = ?)";
            try (PreparedStatement psCaja = conexion.prepareStatement(sqlCaja)) {
                psCaja.setDouble(1, totalVenta);
                psCaja.setInt(2, ventaId);
                int filasAfectadas = psCaja.executeUpdate();
                if (filasAfectadas > 0) {
                    log.add("OK|Caja ✔ — Saldo de caja ajustado: -S/. "
                            + String.format("%.2f", totalVenta)
                            + " descontado de caja_diaria.");
                } else {
                    log.add("INFO|Caja — No se encontró caja activa para ajustar (venta sin caja).");
                }
            } catch (SQLException eCaja) {
                log.add("INFO|Caja — No se pudo ajustar saldo: " + eCaja.getMessage());
            }

            // ── Criterio 3: Actualizar estado a ANULADO ───────────────────────
            try (PreparedStatement psA = conexion.prepareStatement(sqlAnular)) {
                psA.setInt(1, ventaId);
                psA.executeUpdate();
            }
            log.add("OK|Criterio 3 ✔ — Estado de venta ID " + ventaId
                    + " actualizado: '" + estadoActual + "' → 'ANULADO'.");

            // ── Criterio 4: Confirmación de registro de auditoría en el sistema ──
            // El registro de auditoría se muestra en la interfaz (sin tabla BD adicional)
            java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
            String idEvento = "AUD-" + ahora.getYear()
                    + String.format("%02d", ahora.getMonthValue())
                    + String.format("%02d", ahora.getDayOfMonth())
                    + "-" + String.format("%04d", ventaId);
            log.add("AUDIT|ID_EVENTO=" + idEvento);
            log.add("AUDIT|VENTA_ID=" + ventaId);
            log.add("AUDIT|FECHA=" + String.format("%02d/%02d/%04d %02d:%02d:%02d",
                    ahora.getDayOfMonth(), ahora.getMonthValue(), ahora.getYear(),
                    ahora.getHour(), ahora.getMinute(), ahora.getSecond()));
            log.add("AUDIT|EMPLEADO_ID=" + empleadoId);
            log.add("AUDIT|MONTO=" + String.format("%.2f", totalVenta));
            log.add("AUDIT|INSUMOS=" + itemsRevertidos);
            log.add("AUDIT|ESTADO_ANTERIOR=" + estadoActual);
            log.add("OK|Criterio 4 ✔ — Registro de auditoría confirmado en el sistema (visualizado en interfaz).");

            conexion.commit();
            log.add("COMMIT|Anulación completada exitosamente. Venta ID " + ventaId
                    + " anulada. Total revertido: S/. " + String.format("%.2f", totalVenta));

        } catch (SQLException e) {
            log.add("ERROR|Error de BD: " + e.getMessage());
            try {
                conexion.rollback();
                log.add("ROLLBACK|Rollback automático ejecutado. Ningún cambio fue aplicado.");
            } catch (SQLException ex) {
                log.add("ERROR|Fallo crítico en rollback: " + ex.getMessage());
            }
        } finally {
            try { conexion.setAutoCommit(true); } catch (SQLException ignored) {}
        }
        return log;
    }

    private VentaDetalle mapear_venta_detalle(ResultSet rs) throws SQLException {
        VentaDetalle detalle = new VentaDetalle();

        Plato plato = new Plato();
        plato.setPlatoId(rs.getInt("plato_id"));
        plato.setNombre(rs.getString("plato_nombre_plato"));
        plato.setCategoria(new CategoriaMenu(rs.getInt("categoria_id"),rs.getString("categoria_nombre_categoria")));
        plato.setPrecio(rs.getFloat("plato_precio_venta")); 
        plato.setDisponible(rs.getInt("plato_disponible"));
        detalle.setPlato(plato);

        detalle.setDetalleId(rs.getInt("detalle_id"));
        detalle.setCantidad(rs.getInt("venta_detalle_cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("venta_detalle_precio_unitario_venta"));
        detalle.setSubtotal(rs.getDouble("venta_detalle_subtotal"));
        detalle.setComentario(rs.getString("venta_detalle_comentario_cocina")); 

        return detalle;
    }
}