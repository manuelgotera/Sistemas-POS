/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.dao.interfaces.InsumoDAO;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;
/**
 *
 * @author HP
 */
public class Cliente extends Persona {

    private String tipoCliente; // natural o empresa
    private int puntosFideldiad;
    private String direccion; 
    private Date fecha_registro;
    
    public Cliente(){
        
    }

    public Cliente(String tipoCliente, int puntosFideldiad, String direccion, Date fecha_registro, String nombre, String apellido, String dni, String telefono, String email) {
        super(nombre, apellido, dni, telefono, email);
        this.tipoCliente = tipoCliente;
        this.puntosFideldiad = puntosFideldiad;
        this.direccion = direccion;
        this.fecha_registro = fecha_registro;
    }

    public Cliente(String tipoCliente, int puntosFideldiad, String direccion, Date fecha_registro, int id, String nombre, String apellido, String dni, String telefono, String email) {
        super(id, nombre, apellido, dni, telefono, email);
        this.tipoCliente = tipoCliente;
        this.puntosFideldiad = puntosFideldiad;
        this.direccion = direccion;
        this.fecha_registro = fecha_registro;
    }


    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public void setPuntosFideldiad(int puntosFideldiad) {
        this.puntosFideldiad = puntosFideldiad;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public int getPuntosFideldiad() {
        return puntosFideldiad;
    }

    public String getDireccion() {
        return direccion;
    }

    public Date getFecha_registro() {
        return fecha_registro;
    }
    
   @Override
    public String toString() {
        return "Cliente{" +
                "persona=" + super.toString() +
                ", tipoCliente='" + tipoCliente + '\'' +
                ", puntosFidelidad=" + puntosFideldiad +
                ", direccion='" + direccion + '\'' +
                ", fecha_registro=" + fecha_registro +
                '}';
    }

}