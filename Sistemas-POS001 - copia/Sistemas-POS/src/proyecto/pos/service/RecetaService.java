package proyecto.pos.service;

import proyecto.pos.dao.interfaces.RecetaDAO;
import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.impl.RecetaDAOImpl;
import proyecto.pos.model.Receta;
    
public class RecetaService {

    private RecetaDAO recetaDAO;

    public RecetaService(Connection conexion) {

        this.recetaDAO =
                new RecetaDAOImpl(conexion);
    }

    // =========================
    // REGISTRAR RECETA
    // =========================
    public void registrarReceta(Receta receta) {

        // =========================
        // VALIDAR RECETA
        // =========================
        if (receta == null) {

            throw new IllegalArgumentException(
                    "La receta no puede ser null"
            );
        }

        // =========================
        // VALIDAR PLATO
        // =========================
        if (receta.getPlato() == null) {

            throw new IllegalArgumentException(
                    "La receta debe tener un plato"
            );
        }

        if (receta.getPlato().getPlatoId() <= 0) {

            throw new IllegalArgumentException(
                    "ID de plato inválido"
            );
        }

        // =========================
        // VALIDAR INSUMO
        // =========================
        if (receta.getInsumo() == null) {

            throw new IllegalArgumentException(
                    "La receta debe tener un insumo"
            );
        }

        if (receta.getInsumo().getInsumoId() <= 0) {

            throw new IllegalArgumentException(
                    "ID de insumo inválido"
            );
        }

        // =========================
        // VALIDAR CANTIDAD
        // =========================
        if (receta.getCantidad_requerida() <= 0) {

            throw new IllegalArgumentException(
                    "La cantidad requerida debe ser mayor a 0"
            );
        }

        // =========================
        // EVITAR DUPLICADOS
        // =========================
        List<Receta> recetasExistentes =
                recetaDAO.listar(
                        receta.getPlato().getPlatoId()
                        
                );
        System.out.println("log1");
        for (Receta r : recetasExistentes) {

            if (
                r.getInsumo().getInsumoId()
                ==
                receta.getInsumo().getInsumoId()
            ) {

                throw new RuntimeException(
                        "El insumo ya existe en la receta"
                );
            }
        }

        // =========================
        // INSERTAR
        // =========================
        recetaDAO.insertar(receta);
    }

    // =========================
    // LISTAR RECETA POR PLATO
    // =========================
    public List<Receta> listarRecetaPorPlato(
            int platoId
    ) {

        if (platoId <= 0) {

            throw new IllegalArgumentException(
                    "ID de plato inválido"
            );
        }

        return recetaDAO.listar(platoId);
    }

    // =========================
    // VALIDAR EXISTENCIA
    // =========================
    public boolean existeInsumoEnReceta(
            int platoId,
            int insumoId
    ) {

        List<Receta> recetas =
                recetaDAO.listar(platoId);

        for (Receta r : recetas) {

            if (
                r.getInsumo().getInsumoId()
                ==
                insumoId
            ) {

                return true;
            }
        }

        return false;
    }

    // =========================
    // CALCULAR COSTO TOTAL
    // =========================
    public double calcularCostoTotal(
            int platoId
    ) {

        List<Receta> recetas =
                recetaDAO.listar(platoId);

        double total = 0;

        for (Receta r : recetas) {

            total +=
                    r.getCantidad_requerida()
                    *
                    r.getInsumo().getCosto();
        }

        return total;
    }
}