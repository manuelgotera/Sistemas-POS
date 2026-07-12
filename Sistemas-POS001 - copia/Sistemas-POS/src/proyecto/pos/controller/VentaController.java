package proyecto.pos.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import proyecto.pos.model.Venta;
import proyecto.pos.service.VentaService;

public class VentaController {

    private VentaService ventaService;

    public VentaController(Connection conexion) {

        this.ventaService =
                new VentaService(conexion);
    }

    // =====================================================
    // REGISTRAR VENTA
    // =====================================================
    public void registrarVenta(Venta venta) {

        ventaService.registrarVenta(venta);
    }

    // =====================================================
    // LISTAR VENTAS
    // =====================================================
    public List<Venta> listarVentas() {

        return ventaService.listarVentas();
    }

    // =====================================================
    // LISTAR POR FECHA
    // =====================================================
    public List<Venta> listarVentasPorFecha(
            Date inicio,
            Date fin
    ) {

        return ventaService.listarVentasPorFecha(
                inicio,
                fin
        );
    }

    // =====================================================
    // OBTENER VENTA
    // =====================================================
    public Venta obtenerVenta(int ventaId) {

        return ventaService.obtenerVenta(
                ventaId
        );
    }

    // =====================================================
    // ACTUALIZAR VENTA
    // =====================================================
    public void actualizarVenta(Venta venta) {

        ventaService.actualizarVenta(
                venta
        );
    }

    // =====================================================
    // ELIMINAR VENTA
    // =====================================================
    public void eliminarVenta(int ventaId) {

        ventaService.eliminarVenta(
                ventaId
        );
    }
    
    
    public void descontarStockPorVenta(
            int platoId,
            int cantidadVendida
    ){
        ventaService.descontarStockPorVenta(platoId, cantidadVendida);
    } 
}