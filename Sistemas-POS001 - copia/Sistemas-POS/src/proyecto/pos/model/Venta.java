/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
/**
 *
 * @author HP
 */



public class Venta {

    private int ventaId;
    private Cliente cliente;
    private Empleado empleado;
    private Date fecha;
    private List<VentaDetalle> detalles;
    private List<ComprobantePago> comprobantes;
    private double total;
    private double igv;
    private double subtotal;
    private double descuento;
    private Mesa mesa;
    private EstadoPago estadoPago;
    private int caja_id;
    
    public Venta(int ventaId, int caja_id, Cliente cliente, Empleado empleado, Date fecha, List<VentaDetalle> detalles, double total, double igv, double subtotal, double descuento, Mesa mesa, EstadoPago estadoPago, List<ComprobantePago> comprobantes) {
        this.ventaId = ventaId;
        this.caja_id = caja_id;
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
        this.comprobantes = comprobantes;
    }

    public Venta(int ventaId, int caja_id, Cliente cliente, Empleado empleado, Date fecha, List<VentaDetalle> detalles, double igv, double subtotal, double descuento, Mesa mesa, EstadoPago estadoPago, List<ComprobantePago> comprobantes) {
        this.ventaId = ventaId;
        this.caja_id = caja_id;
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
        this.comprobantes = comprobantes;
    }
    
    public Venta(int caja_id, Cliente cliente, Empleado empleado, Date fecha, List<VentaDetalle> detalles, double igv, double subtotal, double descuento, Mesa mesa, EstadoPago estadoPago, List<ComprobantePago> comprobantes) {
        this.caja_id = caja_id;
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
        this.comprobantes = comprobantes;
    }

    public Venta() {
        
    }

    public int getCaja_id() {
        return caja_id;
    }

    public void setCaja_id(int caja_id) {
        this.caja_id = caja_id;
    }

    
    public double calcularSubtotal() {

        return detalles.stream()
                       .mapToDouble(VentaDetalle::getSubtotal)
                       .sum();
    }
    
    public double calcularTotal() {
        return subtotal * 0.18;
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

    public void setFecha(Date fecha) {
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

    public Date getFecha() {
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

    public List<ComprobantePago> getComprobantes() {
        return comprobantes;
    }

    public void setComprobantes(List<ComprobantePago> comprobantes) {
        this.comprobantes = comprobantes;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Venta {ID = ").append(ventaId)
          .append(", Cliente = ").append(cliente != null ? cliente.getNombre() : "N/A")
          .append(", Empleado = ").append(empleado != null ? empleado.getNombre() : "N/A")
          .append(", Fecha = ").append(fecha)
          .append(", Mesa = ").append(mesa != null ? mesa.getNumero_mesa() : "N/A")
          .append(", EstadoPago = ").append(estadoPago)
          .append(", Subtotal = ").append(subtotal)
          .append(", IGV = ").append(igv)
          .append(", Descuento = ").append(descuento)
          .append(", Total = ").append(total)
          .append("}\n");

        // =========================
        // DETALLES
        // =========================
        if (detalles != null && !detalles.isEmpty()) {
            sb.append("  Detalles:\n");
            for (VentaDetalle d : detalles) {
                sb.append("    - ")
                  .append(d.getPlato() != null ? d.getPlato().getNombre() : "N/A")
                  .append(" | Cantidad: ").append(d.getCantidad())
                  .append(" | Precio Unitario: ").append(d.getPrecioUnitario())
                  .append(" | Subtotal: ").append(d.getSubtotal());

                if (d.getComentario() != null && !d.getComentario().isEmpty()) {
                    sb.append(" | Comentario: ").append(d.getComentario());
                }

                sb.append("\n");
            }
        } else {
            sb.append("  No hay detalles.\n");
        }

        // =========================
        // COMPROBANTES
        // =========================
        if (comprobantes != null && !comprobantes.isEmpty()) {
            sb.append("  Comprobantes:\n");

            for (ComprobantePago c : comprobantes) {
                sb.append("    - ID: ").append(c.getComprobanteId())
                  .append(" | Tipo: ").append(c.getTipo_comprobante())
                  .append(" | Serie: ").append(c.getSerie_numero())
                  .append(" | Método Pago ID: ")
                  .append(c.getMetodo_pago() != null  ? c.getMetodo_pago().getId_metodoPago() : 0)
                  .append(" | Fecha: ").append(c.getFecha_emision())
                  .append(" | Estado: ").append(c.getEstado())
                  .append("\n");
            }

        } else {
            sb.append("  No hay comprobantes.\n");
        }

        return sb.toString();
    }
    
    
}