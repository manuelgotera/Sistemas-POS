package proyecto.pos.model;

import java.sql.Timestamp;

/**
 * HU-07 — DTO liviano solo para listar las ventas de una caja en la
 * pantalla de Cierre ERP. No reemplaza al modelo Venta completo.
 */
public class VentaResumenCaja {

    private int ventaId;
    private Timestamp fechaHora;
    private String clienteNombre;
    private double total;

    public VentaResumenCaja(int ventaId, Timestamp fechaHora, String clienteNombre, double total) {
        this.ventaId = ventaId;
        this.fechaHora = fechaHora;
        this.clienteNombre = clienteNombre;
        this.total = total;
    }

    public int getVentaId() { return ventaId; }
    public Timestamp getFechaHora() { return fechaHora; }
    public String getClienteNombre() { return clienteNombre; }
    public double getTotal() { return total; }
}