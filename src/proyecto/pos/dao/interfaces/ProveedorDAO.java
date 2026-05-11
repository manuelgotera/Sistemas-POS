/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.interfaces;

import java.util.List;
import proyecto.pos.model.Proveedor;

/**
 *
 * @author HP
 */
public interface ProveedorDAO {
    public void insertar(Proveedor proveedor);
    public Proveedor obtenerPorId(int id);
    public List<Proveedor> listar();
    public void actualizar(Proveedor proveedor);
    public void eliminar(int id);
}
