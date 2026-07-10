package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.EmpleadoDAO;
import proyecto.pos.model.Empleado;
import proyecto.pos.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Rol;

public class EmpleadoDAOImpl implements EmpleadoDAO {

    private Connection conexion;
    
    public EmpleadoDAOImpl(Connection conexion){
        this.conexion = conexion;
    }
    
    @Override
    public void insertar(Empleado e) {

        String sql = "INSERT INTO empleados " +
                     "(rol_id, estado, fecha_contratacion, nombre, apellido, dni, telefono, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, e.getRol().getId());

            ps.setString(2, e.getEstado().name());

            ps.setDate(3, new java.sql.Date(e.getFecha_contratación().getTime()));

            ps.setString(4, e.getNombre());
            ps.setString(5, e.getApellidos());
            ps.setString(6, e.getDni());
            ps.setString(7, e.getTelefono());
            ps.setString(8, e.getEmail());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Error al insertar empleado: " + e.getDni(), ex);
        }
    }

    @Override
    public Empleado obtenerPorId(int id) {
        String sql = "SELECT e.*, r.* " +
                     "FROM empleados e " +
                     "JOIN roles r ON e.rol_id = r.rol_id " +
                     "WHERE e.empleado_id = ?";

        Empleado empleado = null;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empleado = mapearEmpleado(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener empleado por ID: " + id, e);
        }

        return empleado;
    }

    @Override
    public Empleado obtenerPorDni(String dni) {
        String sql = "SELECT e.*, r.* " +
                     "FROM empleados e " +
                     "JOIN roles r ON e.rol_id = r.rol_id " +
                     "WHERE e.dni = ?";

        Empleado empleado = null;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    empleado = mapearEmpleado(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener empleado por DNI: " + dni, e);
        }
        return empleado;
    }
    
    @Override
    public List<Empleado> listar() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.*, r.* " +
                     "FROM empleados e " +
                     "JOIN roles r ON e.rol_id = r.rol_id";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearEmpleado(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar empleados", e);
        }
        return lista;
    }

    @Override
    public void actualizar(Empleado e) {

        String sql = "UPDATE empleados SET " +
                     "rol_id = ?, " +
                     "estado = ?, " +
                     "fecha_contratacion = ?, " +
                     "nombre = ?, " +
                     "apellido = ?, " +
                     "dni = ?, " +
                     "telefono = ?, " +
                     "email = ? " +
                     "WHERE empleado_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            // FK Rol
            ps.setInt(1, e.getRol().getId());
            ps.setString(2, e.getEstado().name());
            ps.setDate(3, new java.sql.Date(e.getFecha_contratación().getTime()));
            ps.setString(4, e.getNombre());
            ps.setString(5, e.getApellidos());
            ps.setString(6, e.getDni());
            ps.setString(7, e.getTelefono());
            ps.setString(8, e.getEmail());
            ps.setInt(9, e.getId());

            ps.executeUpdate();

        } catch (SQLException ex) {
            throw new RuntimeException("Error al actualizar empleado con ID: " + e.getId(), ex);
        }
    }

    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {

        Empleado e = new Empleado();

        e.setId(rs.getInt("empleado_id"));
        e.setNombre(rs.getString("nombre"));
        e.setApellidos(rs.getString("apellido"));
        e.setDni(rs.getString("dni"));
        e.setTelefono(rs.getString("telefono"));
        e.setEmail(rs.getString("email"));
        e.setEstado(EstadoEmpleado.valueOf(rs.getString("estado").toUpperCase()));
        e.setFecha_contratación(rs.getDate("fecha_contratacion"));
        Rol rol = new Rol();
        rol.setId(rs.getInt("rol_id"));
        rol.setNombre_rol(rs.getString("nombre_rol"));
        rol.setDescripcion(rs.getString("descripcion"));
        e.setRol(rol);

        return e;
    }

    @Override
    public void actualizarCompleto(Insumo insumo) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminar(int insumoId) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
        
}
