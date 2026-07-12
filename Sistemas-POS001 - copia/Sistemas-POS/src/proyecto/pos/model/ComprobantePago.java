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
public class ComprobantePago {
    private int comprobanteId;
    private String tipo_comprobante;
    private String serie_numero;
    private MetodoPago metodo_pago;
    private Date fecha_emision;
    private String estado;

    public ComprobantePago() {
    }

    
    public ComprobantePago(int comprobanteId, String tipo_comprobante, String serie_numero, MetodoPago metodo_pago, Date fecha_emision, String estado) {
        this.comprobanteId = comprobanteId;
        this.tipo_comprobante = tipo_comprobante;
        this.serie_numero = serie_numero;
        this.metodo_pago = metodo_pago;
        this.fecha_emision = fecha_emision;
        this.estado = estado;
    }

    public ComprobantePago(String tipo_comprobante, String serie_numero, MetodoPago metodo_pago, Date fecha_emision, String estado) {
        this.tipo_comprobante = tipo_comprobante;
        this.serie_numero = serie_numero;
        this.metodo_pago = metodo_pago;
        this.fecha_emision = fecha_emision;
        this.estado = estado;
    }

    public void setComprobanteId(int comprobanteId) {
        this.comprobanteId = comprobanteId;
    }

    public void setTipo_comprobante(String tipo_comprobante) {
        this.tipo_comprobante = tipo_comprobante;
    }

    public void setSerie_numero(String serie_numero) {
        this.serie_numero = serie_numero;
    }

    public void setMetodo_pago(MetodoPago metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public void setFecha_emision(Date fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    
    
    public int getComprobanteId() {
        return comprobanteId;
    }

    public String getTipo_comprobante() {
        return tipo_comprobante;
    }

    public String getSerie_numero() {
        return serie_numero;
    }

    public MetodoPago getMetodo_pago() {
        return metodo_pago;
    }

    public Date getFecha_emision() {
        return fecha_emision;
    }

    public String getEstado() {
        return estado;
    }
    
    @Override
    public String toString() {
        return "ComprobantePago{" +
                "comprobanteId=" + comprobanteId +
                ", tipo_comprobante='" + tipo_comprobante + '\'' +
                ", serie_numero='" + serie_numero + '\'' +
                ", metodo_pago=" + (metodo_pago != null ? metodo_pago.toString() : "null") +
                ", fecha_emision=" + fecha_emision +
                ", estado='" + estado + '\'' +
                '}';
    }
}
