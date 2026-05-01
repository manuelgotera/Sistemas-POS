/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class VentaDetalle {

    private int detalleId;
    private Plato plato;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    private String comentario;

    public VentaDetalle(int detalleId, Plato plato, int cantidad, double precioUnitario, double subtotal, String comentario) {
        this.detalleId = detalleId;
        this.plato = plato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
        this.comentario = comentario;
    }

    public VentaDetalle(Plato plato, int cantidad, double precioUnitario, double subtotal, String comentario) {
        this.plato = plato;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = calcularSubtotal();
        this.comentario = comentario;
    }

    public VentaDetalle() {
    }

    public double calcularSubtotal() {
        return cantidad * precioUnitario;
    }
}
