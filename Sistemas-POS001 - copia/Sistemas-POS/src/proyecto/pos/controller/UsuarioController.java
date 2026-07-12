package proyecto.pos.controller;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.model.Usuario;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Rol;
import proyecto.pos.service.UsuarioService;

public class UsuarioController {

    private UsuarioService usuarioService;

    public UsuarioController(Connection conexion) {
        this.usuarioService = new UsuarioService(conexion);
    }

    // ========================
    // REGISTRAR USUARIO
    // ========================
    /**
     * Registra un usuario en el sistema.
     * Recibe los IDs de Empleado y Rol para asociar correctamente los objetos al modelo.
     */
    public void registrarUsuario(String username, String password, String estado, 
                                 Empleado empleado, Rol rol) {

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setEstado(estado);

        // Construimos el objeto Empleado asociado
        usuario.setEmpleado(empleado);

        // Construimos el objeto Rol asociado
        usuario.setRol(rol);

        // Enviamos a la capa de servicio para validación e inserción
        usuarioService.registrarUsuario(usuario);
    }

    // ========================
    // OBTENER USUARIO
    // ========================
    public Usuario obtenerPorId(int id) {
        return usuarioService.obtenerUsuarioPorId(id);
    }

    public Usuario obtenerPorUsername(String username) {
        return usuarioService.obtenerUsuarioPorUsername(username);
    }

    // ========================
    // LISTAR USUARIOS
    // ========================
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }
    public List<Rol> listarRoles() {
        return usuarioService.listarRoles();
    }
    
    

    // ========================
    // ACTUALIZAR USUARIO
    // ========================
    public void actualizarUsuario(int usuarioId, String username, String password, 
                                  String estado, int empleadoId, String empleadoNombre, 
                                  int rolId, String rolNombre) {

        Usuario usuario = new Usuario();
        usuario.setUsuarioId(usuarioId);
        usuario.setUsername(username);
        usuario.setPassword(password);
        usuario.setEstado(estado);

        Empleado empleado = new Empleado();
        empleado.setId(empleadoId);
        empleado.setNombre(empleadoNombre);
        usuario.setEmpleado(empleado);

        Rol rol = new Rol();
        rol.setId(rolId);
        rol.setNombre_rol(rolNombre);
        usuario.setRol(rol);

        usuarioService.actualizarUsuario(usuario);
    }
    
    // Dentro de UsuarioController.java
    public boolean actualizarPassword(int usuarioId, String nuevaPassword) {
        try {
            return usuarioService.actualizarPassword(usuarioId, nuevaPassword);
        } catch (IllegalArgumentException e) {
            // Relanzamos las excepciones de reglas de negocio para que la vista las pinte en un JOptionPane
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error en el controlador al intentar cambiar la clave: " + e.getMessage(), e);
        }
    }
    // ========================
    // ELIMINAR USUARIO
    // ========================
    public void eliminarPorId(int id) {
        usuarioService.eliminarUsuarioPorId(id);
    }

    // ===========================================
    // MÉTODOS DE CONTROL DE ACCESO / AUTENTICACIÓN
    // ===========================================
    
    /**
     * Permite validar las credenciales del usuario desde el formulario de Login.
     * Retorna el objeto Usuario completo si el login es exitoso.
     */
    public Usuario autenticarUsuario(String username, String password) {
        return usuarioService.autenticar(username, password);
    }
}