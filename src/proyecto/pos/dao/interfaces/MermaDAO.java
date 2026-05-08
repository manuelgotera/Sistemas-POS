/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.interfaces;

import java.sql.ResultSet;
import java.util.List;
import proyecto.pos.model.Merma;

/**
 *
 * @author HP
 */
public interface MermaDAO {
        public void insertar(Merma merma);
        public Merma obtenerPorId(int id);
        public List<Merma> listar();
        public void actualizar(Merma merma);
        public void eliminar(int id);
        

}
