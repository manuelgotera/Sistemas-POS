/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;
import java.util.List;
import proyecto.pos.model.Caja;
import proyecto.pos.model.Caja;

public interface CajaDAO{
    void insertar(Caja caja);
    Caja obtenerPorId(int id);
    Caja obtenerCajaAbierta();
    List<Caja> listar();
    void actualizar (Caja caja);
    //void eliminar (int id);
    
    
}