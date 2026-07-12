package proyecto.pos.model;

import java.sql.Timestamp;

/**
 * HU-07 — Representa un registro del "libro contable" simulado del ERP.
 * Cada instancia corresponde a un intento de sincronización de cierre de caja.
 */
public class AsientoContable {

    private int asientoId;
    private int cajaId;
    private Timestamp fechaSincronizacion;
    private double montoInicial;
    private double totalVentas;
    private double montoFinal;
    private double montoEsperado;
    private double diferencia;
    private String estadoSincronizacion; // SINCRONIZADO | SINCRONIZADO_CON_DIFERENCIA | ERROR
    private String mensaje;

    public AsientoContable() {
    }

    public int getAsientoId() { return asientoId; }
    public void setAsientoId(int asientoId) { this.asientoId = asientoId; }

    public int getCajaId() { return cajaId; }
    public void setCajaId(int cajaId) { this.cajaId = cajaId; }

    public Timestamp getFechaSincronizacion() { return fechaSincronizacion; }
    public void setFechaSincronizacion(Timestamp fechaSincronizacion) { this.fechaSincronizacion = fechaSincronizacion; }

    public double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(double montoInicial) { this.montoInicial = montoInicial; }

    public double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(double totalVentas) { this.totalVentas = totalVentas; }

    public double getMontoFinal() { return montoFinal; }
    public void setMontoFinal(double montoFinal) { this.montoFinal = montoFinal; }

    public double getMontoEsperado() { return montoEsperado; }
    public void setMontoEsperado(double montoEsperado) { this.montoEsperado = montoEsperado; }

    public double getDiferencia() { return diferencia; }
    public void setDiferencia(double diferencia) { this.diferencia = diferencia; }

    public String getEstadoSincronizacion() { return estadoSincronizacion; }
    public void setEstadoSincronizacion(String estadoSincronizacion) { this.estadoSincronizacion = estadoSincronizacion; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}