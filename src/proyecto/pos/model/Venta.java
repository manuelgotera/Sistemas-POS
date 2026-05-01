/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;
import java.time.LocalDateTime;
import java.util.List;
/**
 *
 * @author HP
 */



public class Venta {

    private int ventaId;
    private Cliente cliente;
    private Empleado empleado;
    private LocalDateTime fecha;
    private List<VentaDetalle> detalles;
    private double total;
    private double igv;
    private double subtotal;
    private double descuento;
    private Mesa mesa;
    private EstadoPago estadoPago;

    public Venta(int ventaId, Cliente cliente, Empleado empleado, LocalDateTime fecha, List<VentaDetalle> detalles, double total, double igv, double subtotal, double descuento, Mesa mesa, EstadoPago estadoPago) {
        this.ventaId = ventaId;
        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = fecha;
        this.detalles = detalles;
        this.igv = igv;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.mesa = mesa;
        this.estadoPago = estadoPago;
        this.total = calcularTotal();
    }

    public Venta(int ventaId, Cliente cliente, Empleado empleado, LocalDateTime fecha, List<VentaDetalle> detalles, double igv, double subtotal, double descuento, Mesa mesa, EstadoPago estadoPago) {
        this.ventaId = ventaId;
        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = fecha;
        this.detalles = detalles;
        this.igv = igv;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.mesa = mesa;
        this.estadoPago = estadoPago;
        this.total = calcularTotal();
    }
    
    public Venta(Cliente cliente, Empleado empleado, LocalDateTime fecha, List<VentaDetalle> detalles, double igv, double subtotal, double descuento, Mesa mesa, EstadoPago estadoPago) {

        this.cliente = cliente;
        this.empleado = empleado;
        this.fecha = fecha;
        this.detalles = detalles;
        this.igv = igv;
        this.subtotal = subtotal;
        this.descuento = descuento;
        this.mesa = mesa;
        this.estadoPago = estadoPago;
        this.total = calcularTotal();
    }

    public Venta() {
    }

    
    public double calcularTotal() {
        return detalles.stream().mapToDouble(VentaDetalle::calcularSubtotal).sum();
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public void setDetalles(List<VentaDetalle> detalles) {
        this.detalles = detalles;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setIgv(double igv) {
        this.igv = igv;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public void setEstadoPago(EstadoPago estadoPago) {
        this.estadoPago = estadoPago;
    }

    public int getVentaId() {
        return ventaId;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<VentaDetalle> getDetalles() {
        return detalles;
    }

    public double getTotal() {
        return total;
    }

    public double getIgv() {
        return igv;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public EstadoPago getEstadoPago() {
        return estadoPago;
    }
    
    
}