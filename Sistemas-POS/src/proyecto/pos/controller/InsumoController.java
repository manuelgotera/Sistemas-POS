package proyecto.pos.controller;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;

import proyecto.pos.service.InsumoService;

public class InsumoController {

    private InsumoService insumoService;

    public InsumoController(Connection conexion) {

        this.insumoService =
                new InsumoService(conexion);
    }

    // =========================
    // REGISTRAR INSUMO
    // =========================
    public void registrarInsumo(
            Insumo insumo
    ) {

        try {

            insumoService
                    .registrarInsumo(insumo);

            JOptionPane.showMessageDialog(
                    null,
                    "Insumo registrado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error inesperado: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // LISTAR INSUMOS
    // =========================
    public List<Insumo> listarInsumos() {

        try {

            return insumoService
                    .listarInsumos();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al listar insumos: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return new ArrayList<>();
        }
    }

    // =========================
    // OBTENER INSUMO POR ID
    // =========================
    public Insumo obtenerPorId(
            int insumoId
    ) {

        try {

            return insumoService
                    .obtenerPorId(insumoId);

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error inesperado: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return null;
    }

    // =========================
    // ACTUALIZAR STOCK
    // =========================
    public void actualizarStock(
            int insumoId,
            float stock
    ) {

        try {

            insumoService
                    .actualizarStock(
                            insumoId,
                            stock
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Stock actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // AUMENTAR STOCK
    // =========================
    public void aumentarStock(
            int insumoId,
            float cantidad
    ) {

        try {

            insumoService
                    .aumentarStock(
                            insumoId,
                            cantidad
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Stock aumentado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DISMINUIR STOCK
    // =========================
    public void disminuirStock(
            int insumoId,
            float cantidad
    ) {

        try {

            insumoService
                    .disminuirStock(
                            insumoId,
                            cantidad
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Stock disminuido correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ACTUALIZAR COSTO
    // =========================
    public void actualizarCosto(
            int insumoId,
            float costo
    ) {

        try {

            insumoService
                    .actualizarCosto(
                            insumoId,
                            costo
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Costo actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ACTUALIZAR PROVEEDOR
    // =========================
    public void actualizarProveedor(
            int insumoId,
            Proveedor proveedor
    ) {

        try {

            insumoService
                    .actualizarProveedor(
                            insumoId,
                            proveedor
                    );

            JOptionPane.showMessageDialog(
                    null,
                    "Proveedor actualizado correctamente"
            );

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // VALIDAR STOCK BAJO
    // =========================
    public boolean stockBajo(
            int insumoId
    ) {

        try {

            return insumoService
                    .stockBajo(insumoId);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar stock: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }
}