/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;
import java.time.LocalDate;

/**
 *
 * @author HP
 */
public class Caja {

    private int cajaId;
    private LocalDate fecha_apertura;
    private LocalDate fecha_cierre;
    private double monto_inicial;
    private double monto_final;
    private String estado;
    private double diferencia;

    public Caja() {
    }

    public Caja(LocalDate fecha_apertura, LocalDate fecha_cierre, double monto_inicial, double monto_final, String estado, double diferencia) {
        this.fecha_apertura = fecha_apertura;
        this.fecha_cierre = fecha_cierre;
        this.monto_inicial = monto_inicial;
        this.monto_final = monto_final;
        this.estado = estado;
        this.diferencia = diferencia;
    }

    
    public Caja(int cajaId, LocalDate fecha_apertura, LocalDate fecha_cierre, double monto_inicial, double monto_final, String estado, double diferencia) {
        this.cajaId = cajaId;
        this.fecha_apertura = fecha_apertura;
        this.fecha_cierre = fecha_cierre;
        this.monto_inicial = monto_inicial;
        this.monto_final = monto_final;
        this.estado = estado;
        this.diferencia = diferencia;
    }

    public int getCajaId() {
        return cajaId;
    }

    public void setCajaId(int cajaId) {
        this.cajaId = cajaId;
    }

    public LocalDate getFecha_apertura() {
        return fecha_apertura;
    }

    public void setFecha_apertura(LocalDate fecha_apertura) {
        this.fecha_apertura = fecha_apertura;
    }

    public LocalDate getFecha_cierre() {
        return fecha_cierre;
    }

    public void setFecha_cierre(LocalDate fecha_cierre) {
        this.fecha_cierre = fecha_cierre;
    }

    public double getMonto_inicial() {
        return monto_inicial;
    }

    public void setMonto_inicial(double monto_inicial) {
        this.monto_inicial = monto_inicial;
    }

    public double getMonto_final() {
        return monto_final;
    }

    public void setMonto_final(double monto_final) {
        this.monto_final = monto_final;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

   
}