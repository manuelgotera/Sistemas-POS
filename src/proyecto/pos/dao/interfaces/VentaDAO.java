/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;
import java.util.List;
import proyecto.pos.model.Venta;

/**
 *
 * @author HP
 */

public interface VentaDAO {
    void insertar(Venta venta);
    Venta obtenerPorId(int id);
    List<Venta> listar();
}