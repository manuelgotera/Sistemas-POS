/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

import java.util.Date;

/**
 *
 * @author HP
 */
public class Merma {

    private int mermaId;
    private Insumo insumo;
    private Empleado empleado;
    private double cantidad;
    private String motivo;
    private Date fecha_registro;

    public Merma() {
    }

    public Merma(int mermaId, Insumo insumo, Empleado empleado, double cantidad, String motivo, Date fecha_registro) {
        this.mermaId = mermaId;
        this.insumo = insumo;
        this.empleado = empleado;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha_registro = fecha_registro;
    }

    public Merma(Insumo insumo, Empleado empleado, double cantidad, String motivo, Date fecha_registro) {
        this.insumo = insumo;
        this.empleado = empleado;
        this.cantidad = cantidad;
        this.motivo = motivo;
        this.fecha_registro = fecha_registro;
    }

    public int getMermaId() {
        return mermaId;
    }

    public void setMermaId(int mermaId) {
        this.mermaId = mermaId;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    
}