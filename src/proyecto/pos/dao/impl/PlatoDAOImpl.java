/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.impl;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.interfaces.PlatoDAO;
import proyecto.pos.model.Plato;
import java.sql.*;
import java.util.ArrayList;
import proyecto.pos.model.CategoriaMenu;
/**
 *
 * @author HP
 */
public class PlatoDAOImpl implements PlatoDAO{
    
    private Connection conexion;

    public PlatoDAOImpl(Connection conexion){
        this.conexion = conexion;
    }

    @Override
    public void insertar(Plato plato) {
        String sql = "INSERT INTO platos_menu (categoria_id, nombre_plato, precio_venta, disponible)"
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1,plato.getCategoria().getCategoriaId());
            ps.setString(2, plato.getNombre());
            ps.setFloat(3, plato.getPrecio());
            ps.setInt(4, plato.getDisponible());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar plato", e);
        }
    }
        
    @Override
    public List<Plato> listar() {
        List<Plato> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.* " + 
                "FROM platos_menu p JOIN categorias_menu c "+
                "ON p.categoria_id = c.categoria_id";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPlato(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    @Override
    public Plato obtenerPorId(int id) {
        String sql = "SELECT p.*, c.* FROM platos_menu p JOIN categorias_menu c "+
                "ON p.categoria_id = c.categoria_id WHERE plato_id = ?";
        Plato plato = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    plato = mapearPlato(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plato;
    }
    
    @Override
    public void actualizarPrecio(int platoId, float precio) {
        String sql = "UPDATE platos_menu SET precio_venta = ? WHERE plato_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setFloat(1, precio);
            ps.setInt(2, platoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void actualizarDisponibilidad(int platoId, int disponible) {
        String sql = "UPDATE platos_menu SET disponible = ? WHERE plato_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, disponible);
            ps.setInt(2, platoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void actualizarNombre(int platoId, String nombre) {
        String sql = "UPDATE platos_menu SET nombre_plato = ? WHERE plato_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setInt(2, platoId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void actualizarCategoria(int platoId, CategoriaMenu categoria) {
        String sql = "UPDATE platos_menu SET categoria_id = ? WHERE plato_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, categoria.getCategoriaId());
            ps.setInt(2, platoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Plato mapearPlato(ResultSet rs) throws SQLException {
        Plato plato = new Plato();
        plato.setPlatoId(rs.getInt("plato_id"));
        plato.setNombre(rs.getString("nombre_plato"));
        plato.setPrecio(rs.getFloat("precio_venta"));
        plato.setDisponible(rs.getInt("disponible"));

        CategoriaMenu cm = new CategoriaMenu();
        cm.setCategoriaId(rs.getInt("categoria_id"));
        cm.setNombre(rs.getString("nombre_categoria"));
        plato.setCategoria(cm);

        return plato;
    }

    public List<CategoriaMenu> listarCategorias() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
