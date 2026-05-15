package proyecto.pos.controller;

import java.sql.Connection;
import java.util.List;

import proyecto.pos.model.Proveedor;
import proyecto.pos.service.ProveedorService;

public class ProveedorController {

    private ProveedorService proveedorService;

    public ProveedorController(Connection conexion) {
        this.proveedorService = new ProveedorService(conexion);
    }

    // ========================
    // REGISTRAR
    // ========================
    public void registrarProveedor(Proveedor proveedor) {
        proveedorService.registrarProveedor(proveedor);
    }

    // ========================
    // OBTENER POR ID
    // ========================
    public Proveedor obtenerPorId(int id) {
        return proveedorService.obtenerProveedorPorId(id);
    }

    // ========================
    // OBTENER POR RUC
    // ========================
    public Proveedor obtenerPorRuc(String ruc) {
        return proveedorService.obtenerProveedorPorRuc(ruc);
    }

    // ========================
    // LISTAR
    // ========================
    public List<Proveedor> listarProveedores() {
        return proveedorService.listarProveedores();
    }

    // ========================
    // ACTUALIZAR
    // ========================
    public void actualizarProveedor(Proveedor proveedor) {
        proveedorService.actualizarProveedor(proveedor);
    }

    // ========================
    // ELIMINAR
    // ========================
    public void eliminarProveedor(int id) {
        proveedorService.eliminarProveedor(id);
    }
}