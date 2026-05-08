package proyecto.pos.model;

/**
 * DTO simple para mostrar el detalle de productos de una venta en el historial.
 */
public class HistorialVentaDetalle {
    private String producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public HistorialVentaDetalle() {
    }

    public HistorialVentaDetalle(String producto, int cantidad, double precioUnitario, double subtotal) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
