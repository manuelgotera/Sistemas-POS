/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Plato;
import proyecto.pos.dao.interfaces.RecetaDAO;
import proyecto.pos.model.Receta;

/**
 *
 * @author HP
 */
public class RecetaDAOImpl implements RecetaDAO{
    
    private Connection conexion;

    public RecetaDAOImpl(){
        
    }
    
    public RecetaDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }
    
    @Override
    public void insertar(Receta receta) {
        String sql = "INSERT INTO recetas_escandallo (plato_id, insumo_id, cantidad_requerida) VALUES (?, ?, ?)";
        System.out.println(receta);
        System.out.println(receta.getPlato());
        System.out.println(receta.getInsumo());
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            System.out.println("LA REPUTA MADRE");
            ps.setInt(1, receta.getPlato().getPlatoId());
            System.out.println(receta.getPlato().getPlatoId());
            ps.setInt(2, receta.getInsumo().getInsumoId());
            System.out.println(receta.getInsumo().getInsumoId());
            ps.setFloat(3, receta.getCantidad_requerida());
            System.out.println(receta.getCantidad_requerida());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Receta> listar(int plato_id) {
        List<Receta> lista = new ArrayList<>();

        String sql = """
            SELECT
                re.receta_id,
                re.cantidad_requerida,

                pm.plato_id,
                pm.nombre_plato,
                pm.precio_venta,
                pm.disponible,

                i.insumo_id,
                i.nombre_insumo,
                i.unidad_medida,
                i.costo,
                i.stock

            FROM recetas_escandallo re

            INNER JOIN platos_menu pm
                ON re.plato_id = pm.plato_id

            INNER JOIN insumos i
                ON re.insumo_id = i.insumo_id

            WHERE re.plato_id = ?

            ORDER BY re.receta_id
        """;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
    ) {

        // =========================
        // PARAMETRO
        // =========================
        ps.setInt(1, plato_id);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(
                        mapearReceta(rs)
                );
            }
        }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return lista;
    }

    
    private Receta mapearReceta(ResultSet rs) throws SQLException {

        // =========================
        // PLATO
        // =========================
        Plato plato = new Plato();
        plato.setPlatoId(rs.getInt("plato_id"));
        plato.setNombre(rs.getString("nombre_plato"));
        plato.setPrecio(rs.getFloat("precio_venta"));
        plato.setDisponible(rs.getInt("disponible"));
        // =========================
        // INSUMO
        // =========================
        Insumo insumo = new Insumo();
        insumo.setInsumoId(rs.getInt("insumo_id"));
        insumo.setNombre(rs.getString("nombre_insumo"));
        insumo.setUnidadMedida(rs.getString("unidad_medida"));
        insumo.setCosto(rs.getFloat("costo"));
        insumo.setCantidad(rs.getFloat("stock"));
        // =========================
        // RECETA
        // =========================
        Receta r = new Receta();
        r.setReceta_id(rs.getInt("receta_id"));
        r.setCantidad_requerida(rs.getFloat("cantidad_requerida"));
        r.setPlato(plato);
        r.setInsumo(insumo);
        return r;
    }
}
