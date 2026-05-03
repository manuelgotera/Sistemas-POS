/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;
import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author HP
 */
public class Caja {

    private int cajaId;
    private Date fecha_apertura;
    private Date fecha_cierre;
    private double monto_inicial;
    private double monto_final;
    private String estado;
    private double diferencia;
    private Empleado empleado;

    public Caja() {
    }

    public Caja(Date fecha_apertura, double monto_inicial, String estado, Empleado empleado) {
        this.fecha_apertura = fecha_apertura;
        this.monto_inicial = monto_inicial;
        this.estado = estado;
        this.empleado = empleado;
    }
    
    public Caja(Empleado empleado, Date fecha_apertura, Date fecha_cierre, double monto_inicial, double monto_final, String estado, double diferencia) {
        this.empleado = empleado;
        this.fecha_apertura = fecha_apertura;
        this.fecha_cierre = fecha_cierre;
        this.monto_inicial = monto_inicial;
        this.monto_final = monto_final;
        this.estado = estado;
        this.diferencia = diferencia;
    }

    
    public Caja(int cajaId, Empleado empleado, Date fecha_apertura, Date fecha_cierre, double monto_inicial, double monto_final, String estado, double diferencia) {
        this.empleado = empleado;
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

    public Date getFecha_apertura() {
        return fecha_apertura;
    }

    public void setFecha_apertura(Date fecha_apertura) {
        this.fecha_apertura = fecha_apertura;
    }

    public Date getFecha_cierre() {
        return fecha_cierre;
    }

    public void setFecha_cierre(Date fecha_cierre) {
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

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

   @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Caja {")
          .append("ID=").append(cajaId)
          .append(", Apertura=").append(fecha_apertura)
          .append(", Cierre=").append(fecha_cierre)
          .append(", Monto Inicial=").append(monto_inicial)
          .append(", Monto Final=").append(monto_final)
          .append(", Estado='").append(estado).append('\'')
          .append(", Diferencia=").append(diferencia)
          .append(", Empleado=").append(empleado != null ? empleado.toString() : "null")
          .append("}");

        return sb.toString();
    }
}