/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

import java.util.Date;

/**
 *
 * @author HP
 */
public class Empleado extends Persona {

    private Rol rol;
    private EstadoEmpleado estado;
    private Date fecha_contratación;

    public Empleado() {
    }

    public Empleado(Rol rol, EstadoEmpleado estado, Date fecha_contratación, String nombre, String apellido, String dni, String telefono, String email) {
        super(nombre,apellido, dni, telefono, email);
        this.rol = rol;
        this.estado = estado;
        this.fecha_contratación = fecha_contratación;
    }

    public Empleado(Rol rol, EstadoEmpleado estado, Date fecha_contratación, int id, String nombre, String apellido, String documento, String telefono, String email) {
        super(id, nombre, apellido, documento, telefono, email);
        this.rol = rol;
        this.estado = estado;
        this.fecha_contratación = fecha_contratación;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoEmpleado getEstado() {
        return estado;
    }

    public Date getFecha_contratación() {
        return fecha_contratación;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setEstado(EstadoEmpleado estado) {
        this.estado = estado;
    }

    public void setFecha_contratación(Date fecha_contratación) {
        this.fecha_contratación = fecha_contratación;
    }
    
    @Override
    public String toString() {
        return "Empleado{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", dni='" + dni + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + (rol != null ? rol.getNombre_rol() : "null") +
                ", estado=" + estado +
                ", fecha_contratacion=" + fecha_contratación +
                '}';
    }
   
}