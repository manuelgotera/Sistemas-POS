/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class Usuario {
    private int usuario_id;
    private String username;
    private String password;
    private String estado;
    private Empleado empleado;
    private Rol rol;
    
    public Usuario() {
    }

    public Usuario(int usuarioId, String username, String password, String estado, Empleado empleado, Rol rol) {
        this.usuario_id = usuarioId;
        this.username = username;
        this.password = password;
        this.estado = estado;
        this.empleado = empleado;
        this.rol = rol;
    }

    public Usuario(String username, String password, String estado, Empleado empleado, Rol rol) {
        this.username = username;
        this.password = password;
        this.estado = estado;
        this.empleado = empleado;
        this.rol = rol;
    }

    // ============================
    // 📌 GETTERS Y SETTERS
    // ============================

    public int getUsuarioId() {
        return usuario_id;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuario_id = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    // ⚠️ No expongas el password libremente en producción
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    // ============================
    // 📌 MÉTODOS ÚTILES
    // ============================

    public boolean estaActivo() {
        return estado == "ACTIVO";
    }

    public boolean esAdmin() {
        System.out.println("ES ADMIN");
        return rol != null && rol.getNombre_rol().equalsIgnoreCase("ADMINISTRADOR");
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "usuarioId=" + usuario_id +
                ", username='" + username + '\'' +
                ", estado=" + estado +
                ", empleado=" + (empleado != null ? empleado.getNombre() : "null") +
                ", rol=" + (rol != null ? rol.getNombre_rol() : "null") +
                '}';
    }
}
