/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;

import java.util.List;
import proyecto.pos.model.Receta;

/**
 *
 * @author HP
 */
public interface RecetaDAO {
        void insertar(Receta receta);
        List<Receta> listar(int plato_id);
        

}
