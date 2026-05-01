package proyecto.pos.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.dao.interfaces.InsumoDAO;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;

public class InsumoDAOImpl implements InsumoDAO {

    private Connection conexion;

    public InsumoDAOImpl(){
        
    }
    
    public InsumoDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Insumo insumo) {
        String sql = "INSERT INTO insumos (nombre_insumo, unidad_medida, stock_minimo_alerta, proveedor_id, costo, stock) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, insumo.getNombre());
            ps.setString(2, insumo.getUnidadMedida());
            ps.setDouble(3, insumo.getStockMinimo());
            ps.setInt(4, insumo.getProveedor().getProveedorId());
            ps.setFloat(5, insumo.getCosto());
            ps.setFloat(6, insumo.getCantidad());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Insumo> listar() {
        List<Insumo> lista = new ArrayList<>();

        String sql = "SELECT i.*, p.proveedor_id, p.nombre_empresa " +
                     "FROM insumos i " +
                     "JOIN proveedores p ON i.proveedor_id = p.proveedor_id";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Crear proveedor
                Proveedor proveedor = new Proveedor();
                proveedor.setProveedorId(rs.getInt("proveedor_id"));
                proveedor.setNombre_empresa(rs.getString("nombre_empresa"));

                // Crear insumo
                Insumo insumo = new Insumo();
                insumo.setInsumoId(rs.getInt("insumo_id"));
                insumo.setNombre(rs.getString("nombre"));
                insumo.setUnidadMedida(rs.getString("unidad_medida"));
                insumo.setStockMinimo(rs.getDouble("stock_minimo"));
                insumo.setCosto(rs.getFloat("costo"));
                insumo.setCantidad(rs.getFloat("cantidad"));
                insumo.setProveedor(proveedor);

                lista.add(insumo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public Insumo obtenerPorId(int id) {
        String sql = "SELECT i.*, p.proveedor_id, p.nombre_empresa " +
                     "FROM insumos i " +
                     "JOIN proveedores p ON i.proveedor_id = p.proveedor_id " +
                     "WHERE i.insumo_id = ?";

        Insumo insumo = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            System.out.println("xd");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Proveedor proveedor = new Proveedor();
                proveedor.setProveedorId(rs.getInt("proveedor_id"));
                proveedor.setNombre_empresa(rs.getString("nombre_empresa"));

                insumo = new Insumo();
                insumo.setInsumoId(rs.getInt("insumo_id"));
                insumo.setNombre(rs.getString("nombre_insumo"));
                insumo.setUnidadMedida(rs.getString("unidad_medida"));
                insumo.setStockMinimo(rs.getDouble("stock_minimo_alerta"));
                insumo.setCosto(rs.getFloat("costo"));
                insumo.setCantidad(rs.getFloat("stock"));
                insumo.setProveedor(proveedor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return insumo;
    }

    @Override
    public void actualizarStock(int insumoId, float stock) {
        String sql = "UPDATE insumos SET stock = ? WHERE insumo_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, stock);
            ps.setInt(2, insumoId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actualizarProveedor(int insumoId, Proveedor proveedor) {
        int proveedor_id = proveedor.getProveedorId();
        String sql = "UPDATE insumos SET proveedor_id = ? WHERE insumo_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, proveedor_id);
            ps.setInt(2, insumoId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
            
    }

    @Override
    public void actualizarCosto(int insumoId, float costo) {
        String sql = "UPDATE insumos SET costo = ? WHERE insumo_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, costo);
            ps.setInt(2, insumoId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}