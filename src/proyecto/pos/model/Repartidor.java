/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author USER
 */

public class Repartidor {

    private int repartidorId;
    private String nombre;
    private String telefono;
    private boolean disponible;
    private int pedidosAsignados;

    public Repartidor() {
    }

    public int getRepartidorId() {
        return repartidorId;
    }

    public void setRepartidorId(int repartidorId) {
        this.repartidorId = repartidorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getPedidosAsignados() {
        return pedidosAsignados;
    }

    public void setPedidosAsignados(int pedidosAsignados) {
        this.pedidosAsignados = pedidosAsignados;
    }

    @Override
    public String toString() {
        return nombre + " (" + pedidosAsignados + " pedidos)";
    }
}