/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.interfaces;

import java.util.List;
import proyecto.pos.model.Rol;
import proyecto.pos.model.Usuario;

/**
 *
 * @author HP
 */
public interface UsuarioDAO {
    void insertar(Usuario usuario);

    Usuario obtenerPorId(int id);

    Usuario obtenerPorUsername(String username);

    List<Usuario> listar();
    
    List<Rol> listarRoles();

    void actualizar(Usuario usuario);

    void eliminar(int id);
  
    boolean actualizarPassword(int usuarioId, String nuevaPassword);
}
