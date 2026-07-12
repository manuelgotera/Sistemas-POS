/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;
import java.sql.Date;
import java.util.List;
import proyecto.pos.model.Venta;
import java.util.*;
/**
 *
 * @author HP
 */

public interface VentaDAO {
    void insertar(Venta venta);
    public void actualizar(Venta venta);
    public void eliminar(int ventaId);
    Venta obtenerPorId(int id);
    List<Venta> listar();
    public List<Venta> listarPorRangoFecha(java.util.Date inicio, java.util.Date fin);
}