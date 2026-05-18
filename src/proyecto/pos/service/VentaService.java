package proyecto.pos.service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import proyecto.pos.dao.impl.VentaDAOImpl;
import proyecto.pos.dao.interfaces.VentaDAO;

import proyecto.pos.model.ComprobantePago;
import proyecto.pos.model.Receta;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;

public class VentaService {

    private VentaDAO ventaDAO;
    private InsumoService insumo_service;
    private RecetaService receta_service;
    
    public VentaService(Connection conexion) {

        this.ventaDAO =
                new VentaDAOImpl(conexion);
        this.insumo_service = new InsumoService(conexion);
        this.receta_service = new RecetaService(conexion);
    }

    // =====================================================
    // REGISTRAR VENTA
    // =====================================================
    public void registrarVenta(Venta venta) {

        // =========================
        // VALIDAR OBJETO
        // =========================
        if (venta == null) {

            throw new IllegalArgumentException(
                    "La venta no puede ser null"
            );
        }

        validarVenta(venta);

        // =========================
        // CALCULAR TOTALES
        // =========================
        double subtotal =
                calcularSubtotal(venta);

        double igv =
                subtotal * 0.18;

        double total =
                subtotal
                + igv
                - venta.getDescuento();

        if (total < 0) {

            throw new IllegalArgumentException(
                    "El total no puede ser negativo"
            );
        }

        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setTotal(total);

        ventaDAO.insertar(venta);
    }

    // =====================================================
    // LISTAR VENTAS
    // =====================================================
    public List<Venta> listarVentas() {

        return ventaDAO.listar();
    }

    // =====================================================
    // LISTAR POR FECHA
    // =====================================================
    public List<Venta> listarVentasPorFecha(
            Date inicio,
            Date fin
    ) {

        if (inicio == null || fin == null) {

            throw new IllegalArgumentException(
                    "Las fechas no pueden ser null"
            );
        }

        return ventaDAO.listarPorRangoFecha(
                inicio,
                fin
        );
    }

    // =====================================================
    // OBTENER VENTA
    // =====================================================
    public Venta obtenerVenta(int ventaId) {

        if (ventaId <= 0) {

            throw new IllegalArgumentException(
                    "ID inválido"
            );
        }

        Venta venta =
                ventaDAO.obtenerPorId(
                        ventaId
                );

        if (venta == null) {

            throw new RuntimeException(
                    "Venta no encontrada"
            );
        }

        return venta;
    }

    // =====================================================
    // ACTUALIZAR VENTA
    // =====================================================
    public void actualizarVenta(Venta venta) {

        // =========================
        // VALIDAR OBJETO
        // =========================
        if (venta == null) {

            throw new IllegalArgumentException(
                    "La venta no puede ser null"
            );
        }

        // =========================
        // VALIDAR ID
        // =========================
        if (venta.getVentaId() <= 0) {

            throw new IllegalArgumentException(
                    "ID inválido"
            );
        }

        // =========================
        // VALIDAR EXISTENCIA
        // =========================
        Venta actual =
                ventaDAO.obtenerPorId(
                        venta.getVentaId()
                );

        if (actual == null) {

            throw new RuntimeException(
                    "La venta no existe"
            );
        }

        validarVenta(venta);

        // =========================
        // RECALCULAR TOTALES
        // =========================
        double subtotal =
                calcularSubtotal(venta);

        double igv =
                subtotal * 0.18;

        double total =
                subtotal
                + igv
                - venta.getDescuento();

        if (total < 0) {

            throw new IllegalArgumentException(
                    "El total no puede ser negativo"
            );
        }

        venta.setSubtotal(subtotal);
        venta.setIgv(igv);
        venta.setTotal(total);

        ventaDAO.actualizar(venta);
    }

    // =====================================================
    // ELIMINAR VENTA
    // =====================================================
    public void eliminarVenta(int ventaId) {

        if (ventaId <= 0) {

            throw new IllegalArgumentException(
                    "ID inválido"
            );
        }

        Venta venta =
                ventaDAO.obtenerPorId(
                        ventaId
                );

        if (venta == null) {

            throw new RuntimeException(
                    "Venta no encontrada"
            );
        }

        ventaDAO.eliminar(ventaId);
    }

    // =====================================================
    // VALIDAR VENTA
    // =====================================================
    private void validarVenta(Venta venta) {

        // =========================
        // CLIENTE
        // =========================
        if (venta.getCliente() == null) {

            throw new IllegalArgumentException(
                    "La venta debe tener un cliente"
            );
        }

        // =========================
        // EMPLEADO
        // =========================
        if (venta.getEmpleado() == null) {

            throw new IllegalArgumentException(
                    "La venta debe tener un empleado"
            );
        }

        // =========================
        // FECHA
        // =========================
        if (venta.getFecha() == null) {

            venta.setFecha(
                    new java.util.Date()
            );
        }

        // =========================
        // CAJA
        // =========================
        if (venta.getCaja_id() <= 0) {

            throw new IllegalArgumentException(
                    "Caja inválida"
            );
        }

        // =========================
        // MESA
        // =========================
        if (venta.getMesa() == null) {

            throw new IllegalArgumentException(
                    "Debe asignarse una mesa"
            );
        }

        // =========================
        // ESTADO DE PAGO
        // =========================
        if (venta.getEstadoPago() == null) {

            throw new IllegalArgumentException(
                    "Debe tener un estado de pago"
            );
        }

        // =========================
        // DESCUENTO
        // =========================
        if (venta.getDescuento() < 0) {

            throw new IllegalArgumentException(
                    "El descuento no puede ser negativo"
            );
        }

        // =========================
        // DETALLES
        // =========================
        if (
                venta.getDetalles() == null
                ||
                venta.getDetalles().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Debe haber al menos un detalle"
            );
        }

        // =========================
        // VALIDAR DETALLES
        // =========================
        for (
                VentaDetalle d
                : venta.getDetalles()
        ) {

            if (d == null) {

                throw new IllegalArgumentException(
                        "Detalle nulo"
                );
            }

            if (d.getPlato() == null) {

                throw new IllegalArgumentException(
                        "Detalle sin plato"
                );
            }

            if (d.getCantidad() <= 0) {

                throw new IllegalArgumentException(
                        "Cantidad inválida"
                );
            }

            if (d.getPrecioUnitario() <= 0) {

                throw new IllegalArgumentException(
                        "Precio inválido"
                );
            }
        }

        // =========================
        // VALIDAR COMPROBANTES
        // =========================
        if (venta.getComprobantes() != null) {

            for (
                    ComprobantePago c
                    : venta.getComprobantes()
            ) {

                if (c == null) {

                    throw new IllegalArgumentException(
                            "Comprobante nulo"
                    );
                }

                if (
                        c.getTipo_comprobante() == null
                ) {

                    throw new IllegalArgumentException(
                            "Comprobante sin tipo"
                    );
                }
            }
        }
    }

    // =====================================================
    // CALCULAR SUBTOTAL
    // =====================================================
    private double calcularSubtotal(
            Venta venta
    ) {

        return venta.getDetalles()
                .stream()
                .mapToDouble(
                        d ->
                                d.getCantidad()
                                * d.getPrecioUnitario()
                )
                .sum();
    }
    
    public void descontarStockPorVenta(
            int platoId,
            int cantidadVendida
    ) {

        List<Receta> recetas =
            receta_service.listarRecetaPorPlato(platoId);

        for (Receta receta : recetas) {

            int insumoId =
                receta.getInsumo().getInsumoId();

            float cantidadNecesaria =
                receta.getCantidad_requerida()
                * cantidadVendida;

            float stockActual =
                insumo_service.obtenerPorId(insumoId).getCantidad();

            float nuevoStock =
                stockActual - cantidadNecesaria;

            // VALIDAR STOCK
            if (nuevoStock < 0) {

                throw new RuntimeException(
                    "Stock insuficiente para el insumo ID: "
                    + insumoId
                );
            }

            // ACTUALIZAR STOCK
            insumo_service.actualizarStock(
                    insumoId,
                    nuevoStock
            );
        }
    }
}