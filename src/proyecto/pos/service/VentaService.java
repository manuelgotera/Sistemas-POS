/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.service;

import java.util.Date;
import java.util.List;
import proyecto.pos.dao.interfaces.VentaDAO;
import proyecto.pos.model.ComprobantePago;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;

/**
 *
 * @author HP
 */
public class VentaService {

    private VentaDAO ventaDAO;

    public VentaService(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }


    public void registrarVenta(Venta venta) {

        try {
            validarVenta(venta);

            double subtotal = calcularSubtotal(venta);
            double igv = subtotal * 0.18;
            double total = subtotal + igv - venta.getDescuento();

            if (total < 0) {
                throw new IllegalArgumentException("El total no puede ser negativo");
            }

            venta.setSubtotal(subtotal);
            venta.setIgv(igv);
            venta.setTotal(total);

            ventaDAO.insertar(venta);

        } catch (IllegalArgumentException e) {
            throw e; // errores de negocio
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar la venta", e);
        }
    }

    public List<Venta> listarVentas() {
        try {
            return ventaDAO.listar();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar ventas", e);
        }
    }

    public List<Venta> listarVentasPorFecha(Date inicio, Date fin) {
        try {
            return ventaDAO.listarPorRangoFecha(inicio, fin);
        } catch (Exception e) {
            throw new RuntimeException("Error al listar ventas", e);
        }
    }
    
    
    public Venta obtenerVenta(int ventaId) {

        if (ventaId <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        try {
            Venta venta = ventaDAO.obtenerPorId(ventaId);

            if (venta == null) {
                throw new IllegalArgumentException("Venta no encontrada");
            }

            return venta;

        } catch (Exception e) {
            throw new RuntimeException("Error al acceder a la base de datos", e);
        }
    }
    
    public void actualizarVenta(Venta venta) {

        if (venta == null) {
            throw new IllegalArgumentException("Venta nula");
        }

        if (venta.getVentaId() <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        validarVenta(venta);

        try {
            Venta existente = ventaDAO.obtenerPorId(venta.getVentaId());

            if (existente == null) {
                throw new IllegalArgumentException("La venta no existe");
            }

            double subtotal = calcularSubtotal(venta);
            double igv = subtotal * 0.18;
            double total = subtotal + igv - venta.getDescuento();

            if (total < 0) {
                throw new IllegalArgumentException("El total no puede ser negativo");
            }

            venta.setSubtotal(subtotal);
            venta.setIgv(igv);
            venta.setTotal(total);

            ventaDAO.actualizar(venta);

        } catch (IllegalArgumentException e) {
            // errores de negocio → no los envuelvas
            throw e;
        } catch (Exception e) {
            // errores técnicos (BD, conexión, etc.)
            throw new RuntimeException("Error al actualizar la venta", e);
        }
    }


    public void eliminarVenta(int ventaId) {

        try {
            if (ventaId <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }
            ventaDAO.eliminar(ventaId);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la venta", e);
        }
    }

    private void validarVenta(Venta venta) {

        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }

        if (venta.getCliente() == null) {
            throw new IllegalArgumentException("La venta debe tener un cliente");
        }

        if (venta.getEmpleado() == null) {
            throw new IllegalArgumentException("La venta debe tener un empleado");
        }

        if (venta.getFecha() == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }

        if (venta.getCaja_id() <= 0) {
            throw new IllegalArgumentException("Caja inválida");
        }

        if (venta.getMesa() == null) {
            throw new IllegalArgumentException("Debe asignarse una mesa");
        }

        if (venta.getEstadoPago() == null) {
            throw new IllegalArgumentException("Debe tener un estado de pago");
        }

        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un detalle");
        }

        if (venta.getDescuento() < 0) {
            throw new IllegalArgumentException("El descuento no puede ser negativo");
        }

        // 🔍 Validar cada detalle
        for (VentaDetalle d : venta.getDetalles()) {

            if (d == null) {
                throw new IllegalArgumentException("Detalle nulo");
            }

            if (d.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cantidad inválida en detalle");
            }

            if (d.getPrecioUnitario() <= 0) {
                throw new IllegalArgumentException("Precio inválido en detalle");
            }

            if (d.getPlato() == null) {
                throw new IllegalArgumentException("Detalle sin plato");
            }
        }

        // 🔍 Validar comprobantes (si existen)
        if (venta.getComprobantes() != null) {
            for (ComprobantePago c : venta.getComprobantes()) {

                if (c == null) {
                    throw new IllegalArgumentException("Comprobante nulo");
                }

                if (c.getTipo_comprobante() == null) {
                    throw new IllegalArgumentException("Comprobante sin tipo");
                }
            }
        }
    }

    private double calcularSubtotal(Venta venta) {
        return venta.getDetalles()
                .stream()
                .mapToDouble(d -> d.getCantidad() * d.getPrecioUnitario())
                .sum();
    }

}
