/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class Mesa {
    private int mesaId;
    private int numero_mesa;
    private int capacidad;
    private int estado_mesa;

    public Mesa(int mesaId, int numero_mesa, int capacidad, int estado_mesa) {
        this.mesaId = mesaId;
        this.numero_mesa = numero_mesa;
        this.capacidad = capacidad;
        this.estado_mesa = estado_mesa;
    }

    public Mesa() {
    }

    public void setMesaId(int mesaId) {
        this.mesaId = mesaId;
    }

    public void setNumero_mesa(int numero_mesa) {
        this.numero_mesa = numero_mesa;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setEstado_mesa(int estado_mesa) {
        this.estado_mesa = estado_mesa;
    }

    public Mesa(int numero_mesa, int capacidad, int estado_mesa) {
        this.numero_mesa = numero_mesa;
        this.capacidad = capacidad;
        this.estado_mesa = estado_mesa;
    }

    public int getMesaId() {
        return mesaId;
    }

    public int getNumero_mesa() {
        return numero_mesa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public int getEstado_mesa() {
        return estado_mesa;
    }
    
    
}
