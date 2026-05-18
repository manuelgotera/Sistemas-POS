package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.impl.UsuarioDAOImpl;
import proyecto.pos.dao.interfaces.UsuarioDAO;
import proyecto.pos.model.Rol;
import proyecto.pos.model.Usuario;

public class UsuarioService {

    private UsuarioDAO usuarioDAO;

    public UsuarioService(Connection conexion) {
        this.usuarioDAO = new UsuarioDAOImpl(conexion);
    }

    // ========================
    // REGISTRAR USUARIO
    // ========================
    public void registrarUsuario(Usuario usuario) {
        
        // 1. Validaciones de presencia de datos
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario (username) no puede estar vacío");
        }

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        // 2. Validaciones de Relaciones Obligatorias
        if (usuario.getEmpleado() == null || usuario.getEmpleado().getId() <= 0) {
            throw new IllegalArgumentException("El usuario debe estar asociado a un Empleado válido");
        }

        if (usuario.getRol() == null || usuario.getRol().getId() <= 0) {
            throw new IllegalArgumentException("El usuario debe tener un Rol asignado");
        }

        // 3. Regla de negocio: Validar que el username no esté duplicado
        Usuario usuarioExistente = usuarioDAO.obtenerPorUsername(usuario.getUsername().trim());
        if (usuarioExistente != null) {
            throw new IllegalArgumentException("El nombre de usuario '" + usuario.getUsername() + "' ya se encuentra registrado");
        }

        // 4. Asignación automática de estado si viene nulo o vacío
        if (usuario.getEstado() == null || usuario.getEstado().trim().isEmpty()) {
            usuario.setEstado("ACTIVO");
        } else {
            usuario.setEstado(usuario.getEstado().toUpperCase());
        }

        // Limpiar espacios en blanco extras antes de guardar
        usuario.setUsername(usuario.getUsername().trim());

        usuarioDAO.insertar(usuario);
    }

    // ========================
    // OBTENER USUARIO
    // ========================
    public Usuario obtenerUsuarioPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        return usuarioDAO.obtenerPorId(id);
    }

    public Usuario obtenerUsuarioPorUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username de búsqueda no puede estar vacío");
        }
        return usuarioDAO.obtenerPorUsername(username.trim());
    }

    // ========================
    // LISTAR USUARIOS
    // ========================
    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }
    public List<Rol> listarRoles() {
        return usuarioDAO.listarRoles();
    }
    
    

    // ========================
    // ACTUALIZAR USUARIO
    // ========================
    public void actualizarUsuario(Usuario usuario) {

        if (usuario.getUsuarioId() <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido para actualización");
        }

        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede quedar vacío");
        }

        if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede quedar vacía");
        }

        if (usuario.getEmpleado() == null || usuario.getEmpleado().getId() <= 0) {
            throw new IllegalArgumentException("Asociación de empleado inválida");
        }

        if (usuario.getRol() == null || usuario.getRol().getId() <= 0) {
            throw new IllegalArgumentException("Asociación de rol inválida");
        }

        // Verificar si el username al que se quiere cambiar ya le pertenece a OTRO usuario
        Usuario usuarioExistente = usuarioDAO.obtenerPorUsername(usuario.getUsername().trim());
        if (usuarioExistente != null && usuarioExistente.getUsuarioId() != usuario.getUsuarioId()) {
            throw new IllegalArgumentException("El username '" + usuario.getUsername() + "' ya está siendo usado por otro usuario");
        }

        usuario.setUsername(usuario.getUsername().trim());
        usuario.setEstado(usuario.getEstado() != null ? usuario.getEstado().toUpperCase() : "ACTIVO");

        usuarioDAO.actualizar(usuario);
    }

    // ========================
    // ELIMINAR USUARIO
    // ========================
    public void eliminarUsuarioPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido para eliminar");
        }
        
        // Opcional: podrías verificar si el usuario existe antes de intentar borrar
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            throw new RuntimeException("El usuario que intenta eliminar no existe");
        }

        usuarioDAO.eliminar(id);
    }
    
    // Dentro de tu clase de Servicio de Usuarios
    public boolean actualizarPassword(int usuarioId, String nuevaPassword) {
        // Regla de negocio: Validar longitud mínima de la contraseña
        if (nuevaPassword == null || nuevaPassword.trim().length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }

        // Si todo está bien, lo enviamos al DAO
        return usuarioDAO.actualizarPassword(usuarioId, nuevaPassword);
    }

    // ===========================================
    // REGLAS DE NEGOCIO EXTRA: LOGIN / SEGURIDAD
    // ===========================================
    
    /**
     * Autentica a un usuario en el sistema comprobando sus credenciales y estado.
     * @return El objeto Usuario completo si las credenciales son válidas.
     */
    public Usuario autenticar(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario y la contraseña son requeridos");
        }

        Usuario usuario = usuarioDAO.obtenerPorUsername(username.trim());

        // Validar si existe el usuario
        if (usuario == null) {
            throw new RuntimeException("El nombre de usuario no existe");
        }

        // Validar si está activo (usando la corrección sugerida en el modelo)
        if (!usuario.estaActivo()) {
            throw new RuntimeException("El usuario se encuentra INACTIVO. Contacte al administrador.");
        }

        // Validar contraseña
        // NOTA: Si en el futuro agregas Bcrypt o un hash, aquí descifrarías/compararías el hash.
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;
    }
}