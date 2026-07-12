package proyecto.pos.dao.interfaces;

import java.util.List;
import proyecto.pos.model.AsientoContable;
import proyecto.pos.model.VentaResumenCaja;

/**
 * HU-07 — Acceso a datos para la sincronización contable con el ERP simulado.
 */
public interface AsientoContableDAO {

    void insertar(AsientoContable asiento);

    List<AsientoContable> listarPorCaja(int cajaId);

    double obtenerTotalVentasPagadas(int cajaId);

    List<VentaResumenCaja> listarVentasDeCaja(int cajaId);
}