/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.dao.interfaces.CajaDAO;
import proyecto.pos.model.Caja;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.Rol;


public class CajaDAOImpl implements CajaDAO {

    private Connection conexion;

    public CajaDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Caja caja) {

    String sql = "INSERT INTO caja_diaria (fecha_apertura, empleado_id, monto_inicial, estado_caja) " +
                 "VALUES (?, ?, ?, ?)";

    try (PreparedStatement ps = conexion.prepareStatement(sql)) {

        ps.setDate(1, new java.sql.Date(caja.getFecha_apertura().getTime()));

        ps.setInt(2, caja.getEmpleado().getId()); // 👈 importante

        ps.setDouble(3, caja.getMonto_inicial());

        ps.setString(4, caja.getEstado());

        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Error al insertar caja", e);
    }
}

    @Override
    public Caja obtenerPorId(int id) {

        String sql = """
                SELECT c.caja_id,
                       c.fecha_apertura,
                       c.fecha_cierre,
                       c.monto_inicial,
                       c.monto_final_arqueo,
                       c.estado_caja,
                       c.diferencia,

                       e.empleado_id,
                       e.nombre AS empleado_nombre,
                       e.apellido AS empleado_apellido,
                       e.dni AS empleado_dni,
                       e.estado AS empleado_estado,
                       e.telefono AS empleado_telefono,
                       e.email AS empleado_email,
                       e.fecha_contratacion AS empleado_fecha_contratacion,
                       e.rol_id,

                       r.nombre_rol,
                       r.descripcion

                FROM caja_diaria c
                JOIN empleados e ON c.empleado_id = e.empleado_id
                JOIN roles r ON e.rol_id = r.rol_id
                WHERE c.caja_id = ?
            """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCaja(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener caja ID: " + id, e);
        }

        return null;
    }
    
    @Override
    public Caja obtenerCajaAbierta() {

        String sql = """
        SELECT c.caja_id,
            c.fecha_apertura,
            c.fecha_cierre,
            c.monto_inicial,
            c.monto_final_arqueo,
            c.estado_caja,
            c.diferencia,
        
            e.empleado_id,
            e.nombre AS empleado_nombre,
            e.apellido AS empleado_apellido,
            e.dni AS empleado_dni,
            e.estado AS empleado_estado,
            e.telefono AS empleado_telefono,
            e.email AS empleado_email,
            e.fecha_contratacion AS empleado_fecha_contratacion,
            e.rol_id,
        
            r.nombre_rol,
            r.descripcion

            FROM caja_diaria c
            JOIN empleados e ON c.empleado_id = e.empleado_id
            JOIN roles r ON e.rol_id = r.rol_id
            WHERE c.estado_caja = 'ABIERTA'
            AND ROWNUM = 1
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapearCaja(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener caja abierta", e);
        }

        return null;
    }

    @Override
    public List<Caja> listar() {

        List<Caja> lista = new ArrayList<>();

        String sql = """
        SELECT c.caja_id,
            c.fecha_apertura,
            c.fecha_cierre,
            c.monto_inicial,
            c.monto_final_arqueo,
            c.estado_caja,
            c.diferencia,
        
            e.empleado_id,
            e.nombre AS empleado_nombre,
            e.apellido AS empleado_apellido,
            e.dni AS empleado_dni,
            e.estado AS empleado_estado,
            e.telefono AS empleado_telefono,
            e.email AS empleado_email,
            e.fecha_contratacion AS empleado_fecha_contratacion,
            e.rol_id,
        
            r.nombre_rol,
            r.descripcion

            FROM caja_diaria c
            JOIN empleados e ON c.empleado_id = e.empleado_id
            JOIN roles r ON e.rol_id = r.rol_id
            ORDER BY c.caja_id DESC
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCaja(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cajas", e);
        }

        return lista;
    }

    @Override
    public void actualizar(Caja caja) {

        String sql = """
            UPDATE caja_diaria
            SET fecha_cierre = ?,
                monto_final_arqueo = ?,
                estado_caja = ?,
                diferencia = ?
            WHERE caja_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            if (caja.getFecha_cierre() != null) {
                ps.setTimestamp(1, new java.sql.Timestamp(caja.getFecha_cierre().getTime()));
            } else {
                ps.setNull(1, java.sql.Types.TIMESTAMP);
            }

            ps.setDouble(2, caja.getMonto_final());

            ps.setString(3, caja.getEstado());

            ps.setDouble(4, caja.getDiferencia());

            ps.setInt(5, caja.getCajaId());

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new RuntimeException("No existe caja con ID: " + caja.getCajaId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar caja ID: " + caja.getCajaId(), e);
        }
    }

    /*@Override
    public void eliminar(int id) {
        String sql = "DELETE FROM caja WHERE caja_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar caja con ID: " + id, e);
        }
    }*/

    public Caja mapearCaja(ResultSet rs) throws SQLException {

        Caja c = new Caja();

        c.setCajaId(rs.getInt("caja_id"));
        c.setFecha_apertura(rs.getTimestamp("fecha_apertura"));
        c.setFecha_cierre(rs.getTimestamp("fecha_cierre"));

        c.setMonto_inicial(rs.getDouble("monto_inicial"));
        if (rs.wasNull()) c.setMonto_inicial(0);

        c.setMonto_final(rs.getDouble("monto_final_arqueo"));
        if (rs.wasNull()) c.setMonto_final(0);

        c.setEstado(rs.getString("estado_caja"));

        c.setDiferencia(rs.getDouble("diferencia"));
        if (rs.wasNull()) c.setDiferencia(0);

        // ---------------- EMPLEADO ----------------
        Empleado e = new Empleado();

        e.setId(rs.getInt("empleado_id"));
        e.setNombre(rs.getString("empleado_nombre"));
        e.setApellidos(rs.getString("empleado_apellido"));
        e.setDni(rs.getString("empleado_dni"));
        e.setTelefono(rs.getString("empleado_telefono"));
        e.setEmail(rs.getString("empleado_email"));
        e.setFecha_contratación(rs.getDate("empleado_fecha_contratacion"));
        System.out.println(rs.getInt("empleado_id"));
        System.out.println(rs.getString("empleado_estado"));
        // ENUM seguro
        e.setEstado(EstadoEmpleado.valueOf(rs.getString("empleado_estado").toUpperCase()));

        // ---------------- ROL ----------------
        Rol r = new Rol();
        r.setId(rs.getInt("rol_id"));
        r.setNombre_rol(rs.getString("nombre_rol"));
        r.setDescripcion(rs.getString("descripcion"));

        e.setRol(r);

        // asignar empleado a caja
        c.setEmpleado(e);

        return c;
    }
}