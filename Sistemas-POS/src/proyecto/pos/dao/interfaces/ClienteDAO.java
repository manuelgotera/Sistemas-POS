/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;
import java.sql.ResultSet;
import java.util.List;
import proyecto.pos.model.Cliente;
/**
 *
 * @author HP
 */

public interface ClienteDAO {
    void insertar(Cliente cliente);
    Cliente obtenerPorId(int id);
    Cliente obtenerPorDni(String dni);
    List<Cliente> listar();
    void actualizar(Cliente cliente);
    void eliminar(int id);
    void eliminarPorDni(String dni);
}