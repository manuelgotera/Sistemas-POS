package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;

import proyecto.pos.dao.interfaces.InsumoDAO;
import proyecto.pos.dao.impl.InsumoDAOImpl;

import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;

public class InsumoService {

    private InsumoDAO insumoDAO;

    public InsumoService(Connection conexion) {

        this.insumoDAO =
                new InsumoDAOImpl(conexion);
    }

    // =========================
    // REGISTRAR INSUMO
    // =========================
    public void registrarInsumo(
            Insumo insumo
    ) {

        // =========================
        // VALIDAR NULL
        // =========================
        if (insumo == null) {

            throw new IllegalArgumentException(
                    "El insumo no puede ser null"
            );
        }

        // =========================
        // VALIDAR NOMBRE
        // =========================
        if (
            insumo.getNombre() == null
            ||
            insumo.getNombre().trim().isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "El nombre del insumo es obligatorio"
            );
        }

        // =========================
        // VALIDAR UNIDAD
        // =========================
        if (
            insumo.getUnidadMedida() == null
            ||
            insumo.getUnidadMedida()
                    .trim()
                    .isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "La unidad de medida es obligatoria"
            );
        }

        // =========================
        // VALIDAR STOCK MINIMO
        // =========================
        if (
            insumo.getStockMinimo() < 0
        ) {

            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo"
            );
        }

        // =========================
        // VALIDAR COSTO
        // =========================
        if (
            insumo.getCosto() < 0
        ) {

            throw new IllegalArgumentException(
                    "El costo no puede ser negativo"
            );
        }

        // =========================
        // VALIDAR STOCK
        // =========================
        if (
            insumo.getCantidad() < 0
        ) {

            throw new IllegalArgumentException(
                    "El stock no puede ser negativo"
            );
        }

        // =========================
        // VALIDAR PROVEEDOR
        // =========================
        if (
            insumo.getProveedor() == null
        ) {

            throw new IllegalArgumentException(
                    "El insumo debe tener proveedor"
            );
        }

        if (
            insumo.getProveedor()
                    .getId() <= 0
        ) {

            throw new IllegalArgumentException(
                    "Proveedor inválido"
            );
        }

        // =========================
        // INSERTAR
        // =========================
        insumoDAO.insertar(insumo);
    }

    // =========================
    // LISTAR INSUMOS
    // =========================
    public List<Insumo> listarInsumos() {

        return insumoDAO.listar();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    public Insumo obtenerPorId(
            int id
    ) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "ID de insumo inválido"
            );
        }

        Insumo insumo =
                insumoDAO.obtenerPorId(id);

        if (insumo == null) {

            throw new RuntimeException(
                    "Insumo no encontrado"
            );
        }

        return insumo;
    }

    // =========================
    // ACTUALIZAR STOCK
    // =========================
    public void actualizarStock(
            int insumoId,
            float stock
    ) {

        if (insumoId <= 0) {

            throw new IllegalArgumentException(
                    "ID de insumo inválido"
            );
        }

        if (stock < 0) {

            throw new IllegalArgumentException(
                    "El stock no puede ser negativo"
            );
        }

        insumoDAO.actualizarStock(
                insumoId,
                stock
        );
    }

    // =========================
    // AUMENTAR STOCK
    // =========================
    public void aumentarStock(
            int insumoId,
            float cantidad
    ) {

        if (cantidad <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0"
            );
        }

        Insumo insumo =
                obtenerPorId(insumoId);

        float nuevoStock =
                insumo.getCantidad()
                +
                cantidad;

        insumoDAO.actualizarStock(
                insumoId,
                nuevoStock
        );
    }

    // =========================
    // DISMINUIR STOCK
    // =========================
    public void disminuirStock(
            int insumoId,
            float cantidad
    ) {

        if (cantidad <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor a 0"
            );
        }

        Insumo insumo =
                obtenerPorId(insumoId);

        float stockActual =
                insumo.getCantidad();

        if (cantidad > stockActual) {

            throw new RuntimeException(
                    "Stock insuficiente"
            );
        }

        float nuevoStock =
                stockActual
                -
                cantidad;

        insumoDAO.actualizarStock(
                insumoId,
                nuevoStock
        );
    }

    // =========================
    // ACTUALIZAR COSTO
    // =========================
    public void actualizarCosto(
            int insumoId,
            float costo
    ) {

        if (insumoId <= 0) {

            throw new IllegalArgumentException(
                    "ID de insumo inválido"
            );
        }

        if (costo < 0) {

            throw new IllegalArgumentException(
                    "El costo no puede ser negativo"
            );
        }

        insumoDAO.actualizarCosto(
                insumoId,
                costo
        );
    }

    // =========================
    // ACTUALIZAR PROVEEDOR
    // =========================
    public void actualizarProveedor(
            int insumoId,
            Proveedor proveedor
    ) {

        if (insumoId <= 0) {

            throw new IllegalArgumentException(
                    "ID de insumo inválido"
            );
        }

        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "Proveedor inválido"
            );
        }

        if (proveedor.getId() <= 0) {

            throw new IllegalArgumentException(
                    "Proveedor inválido"
            );
        }

        insumoDAO.actualizarProveedor(
                insumoId,
                proveedor
        );
    }

    // =========================
    // VALIDAR STOCK MINIMO
    // =========================
    public boolean stockBajo(
            int insumoId
    ) {

        Insumo insumo =
                obtenerPorId(insumoId);

        return
                insumo.getCantidad()
                <=
                insumo.getStockMinimo();
    }

    public Connection getConexion() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}