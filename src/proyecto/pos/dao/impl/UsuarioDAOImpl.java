/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;

import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.interfaces.UsuarioDAO;
import java.sql.*;
import proyecto.pos.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Rol;
/**
 *
 * @author HP
 */
public class UsuarioDAOImpl implements UsuarioDAO{
    private Connection conexion;

    public UsuarioDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    // ============================
    // INSERTAR
    // ============================
    @Override
    public void insertar(Usuario usuario) {

        String sql = """
            INSERT INTO usuarios (username, password_hash, estado, empleado_id, rol_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getEstado());
            ps.setInt(4, usuario.getEmpleado().getId());
            ps.setInt(5, usuario.getRol().getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario", e);
        }
    }
    
    @Override
    public Usuario obtenerPorId(int id) {

        String sql = """
            SELECT u.*, e.nombre AS empleado_nombre, r.nombre AS rol_nombre
            FROM usuarios u
            JOIN empleados e ON u.empleado_id = e.empleado_id
            JOIN roles r ON u.rol_id = r.rol_id
            WHERE u.usuario_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener usuario", e);
        }

        return null;
    }
    
    @Override
    public Usuario obtenerPorUsername(String username) {

        String sql = """
            SELECT u.*, e.nombre AS empleado_nombre, r.nombre AS rol_nombre
            FROM usuarios u
            JOIN empleados e ON u.empleado_id = e.empleado_id
            JOIN roles r ON u.rol_id = r.rol_id
            WHERE u.username = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar usuario", e);
        }

        return null;
    }

    @Override
    public List<Usuario> listar() {

        List<Usuario> lista = new ArrayList<>();

        String sql = """
            SELECT u.*, e.nombre AS empleado_nombre, r.nombre AS rol_nombre
            FROM usuarios u
            JOIN empleados e ON u.empleado_id = e.empleado_id
            JOIN roles r ON u.rol_id = r.rol_id
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar usuarios", e);
        }

        return lista;
    }

    // ============================
    // ACTUALIZAR
    // ============================
    @Override
    public void actualizar(Usuario usuario) {

        String sql = """
            UPDATE usuarios
            SET username = ?, password = ?, estado = ?, empleado_id = ?, rol_id = ?
            WHERE usuario_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getEstado());
            ps.setInt(4, usuario.getEmpleado().getId());
            ps.setInt(5, usuario.getRol().getId());
            ps.setInt(6, usuario.getUsuarioId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar usuario", e);
        }
    }

    @Override
    public void eliminar(int id) {

        String sql = "DELETE FROM usuarios WHERE usuario_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar usuario", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {

        Usuario u = new Usuario();

        u.setUsuarioId(rs.getInt("usuario_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setEstado(rs.getString("estado"));

        Empleado e = new Empleado();
        e.setId(rs.getInt("empleado_id"));
        e.setNombre(rs.getString("empleado_nombre"));
        u.setEmpleado(e);

        Rol r = new Rol();
        r.setId(rs.getInt("rol_id"));
        r.setNombre_rol(rs.getString("rol_nombre"));
        u.setRol(r);

        
        return u;
    }
}
