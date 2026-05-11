/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class Proveedor {

    private int proveedorId;
    private String ruc;
    private String nombre_empresa;
    private String contacto_nombre;
    private String telefono;
    private String email;
    private String direccion;

    public Proveedor() {
    }

    public Proveedor(String nombre_empresa, String ruc, String contacto_nombre, String telefono, String email, String direccion) {
        this.nombre_empresa = nombre_empresa;
        this.ruc = ruc;
        this.contacto_nombre = contacto_nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public Proveedor(int proveedorId, String nombre_empresa, String ruc, String contacto_nombre, String telefono, String email, String direccion) {
        this.proveedorId = proveedorId;
        this.nombre_empresa = nombre_empresa;
        this.ruc = ruc;
        this.contacto_nombre = contacto_nombre;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public int getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(int proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getContacto_nombre() {
        return contacto_nombre;
    }

    public void setContacto_nombre(String contacto_nombre) {
        this.contacto_nombre = contacto_nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Proveedor{" + "proveedorId=" + proveedorId + ", ruc=" + ruc + ", nombre_empresa=" + nombre_empresa + ", contacto_nombre=" + contacto_nombre + ", telefono=" + telefono + ", email=" + email + ", direccion=" + direccion + '}';
    }
    
    
}
