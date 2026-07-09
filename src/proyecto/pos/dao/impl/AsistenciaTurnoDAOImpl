/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.AsistenciaTurnoDAO;
import proyecto.pos.model.AsistenciaTurno;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.Rol;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author HP
 */
public class AsistenciaTurnoDAOImpl implements AsistenciaTurnoDAO {

    private Connection conexion;

    public AsistenciaTurnoDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void registrarEntrada(int empleadoId) {
        String sql = "INSERT INTO asistencia_turnos (empleado_id, fecha, hora_entrada) " +
                     "VALUES (?, TRUNC(SYSDATE), SYSTIMESTAMP)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, new String[]{"ASISTENCIA_ID"})) {
            ps.setInt(1, empleadoId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Error al registrar entrada del empleado: " + empleadoId, ex);
        }
    }

    @Override
    public void registrarSalida(int empleadoId) {
        String sql = "UPDATE asistencia_turnos SET hora_salida = SYSTIMESTAMP " +
                     "WHERE empleado_id = ? AND fecha = TRUNC(SYSDATE) AND hora_salida IS NULL";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, empleadoId);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontro una entrada pendiente hoy para el empleado: " + empleadoId);
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al registrar salida del empleado: " + empleadoId, ex);
        }
    }

    @Override
    public List<AsistenciaTurno> listarPorRangoFecha(LocalDate desde, LocalDate hasta) {
        List<AsistenciaTurno> lista = new ArrayList<>();
        String sql = "SELECT a.asistencia_id, a.empleado_id, a.fecha, a.hora_entrada, a.hora_salida, " +
                     "e.nombre, e.apellido, e.dni, e.estado, e.telefono, e.email, e.fecha_contratacion, " +
                     "e.rol_id, r.nombre_rol, r.descripcion " +
                     "FROM asistencia_turnos a " +
                     "JOIN empleados e ON e.empleado_id = a.empleado_id " +
                     "LEFT JOIN roles r ON r.rol_id = e.rol_id " +
                     "WHERE a.fecha BETWEEN ? AND ? " +
                     "ORDER BY a.fecha DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar asistencia por rango de fecha", ex);
        }
        return lista;
    }

    @Override
    public List<AsistenciaTurno> listarPorEmpleadoYRango(int empleadoId, LocalDate desde, LocalDate hasta) {
        List<AsistenciaTurno> lista = new ArrayList<>();
        String sql = "SELECT a.asistencia_id, a.empleado_id, a.fecha, a.hora_entrada, a.hora_salida, " +
                     "e.nombre, e.apellido, e.dni, e.estado, e.telefono, e.email, e.fecha_contratacion, " +
                     "e.rol_id, r.nombre_rol, r.descripcion " +
                     "FROM asistencia_turnos a " +
                     "JOIN empleados e ON e.empleado_id = a.empleado_id " +
                     "LEFT JOIN roles r ON r.rol_id = e.rol_id " +
                     "WHERE a.empleado_id = ? AND a.fecha BETWEEN ? AND ? " +
                     "ORDER BY a.fecha DESC";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, empleadoId);
            ps.setDate(2, Date.valueOf(desde));
            ps.setDate(3, Date.valueOf(hasta));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar asistencia del empleado: " + empleadoId, ex);
        }
        return lista;
    }

    /**
     * Identifica tardanzas e inasistencias en un rango de fechas SIN necesidad
     * de una columna ESTADO en la tabla: genera el calendario de dias del rango
     * con CONNECT BY, cruza con empleados y hace LEFT JOIN a asistencia_turnos.
     * Si no hay hora_entrada y la fecha ya paso -> falta el registro (inasistencia).
     * Si hay hora_entrada despues de las 08:15 -> tardanza.
     */
    @Override
    public List<AsistenciaTurno> listarTardanzasEInasistencias(LocalDate desde, LocalDate hasta) {
        List<AsistenciaTurno> lista = new ArrayList<>();
        String sql =
            "SELECT e.empleado_id, e.nombre, e.apellido, e.dni, e.estado, e.telefono, e.email, " +
            "       e.fecha_contratacion, e.rol_id, r.nombre_rol, r.descripcion, " +
            "       cal.dia AS fecha, a.asistencia_id, a.hora_entrada, a.hora_salida " +
            "FROM empleados e " +
            "CROSS JOIN ( " +
            "    SELECT TRUNC(?) + LEVEL - 1 AS dia " +
            "    FROM dual " +
            "    CONNECT BY LEVEL <= (TRUNC(?) - TRUNC(?) + 1) " +
            ") cal " +
            "LEFT JOIN asistencia_turnos a ON a.empleado_id = e.empleado_id AND a.fecha = cal.dia " +
            "WHERE cal.dia <= TRUNC(SYSDATE) " +
            "AND (a.hora_entrada IS NULL OR a.hora_entrada > cal.dia + INTERVAL '8:15' HOUR TO MINUTE) " +
            "ORDER BY cal.dia DESC, e.nombre";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            ps.setDate(3, Date.valueOf(desde));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsistenciaTurno at = new AsistenciaTurno();
                    at.setAsistenciaId(rs.getInt("asistencia_id")); // 0 si no hubo registro (inasistencia)
                    at.setEmpleado(mapearEmpleado(rs));
                    at.setFecha(rs.getDate("fecha").toLocalDate());
                    Timestamp entrada = rs.getTimestamp("hora_entrada");
                    Timestamp salida = rs.getTimestamp("hora_salida");
                    if (entrada != null) at.setEntrada(entrada.toLocalDateTime());
                    if (salida != null) at.setSalida(salida.toLocalDateTime());
                    lista.add(at);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Error al listar tardanzas e inasistencias", ex);
        }
        return lista;
    }

    private AsistenciaTurno mapearAsistencia(ResultSet rs) throws SQLException {
        AsistenciaTurno at = new AsistenciaTurno();
        at.setAsistenciaId(rs.getInt("asistencia_id"));
        at.setEmpleado(mapearEmpleado(rs));
        at.setFecha(rs.getDate("fecha").toLocalDate());

        Timestamp entrada = rs.getTimestamp("hora_entrada");
        Timestamp salida = rs.getTimestamp("hora_salida");
        if (entrada != null) at.setEntrada(entrada.toLocalDateTime());
        if (salida != null) at.setSalida(salida.toLocalDateTime());

        return at;
    }

    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setId(rs.getInt("empleado_id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellidos(rs.getString("apellido"));
        e.setDni(rs.getString("dni"));
        e.setTelefono(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));

        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            e.setEstado(EstadoEmpleado.valueOf(estadoStr.toUpperCase()));
        }
        e.setFecha_contratación(rs.getDate("fecha_contratacion"));

        Rol rol = new Rol();
        rol.setId(rs.getInt("rol_id"));
        rol.setNombre_rol(rs.getString("nombre_rol"));
        rol.setDescripcion(rs.getString("descripcion"));
        e.setRol(rol);

        return e;
    }
}
