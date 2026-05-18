/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;


import java.util.List;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Insumo;

/**
 *
 * @author HP
 */
public interface EmpleadoDAO {
    void insertar(Empleado empleado);
    Empleado obtenerPorId(int id);
    Empleado obtenerPorDni(String dni);
    List<Empleado> listar();
    void actualizar(Empleado empleado);
    void actualizarCompleto(Insumo insumo);
    void eliminar(int insumoId);
}