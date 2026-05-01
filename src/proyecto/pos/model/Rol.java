/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class Rol {
    private int id;
    private String nombre_rol;
    private String descripcion;
    
    public Rol(int id, String nombre_rol, String descripcion) {
        this.id = id;
        this.nombre_rol = nombre_rol;
        this.descripcion = descripcion;
    }

    public Rol(String nombre_rol, String descripcion) {
        this.nombre_rol = nombre_rol;
        this.descripcion = descripcion;
    }

    public Rol() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre_rol() {
        return nombre_rol;
    }

    public void setNombre_rol(String nombre_rol) {
        this.nombre_rol = nombre_rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}
