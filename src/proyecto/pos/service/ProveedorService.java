package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;

import proyecto.pos.dao.impl.ProveedorDAOImpl;
import proyecto.pos.dao.interfaces.ProveedorDAO;
import proyecto.pos.model.Proveedor;

public class ProveedorService {

    private ProveedorDAO proveedorDAO;

    public ProveedorService(Connection conexion) {
        this.proveedorDAO = new ProveedorDAOImpl(conexion);
    }

    // ========================
    // REGISTRAR PROVEEDOR
    // ========================
    public void registrarProveedor(Proveedor proveedor) {

        // ========================
        // VALIDACIONES BÁSICAS
        // ========================

        if (proveedor == null) {
            throw new IllegalArgumentException("El proveedor no puede ser null");
        }

        if (proveedor.getNombre_empresa() == null || proveedor.getNombre_empresa().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío");
        }

        if (proveedor.getRuc() == null || proveedor.getRuc().trim().isEmpty()) {
            throw new IllegalArgumentException("El RUC no puede estar vacío");
        }

        if (proveedor.getRuc().length() != 11) {
            throw new IllegalArgumentException("El RUC debe tener 11 dígitos");
        }

        if (!proveedor.getRuc().matches("\\d+")) {
            throw new IllegalArgumentException("El RUC solo debe contener números");
        }

        if (proveedor.getContacto_nombre() == null || proveedor.getContacto_nombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de contacto no puede estar vacío");
        }

        if (proveedor.getTelefono() == null || proveedor.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío");
        }

        if (!proveedor.getTelefono().matches("\\d{7,15}")) {
            throw new IllegalArgumentException("El teléfono debe contener entre 7 y 15 dígitos");
        }

        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            if (!proveedor.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException("El email no tiene formato válido");
            }
        }

        if (proveedor.getDireccion() == null || proveedor.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección no puede estar vacía");
        }

        proveedorDAO.insertar(proveedor);
    }

    // ========================
    // OBTENER PROVEEDOR
    // ========================
    public Proveedor obtenerProveedorPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }

        return proveedorDAO.obtenerPorId(id);
    }

    public Proveedor obtenerProveedorPorRuc(String ruc) {

        if (ruc == null || ruc.trim().isEmpty()) {
            throw new IllegalArgumentException("RUC inválido");
        }

        if (ruc.length() != 11 || !ruc.matches("\\d+")) {
            throw new IllegalArgumentException("RUC inválido");
        }

        return proveedorDAO.obtenerPorRUC(ruc);
    }

    // ========================
    // LISTAR PROVEEDORES
    // ========================
    public List<Proveedor> listarProveedores() {
        return proveedorDAO.listar();
    }

    // ========================
    // ACTUALIZAR PROVEEDOR
    // ========================
    public void actualizarProveedor(Proveedor proveedor) {

        if (proveedor == null) {
            throw new IllegalArgumentException("Proveedor no puede ser null");
        }

        if (proveedor.getProveedorId() <= 0) {
            throw new IllegalArgumentException("ID de proveedor inválido");
        }

        if (proveedor.getNombre_empresa() == null || proveedor.getNombre_empresa().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío");
        }

        if (proveedor.getRuc() == null || proveedor.getRuc().length() != 11) {
            throw new IllegalArgumentException("RUC inválido");
        }

        if (proveedor.getTelefono() != null && !proveedor.getTelefono().matches("\\d{7,15}")) {
            throw new IllegalArgumentException("Teléfono inválido");
        }

        if (proveedor.getEmail() != null && !proveedor.getEmail().trim().isEmpty()) {
            if (!proveedor.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new IllegalArgumentException("Email inválido");
            }
        }

        proveedorDAO.actualizar(proveedor);
    }

    // ========================
    // ELIMINAR PROVEEDOR
    // ========================
    public void eliminarProveedor(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Proveedor proveedor = proveedorDAO.obtenerPorId(id);

        if (proveedor == null) {
            throw new RuntimeException("Proveedor no encontrado");
        }

        proveedorDAO.eliminar(id);
    }
}