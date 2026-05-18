/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;
import java.util.List;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;

/**
 *
 * @author HP
 */

public interface InsumoDAO {
    void insertar(Insumo insumo);
    List<Insumo> listar();
    Insumo obtenerPorId(int id);
    void actualizarStock(int insumoId, float cantidad);
    void actualizarProveedor(int insumoId, Proveedor proveedor);
    void actualizarCosto(int insumoId, float costo);
    void actualizarCompleto(Insumo insumo);
    void eliminar(int insumoId);
}