/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class MetodoPago {
    private int id_metodoPago;
    private String nombre_metodoPago;

    public MetodoPago(int id_metodoPago, String nombre_metodoPago) {
        this.id_metodoPago = id_metodoPago;
        this.nombre_metodoPago = nombre_metodoPago;
    }

    public MetodoPago(String nombre_metodoPago) {
        this.nombre_metodoPago = nombre_metodoPago;
    }

    public MetodoPago() {
    }

    public int getId_metodoPago() {
        return id_metodoPago;
    }

    public void setId_metodoPago(int id_metodoPago) {
        this.id_metodoPago = id_metodoPago;
    }

    public String getNombre_metodoPago() {
        return nombre_metodoPago;
    }

    public void setNombre_metodoPago(String nombre_metodoPago) {
        this.nombre_metodoPago = nombre_metodoPago;
    }
    
    
}
