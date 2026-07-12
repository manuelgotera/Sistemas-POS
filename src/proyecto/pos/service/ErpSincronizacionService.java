package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.impl.AsientoContableDAOImpl;
import proyecto.pos.dao.interfaces.AsientoContableDAO;
import proyecto.pos.model.AsientoContable;
import proyecto.pos.model.VentaResumenCaja;

/**
 * HU-07 — Sincronización automática al cierre de caja con el módulo contable (ERP simulado).
 * Criterio 1: Sincronización automática al cierre.
 * Criterio 2: Alerta por fallos de conexión.
 * Criterio 3: Registro automático en el asiento contable general.
 * Criterio 4: Generación de reporte de discrepancias.
 *
 * No existe un ERP real disponible en este proyecto, por lo que la
 * "sincronización" se simula persistiendo el asiento en una tabla propia
 * (ASIENTO_CONTABLE). El diseño queda listo para reemplazar esa persistencia
 * por una llamada real (REST/SOAP) el día que exista un ERP con el que integrar.
 */
public class ErpSincronizacionService {

    private static final double TOLERANCIA = 0.01; // redondeo de centavos

    private final AsientoContableDAO asientoDAO;

    public ErpSincronizacionService(Connection conexion) {
        this.asientoDAO = new AsientoContableDAOImpl(conexion);
    }

    /**
     * Ejecuta la sincronización contable de una caja ya cerrada.
     *
     * @param cajaId          ID de la caja recién cerrada.
     * @param montoInicial    Monto con el que se abrió la caja.
     * @param montoFinal      Monto de arqueo ingresado por el gerente al cerrar.
     * @param simularFalloRed Solo para pruebas/demo: fuerza el escenario de Criterio 2.
     */
    public AsientoContable sincronizarCierre(int cajaId, double montoInicial,
                                              double montoFinal, boolean simularFalloRed) {

        if (cajaId <= 0) {
            throw new IllegalArgumentException("Caja inválida para sincronizar.");
        }
        if (montoFinal < 0) {
            throw new IllegalArgumentException("El monto final no puede ser negativo.");
        }

        // Criterio 2: alerta por fallos de conexión.
        // (simulado: en producción aquí iría la llamada real al ERP vía REST/SOAP,
        //  y esta excepción representaría un timeout o error de red real)
        if (simularFalloRed) {
            throw new ErpConexionException(
                "No se pudo establecer conexión con el servidor ERP. " +
                "Verifique la conectividad de red e intente nuevamente."
            );
        }

        double totalVentas    = asientoDAO.obtenerTotalVentasPagadas(cajaId);
        double montoEsperado  = montoInicial + totalVentas;
        double diferencia     = montoFinal - montoEsperado;

        AsientoContable asiento = new AsientoContable();
        asiento.setCajaId(cajaId);
        asiento.setMontoInicial(montoInicial);
        asiento.setTotalVentas(totalVentas);
        asiento.setMontoFinal(montoFinal);
        asiento.setMontoEsperado(montoEsperado);
        asiento.setDiferencia(diferencia);

        // Criterio 4: reporte de discrepancias (se calcula siempre, se sincroniza igual)
        if (Math.abs(diferencia) <= TOLERANCIA) {
            asiento.setEstadoSincronizacion("SINCRONIZADO");
            asiento.setMensaje("Cierre sincronizado correctamente. Caja cuadrada sin discrepancias.");
        } else {
            asiento.setEstadoSincronizacion("SINCRONIZADO_CON_DIFERENCIA");
            asiento.setMensaje(String.format(
                "Cierre sincronizado, pero se detectó una discrepancia de S/. %.2f entre lo esperado y el arqueo.",
                diferencia
            ));
        }

        // Criterio 3: registro automático en el asiento contable general
        asientoDAO.insertar(asiento);

        return asiento;
    }

    public List<VentaResumenCaja> listarVentasDeCaja(int cajaId) {
        return asientoDAO.listarVentasDeCaja(cajaId);
    }

    public double obtenerTotalVentasPagadas(int cajaId) {
        return asientoDAO.obtenerTotalVentasPagadas(cajaId);
    }

    public List<AsientoContable> historialDeCaja(int cajaId) {
        return asientoDAO.listarPorCaja(cajaId);
    }

    /** Excepción específica para distinguir fallas de "red" (Criterio 2) de otros errores. */
    public static class ErpConexionException extends RuntimeException {
        public ErpConexionException(String mensaje) { super(mensaje); }
    }
}