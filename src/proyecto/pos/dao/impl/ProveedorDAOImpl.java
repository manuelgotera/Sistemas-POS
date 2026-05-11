package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.ProveedorDAO;
import proyecto.pos.model.Proveedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAOImpl implements ProveedorDAO {

    private final Connection conexion;

    public ProveedorDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedores (nombre_empresa, ruc,"
                + " contacto_nombre, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?)";
        ejecutarUpdate(sql, 
            proveedor.getNombre_empresa(),
            proveedor.getRuc(),
            proveedor.getContacto_nombre(), 
            proveedor.getTelefono(), 
            proveedor.getEmail(), 
            proveedor.getDireccion()
        );
    }

    @Override
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT * FROM proveedores WHERE proveedor_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearProveedor(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Proveedor> listar() {
        List<Proveedor> lista = new ArrayList<>();
        String sql = "SELECT * FROM proveedores";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearProveedor(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET nombre_empresa=?, ruc = ?, contacto_nombre=?, telefono=?, email=?, direccion=? WHERE proveedor_id=?";
        ejecutarUpdate(sql, 
            proveedor.getNombre_empresa(), 
            proveedor.getRuc(),
            proveedor.getContacto_nombre(), 
            proveedor.getTelefono(), 
            proveedor.getEmail(), 
            proveedor.getDireccion(), 
            proveedor.getProveedorId()
        );
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM proveedores WHERE proveedor_id = ?";
        ejecutarUpdate(sql, id);
    }

    private void ejecutarUpdate(String sql, Object... params) {
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        p.setProveedorId(rs.getInt("proveedor_id"));
        p.setNombre_empresa(rs.getString("nombre_empresa"));
        p.setRuc(rs.getString("ruc"));
        p.setContacto_nombre(rs.getString("contacto_nombre"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setDireccion(rs.getString("direccion"));
        return p;
    }
}