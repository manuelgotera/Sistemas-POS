package proyecto.pos.controller;

import proyecto.pos.service.RecetaService;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import proyecto.pos.model.Receta;

public class RecetaController {

    private RecetaService recetaService;

    public RecetaController(Connection conexion) {

        this.recetaService =
                new RecetaService(conexion);
    }

    // =========================
    // REGISTRAR RECETA
    // =========================
    public void registrarReceta(
            Receta receta
    ) {
        System.out.println("xd");
        try {

            recetaService.registrarReceta(receta);

            JOptionPane.showMessageDialog(
                    null,
                    "Receta registrada correctamente"
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
    // LISTAR RECETA POR PLATO
    // =========================
    public List<Receta> listarRecetaPorPlato(
            int platoId
    ) {

        try {

            return recetaService
                    .listarRecetaPorPlato(
                            platoId
                    );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al listar receta: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return new ArrayList<>();
        }
    }

    // =========================
    // VALIDAR INSUMO DUPLICADO
    // =========================
    public boolean existeInsumoEnReceta(
            int platoId,
            int insumoId
    ) {

        try {

            return recetaService
                    .existeInsumoEnReceta(
                            platoId,
                            insumoId
                    );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al validar receta: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    // =========================
    // CALCULAR COSTO TOTAL
    // =========================
    public double calcularCostoTotal(
            int platoId
    ) {

        try {

            return recetaService
                    .calcularCostoTotal(
                            platoId
                    );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error al calcular costo: "
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return 0;
        }
    }
}