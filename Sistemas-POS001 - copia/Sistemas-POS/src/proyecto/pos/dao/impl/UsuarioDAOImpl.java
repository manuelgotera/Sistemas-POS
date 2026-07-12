/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.UsuarioDAO;
import java.sql.*;
import proyecto.pos.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Rol;

public class UsuarioDAOImpl implements UsuarioDAO {
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
            ps.setString(2, usuario.getPassword()); // Obtiene el texto plano o hash del modelo
            ps.setString(3, usuario.getEstado());
            ps.setInt(4, usuario.getEmpleado().getId());
            ps.setInt(5, usuario.getRol().getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar usuario", e);
        }
    }
    
    // ============================
    // OBTENER POR ID
    // ============================
    @Override
    public Usuario obtenerPorId(int id) {
        // Se añade alias 'u.password_hash AS password' para que coincida con el mapeador
        String sql = """
            SELECT u.*, u.password_hash AS password, e.nombre AS empleado_nombre, r.nombre AS rol_nombre
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
    
    // ============================
    // OBTENER POR USERNAME
    // ============================
    @Override
    public Usuario obtenerPorUsername(String username) {
        String sql = """
            SELECT u.*, u.password_hash AS password, e.nombre AS empleado_nombre, r.nombre_rol AS rol_nombre
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

    // ============================
    // LISTAR
    // ============================
    @Override
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
            SELECT u.*, u.password_hash AS password, e.nombre AS empleado_nombre, r.nombre AS rol_nombre
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
        // Corregido de 'password' a 'password_hash' para respetar la base de datos
        String sql = """
            UPDATE usuarios
            SET username = ?, password_hash = ?, estado = ?, empleado_id = ?, rol_id = ?
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

    // ============================
    // ELIMINAR
    // ============================
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

    // ============================
    // MAPEADOR
    // ============================
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();

        u.setUsuarioId(rs.getInt("usuario_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password")); // Lee el alias provisto en los SELECTs
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
    
     // ============================
    // LISTAR ROLES
    // ============================
    public List<Rol> listarRoles() {

        List<Rol> lista = new ArrayList<>();

        String sql = "SELECT rol_id, nombre_rol, descripcion FROM roles";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearRol(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al listar roles", e);
        }

        return lista;
    }
    
    public boolean actualizarPassword(int usuarioId, String nuevaPassword) {
        String sql = "UPDATE usuarios SET password_hash = ? WHERE usuario_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nuevaPassword);
            ps.setInt(2, usuarioId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la contraseña en la base de datos", e);
        }
    }

        // ============================
    // MAPEADOR ROL
    // ============================
    private Rol mapearRol(ResultSet rs) throws SQLException {

        Rol r = new Rol();
        
        r.setId(rs.getInt("rol_id"));
        r.setNombre_rol(rs.getString("nombre_rol"));
        r.setDescripcion(rs.getString("descripcion"));
        System.out.println(r.toString());
        return r;
    }
}